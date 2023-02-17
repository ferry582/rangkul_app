package com.example.rangkul.data.repository

import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.example.rangkul.data.model.*
import com.example.rangkul.data.retrofit.ApiInterface
import com.example.rangkul.utils.*
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class PostRepositoryImp(
    private val database: FirebaseFirestore,
    private val appPreferences: SharedPreferences,
    private val gson: Gson,
    private val storageReference: FirebaseStorage,
    private val retrofitInstance: ApiInterface
): PostRepository {

    private val TAG = "PostRepositoryImp"

    override fun getPosts(type: String, result: (UiState<List<PostData>>) -> Unit) {
        val postRef = database.collection(FirestoreCollection.POST)

        val documentRef =
            // Only take posts based on the post's type
            if (type == "Anonymous") {
                postRef.whereEqualTo(FirestoreDocumentField.POST_TYPE, type)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
            } else {
                postRef.orderBy("createdAt", Query.Direction.DESCENDING)
            }

        documentRef.get()
            .addOnSuccessListener {
                val posts = arrayListOf<PostData>()
                    for (document in it) {
                        val post = document.toObject(PostData::class.java)
                        posts.add(post)
                    }
                    result.invoke(
                        UiState.Success(posts)
                    )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun getUserPosts(type: String, uid: String, result: (UiState<List<PostData>>) -> Unit) {
        // Only take posts that belongs to current user
        database.collection(FirestoreCollection.POST)
            .whereEqualTo(FirestoreDocumentField.POST_CREATED_BY, uid)
            .whereEqualTo(FirestoreDocumentField.POST_TYPE, type)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                val posts = arrayListOf<PostData>()
                for (document in it) {
                    val post = document.toObject(PostData::class.java)
                    posts.add(post)
                }
                result.invoke(
                    UiState.Success(posts)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun getPostsWithCategory(category: String, result: (UiState<List<PostData>>) -> Unit) {
        // Only take posts based on the post's category
        database.collection(FirestoreCollection.POST)
            .whereEqualTo(FirestoreDocumentField.POST_CATEGORY, category)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                val posts = arrayListOf<PostData>()
                for (document in it) {
                    val post = document.toObject(PostData::class.java)
                    posts.add(post)
                }
                result.invoke(
                    UiState.Success(posts)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun getUserDiaries(uid: String, result: (UiState<List<DiaryData>>) -> Unit) {
        database.collection(FirestoreCollection.USER)
            .document(uid)
            .collection(FirestoreCollection.DIARY)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                val diaries = arrayListOf<DiaryData>()
                for (document in it) {
                    val diary = document.toObject(DiaryData::class.java)
                    diaries.add(diary)
                }
                result.invoke(
                    UiState.Success(diaries)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun addPost(post: PostData, result: (UiState<String>) -> Unit) {
        val document = database.collection(FirestoreCollection.POST).document()
        post.postId = document.id

        document.set(post)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Post has been published")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun addDiary(diary: DiaryData, result: (UiState<String>) -> Unit) {
        val document = database.collection(FirestoreCollection.USER).document(diary.createdBy)
            .collection(FirestoreCollection.DIARY).document()
        diary.diaryId = document.id

        document.set(diary)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Diary has been published")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun getComments(postId: String, result: (UiState<List<CommentData>>) -> Unit) {
        database.collection(FirestoreCollection.COMMENT)
            .whereEqualTo(FirestoreDocumentField.POST_ID, postId)
            .orderBy("commentedAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                val comments = arrayListOf<CommentData>()
                for (document in it) {
                    val comment = document.toObject(CommentData::class.java)
                    comments.add(comment)
                }
                result.invoke(
                    UiState.Success(comments)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun addComment(comment: CommentData, result: (UiState<String>) -> Unit) {
        val documentPost = database.collection(FirestoreCollection.POST).document(comment.postId)
        val documentComment = database.collection(FirestoreCollection.COMMENT).document()
        comment.commentId = documentComment.id

        database.runTransaction { transaction ->
            // Update Comment Count in Posts Collection
            val post = transaction.get(documentPost)
            val commentsAmount = post.getLong(FirestoreDocumentField.COMMENTS_COUNT)?.plus(1)
            transaction.update(documentPost, FirestoreDocumentField.COMMENTS_COUNT, commentsAmount)
            // Add Comment to Comments Collection
            transaction.set(documentComment, comment)
        }
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Comment has been published")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun addLike(
        like: LikeData,
        postId: String,
        currentUserId: String,
        result: (UiState<String>) -> Unit
    ) {
        val documentPost = database.collection(FirestoreCollection.POST).document(postId)
        val documentLike = documentPost.collection(FirestoreCollection.LIKE).document(currentUserId)
        val getPost = documentPost.get()

        documentLike.get().addOnSuccessListener {
            // Add userId as documentId in Like collection if user haven't liked the post, else delete the document
            if (!it.exists()) {
                addLikeDataToUser(like, postId, currentUserId).apply {
                    val likesAmount = getPost.result.get(FirestoreDocumentField.LIKES_COUNT).toString().toLong().plus(1)
                    documentPost.update(FirestoreDocumentField.LIKES_COUNT, likesAmount)
                    documentLike.set(like)
                        .addOnSuccessListener {
                            result.invoke(UiState.Success("Liked"))
                        }
                        .addOnFailureListener { error ->
                            result.invoke(UiState.Failure(error.localizedMessage))
                        }

                }
            } else {
                deleteLikeDataAtUser(postId, currentUserId).apply {
                    val likesAmount = getPost.result.get(FirestoreDocumentField.LIKES_COUNT).toString().toLong().minus(1)
                    documentPost.update(FirestoreDocumentField.LIKES_COUNT, likesAmount)
                    documentLike.delete()
                        .addOnSuccessListener {
                            result.invoke(UiState.Success("Unliked"))
                        }
                        .addOnFailureListener { error ->
                            result.invoke(UiState.Failure(error.localizedMessage))
                        }
                }
            }
        }.addOnFailureListener {
            result.invoke(
                UiState.Failure(it.localizedMessage)
            )
        }
    }

    private fun addLikeDataToUser(
        like: LikeData,
        postId: String,
        currentUserId: String,
    ) {
        database.collection(FirestoreCollection.USER).document(currentUserId)
            .collection(FirestoreCollection.LIKE).document(postId).set(like)
            .addOnSuccessListener {
                Log.w(TAG, "Like data has been added in user document")
            }
            .addOnFailureListener {
                it.localizedMessage?.let { it1 -> Log.e(TAG, it1) }
            }
    }

    private fun deleteLikeDataAtUser(
        postId: String,
        currentUserId: String,
    ) {
        database.collection(FirestoreCollection.USER).document(currentUserId)
            .collection(FirestoreCollection.LIKE).document(postId).delete()
            .addOnSuccessListener {
                Log.w(TAG, "Like data has been removed from user document")
            }
            .addOnFailureListener {
                it.localizedMessage?.let { it1 -> Log.e(TAG, it1) }
            }
    }

    override fun isPostBeingLiked(currentUId: String, postId: String, result: (Boolean) -> Unit) {
        database.collection(FirestoreCollection.POST).document(postId)
            .collection(FirestoreCollection.LIKE).document(currentUId)
            .get()
            .addOnSuccessListener {
                result.invoke(it.exists())
            }
            .addOnFailureListener {
                it.localizedMessage?.let { it1 -> Log.e(TAG, it1) }
            }
        Log.w(TAG, "isPostBeingLiked")
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

    override suspend fun uploadPostImage(fileUri: Uri, onResult: (UiState<Uri>) -> Unit) {
        try {
            val uri: Uri = withContext(Dispatchers.IO) {
                storageReference
                    .getReference(FirebaseStorageConstants.ROOT_DIRECTORY)
                    .child("${FirebaseStorageConstants.POST_IMAGE}/${UUID.randomUUID()}")
                    .putFile(fileUri)
                    .await()
                    .storage
                    .downloadUrl
                    .await()
            }
            onResult.invoke(
                UiState.Success(uri)
            )
        } catch (e: FirebaseException) {
            onResult.invoke(
                UiState.Failure(e.message)
            )
        } catch (e: Exception) {
            onResult.invoke(
                UiState.Failure(e.message)
            )
        }
    }

    override suspend fun getProfanityCheck(
        caption: String,
        result: (UiState<String>) -> Unit
    ){
        val response = retrofitInstance.getProfanityCheck(caption)

        if (response.isSuccessful) {
            result.invoke(
                UiState.Success(response.body().toString())
            )
        } else {
            UiState.Failure(
                response.errorBody().toString()
            )
        }
    }
}