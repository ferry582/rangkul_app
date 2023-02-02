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

    override fun deletePost( post: PostData, result: (UiState<String>) -> Unit) {
        deleteCommentsSubCollection(post.postId) {
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

        deletePostDataAtUser(post.postId, post.createdBy) {
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

        if (post.image != "null") {
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

    private fun deleteCommentsSubCollection(postId: String, result: (UiState<String>) -> Unit){
        // Note: to delete entire collection, would be better to use callable cloud function
        // https://firebase.google.com/docs/firestore/solutions/delete-collections

        val ref = database.collection(FirestoreCollection.POST).document(postId)
            .collection(FirestoreCollection.COMMENT)

        ref.get()
            .addOnSuccessListener {
                val batch: WriteBatch = database.batch()
                for (document in it) {
                    batch.delete(document.reference)
                }
                batch.commit()
                    .addOnSuccessListener {
                        result.invoke(
                            UiState.Success("Delete comments sub-collection successful")
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

    private fun deletePostDataAtUser(postId: String, userId: String, result: (UiState<String>) -> Unit){
        database.collection(FirestoreCollection.USER).document(userId)
            .collection(FirestoreCollection.POST).document(postId).delete()
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Post data at user collection has been deleted")
                )
            }
            .addOnFailureListener {
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

    override fun deleteComment( postId: String, comment: CommentData, result: (UiState<String>) -> Unit) {
        val documentPost = database.collection(FirestoreCollection.POST).document(postId)

        database.runTransaction {transaction ->
            // Update Comment Count in Posts Collection
            val post = transaction.get(documentPost)
            val commentsAmount = post.getLong(FirestoreDocumentField.COMMENTS_COUNT)?.minus(1)
            transaction.update(documentPost, FirestoreDocumentField.COMMENTS_COUNT, commentsAmount)
        }
            .addOnSuccessListener {
                // Delete comment data at user
                deleteCommentDataAtUser(comment.commentId, comment.commentedBy) { state ->
                    when(state) {
                        is UiState.Success -> {
                            // Delete comment document
                            database.collection(FirestoreCollection.POST).document(postId)
                                .collection(FirestoreCollection.COMMENT).document(comment.commentId)
                                .delete()
                                .addOnSuccessListener {
                                    result.invoke(UiState.Success("Comment has been deleted"))
                                }
                                .addOnFailureListener {
                                    result.invoke(UiState.Failure(it.localizedMessage))
                                }
                        }

                        is UiState.Failure -> {
                            result.invoke(
                                UiState.Failure(state.error)
                            )
                        }
                        UiState.Loading -> {}
                    }
                }
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    private fun deleteCommentDataAtUser(commentId : String, userId: String, result: (UiState<String>) -> Unit){
        database.collection(FirestoreCollection.USER).document(userId)
            .collection(FirestoreCollection.COMMENT).document(commentId).delete()
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Comment data at user collection has been deleted")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

}