package com.example.rangkul.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.rangkul.data.model.*
import com.example.rangkul.utils.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson

class OptionsRepositoryImp(
    private val database: FirebaseFirestore,
    private val appPreferences: SharedPreferences,
    private val gson: Gson,
    private val storageReference: FirebaseStorage
): OptionsRepository {

    private val TAG = "OptionsRepositoryImp"

    override fun deletePost( post: PostData, result: (UiState<String>) -> Unit) {
        deleteAllComments(post.postId) {
            when (it) {
                is UiState.Failure -> {
                    Log.e("deletePost", it.error.toString())
                }

                is UiState.Success -> {
                    Log.w("deletePost", it.data)
                }
                UiState.Loading -> {}
            }
        }

        deleteLikesSubCollection(post.postId) {
            when (it) {
                is UiState.Failure -> {
                    Log.e("deletePost", it.error.toString())
                }

                is UiState.Success -> {
                    Log.w("deletePost", it.data)
                }
                UiState.Loading -> {}
            }
        }

        if (!post.image.isNullOrEmpty()) {
            deletePostImage(post.image) {
                when (it) {
                    is UiState.Failure -> {
                        Log.e("deletePost", it.error.toString())
                    }

                    is UiState.Success -> {
                        Log.w("deletePost", it.data)
                    }
                    UiState.Loading -> {}
                }
            }
        }

        // Delete post document
        database.collection(FirestoreCollection.POST)
            .document(post.postId)
            .delete()
            .addOnSuccessListener {
                result.invoke(UiState.Success("Post has been deleted"))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    private fun deleteAllComments(postId: String, result: (UiState<String>) -> Unit){
        val ref =
            database.collection(FirestoreCollection.COMMENT)
            .whereEqualTo(FirestoreDocumentField.POST_ID, postId)

        ref.get()
            .addOnSuccessListener {
                val batch: WriteBatch = database.batch()
                for (document in it) {
                    batch.delete(document.reference)
                }
                batch.commit()
                    .addOnSuccessListener {
                        result.invoke(
                            UiState.Success("Delete all comments successful")
                        )
                    }
                    .addOnFailureListener {
                        result.invoke(
                            UiState.Failure(it.localizedMessage)
                        )
                    }
            }.addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    private fun deleteLikesSubCollection(postId: String, result: (UiState<String>) -> Unit){
        // Note: to delete entire collection, would be better to use callable cloud function
        // https://firebase.google.com/docs/firestore/solutions/delete-collections

        val ref = database.collection(FirestoreCollection.POST).document(postId)
            .collection(FirestoreCollection.LIKE)

        ref.get()
            .addOnSuccessListener {
                val batch: WriteBatch = database.batch()
                for (document in it) {
                    batch.delete(document.reference)
                }
                batch.commit()
                    .addOnSuccessListener {
                        result.invoke(
                            UiState.Success("Delete likes sub-collection successful")
                        )
                    }
                    .addOnFailureListener {
                        result.invoke(
                            UiState.Failure(it.localizedMessage)
                        )
                    }
            }.addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    private fun deletePostImage(imageUrl: String, result: (UiState<String>) -> Unit){
        storageReference.getReferenceFromUrl(imageUrl).delete()
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Post image has been deleted")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun getSessionData(result: (UserData?) -> Unit) {
        val userStr = appPreferences.getString(SharedPrefConstants.USER_SESSION, null)
        if (userStr == null) {
            result.invoke(null)
        } else {
            val user = gson.fromJson(userStr, UserData::class.java)
            result.invoke(user)
        }
    }

    override fun deleteComment( comment: CommentData, result: (UiState<String>) -> Unit) {
        val documentPost = database.collection(FirestoreCollection.POST).document(comment.postId)

        database.runTransaction {transaction ->
            // Update Comment Count in Posts Collection
            val post = transaction.get(documentPost)
            val commentsAmount = post.getLong(FirestoreDocumentField.COMMENTS_COUNT)?.minus(1)
            transaction.update(documentPost, FirestoreDocumentField.COMMENTS_COUNT, commentsAmount)
        }
            .addOnSuccessListener {
                // Delete comment document
                database.collection(FirestoreCollection.COMMENT).document(comment.commentId)
                    .delete()
                    .addOnSuccessListener {
                        result.invoke(UiState.Success("Comment has been deleted"))
                    }
                    .addOnFailureListener {
                        result.invoke(UiState.Failure(it.localizedMessage))
                    }

            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun deleteDiary(diary: DiaryData, result: (UiState<String>) -> Unit) {
        // Delete post document
        database.collection(FirestoreCollection.USER).document(diary.createdBy)
            .collection(FirestoreCollection.DIARY)
            .document(diary.diaryId)
            .delete()
            .addOnSuccessListener {
                result.invoke(UiState.Success("Diary has been deleted"))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    override fun addReportData(report: ReportData, result: (UiState<Boolean>) -> Unit) {
        isUserAlreadyReport(report) {
            if (it) {
                result.invoke(UiState.Success(true))
            } else {
                val document = database.collection(FirestoreCollection.REPORTS).document()
                report.reportId = document.id

                document.set(report)
                    .addOnSuccessListener {
                        result.invoke(UiState.Success(false))
                    }
                    .addOnFailureListener { e ->
                        result.invoke(
                            UiState.Failure(e.localizedMessage)
                        )
                    }
            }
        }
    }

    private fun isUserAlreadyReport(report: ReportData, result: (Boolean) -> Unit) {
        database.collection(FirestoreCollection.REPORTS)
            .whereEqualTo(FirestoreDocumentField.REPORTED_BY, report.reportedBy)
            .whereEqualTo(FirestoreDocumentField.REPORTED_ID, report.reportedId)
            .whereEqualTo(FirestoreDocumentField.REPORT_REASON, report.reason)
            .get()
            .addOnSuccessListener {
                result.invoke(!it.isEmpty)
            }
            .addOnFailureListener {
                it.localizedMessage?.let { it1 -> Log.e(TAG, it1) }
            }
    }

}