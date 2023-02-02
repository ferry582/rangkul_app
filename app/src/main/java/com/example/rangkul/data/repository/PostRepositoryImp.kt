package com.example.rangkul.data.repository

import android.content.SharedPreferences
import android.net.Uri
import com.example.rangkul.data.model.*
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
    private val storageReference: FirebaseStorage
): PostRepository {

    override fun getPosts(type: String, result: (UiState<List<PostData>>) -> Unit) {
        val postRef = database.collection(FirestoreCollection.POST)

        val document =
            // Only take posts based on the post's type
            if (type == "Anonymous") {
                postRef.whereEqualTo(FirestoreDocumentField.POST_TYPE, type)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
            } else {
                postRef.orderBy("createdAt", Query.Direction.DESCENDING)
            }

        document.get()
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

    override fun getCurrentUserPosts(type: String, uid: String, result: (UiState<List<PostData>>) -> Unit) {
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

    override fun addPost(post: PostData, result: (UiState<String>) -> Unit) {
        val document = database.collection(FirestoreCollection.POST).document()
        post.postId = document.id

        document.set(post)
            .addOnSuccessListener {
                addPostDataToUser(post) {state ->
                    when(state) {
                        is UiState.Success -> {
                            result.invoke(
                                UiState.Success("Post has been published")
                            )
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

    private fun addPostDataToUser(
        post: PostData,
        result: (UiState<String>) -> Unit
    ) {
        val userPostData = UserPostData(
            postId = post.postId,
            createdAt = post.createdAt
        )

        database.collection(FirestoreCollection.USER).document(post.createdBy)
            .collection(FirestoreCollection.POST).document(post.postId).set(userPostData)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Post data has been added in user document")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun getComments(postId: String, result: (UiState<List<CommentData>>) -> Unit) {
        database.collection(FirestoreCollection.POST).document(postId)
            .collection(FirestoreCollection.COMMENT)
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

    override fun addComment(comment: CommentData, postId: String, result: (UiState<String>) -> Unit) {
        val documentPost = database.collection(FirestoreCollection.POST).document(postId)
        val documentComment = documentPost.collection(FirestoreCollection.COMMENT).document()
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
                addCommentDataToUser(comment, postId) {state ->
                    when(state) {
                        is UiState.Success -> {
                            result.invoke(
                                UiState.Success("Comment has been published")
                            )
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

    private fun addCommentDataToUser(
        comment: CommentData,
        postId: String,
        result: (UiState<String>) -> Unit
    ) {
        val userCommentData = UserCommentData(
            commentId = comment.commentId,
            commentedAt = comment.commentedAt,
            postId = postId
        )

        database.collection(FirestoreCollection.USER).document(comment.commentedBy)
            .collection(FirestoreCollection.COMMENT).document(comment.commentId).set(userCommentData)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Comment data has been added in user document")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
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
        val post = documentPost.get()

        documentLike.get().addOnSuccessListener {
            // Add userId as documentId in Like collection if user haven't liked the post, else delete the document
            if (!it.exists()) {
                addLikeDataToUser(like, postId, currentUserId) {state ->
                    when(state) {
                        is UiState.Success -> {
                            val likesAmount = post.result.get(FirestoreDocumentField.LIKES_COUNT).toString().toLong().plus(1)
                            documentPost.update(FirestoreDocumentField.LIKES_COUNT, likesAmount)
                            documentLike.set(like)
                            result.invoke(
                                UiState.Success("Post Liked")
                            )
                        }

                        is UiState.Failure -> {
                            result.invoke(
                                UiState.Failure(state.error)
                            )
                        }
                        UiState.Loading -> {}
                    }
                }
            } else {
                deleteLikeDataAtUser(postId, currentUserId) {state ->
                    when(state) {
                        is UiState.Success -> {
                            val likesAmount = post.result.get(FirestoreDocumentField.LIKES_COUNT).toString().toLong().minus(1)
                            documentPost.update(FirestoreDocumentField.LIKES_COUNT, likesAmount)
                            documentLike.delete()
                            result.invoke(
                                UiState.Success("Post Unliked")
                            )
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
        result: (UiState<String>) -> Unit
    ) {
        database.collection(FirestoreCollection.USER).document(currentUserId)
            .collection(FirestoreCollection.LIKE).document(postId).set(like)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Like data has been added in user document")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    private fun deleteLikeDataAtUser(
        postId: String,
        currentUserId: String,
        result: (UiState<String>) -> Unit
    ) {
        database.collection(FirestoreCollection.USER).document(currentUserId)
            .collection(FirestoreCollection.LIKE).document(postId).delete()
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Like data has been removed from user document")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun getUserLikeData(currentUserId: String, result: (UiState<List<LikeData>>) -> Unit) {
        database.collection(FirestoreCollection.USER).document(currentUserId)
            .collection(FirestoreCollection.LIKE)
            .addSnapshotListener { value, error ->
                error?.let {
                    result.invoke(
                        UiState.Failure(it.localizedMessage)
                    )
                }

                value?.let {
                    val likes = arrayListOf<LikeData>()
                    for (document in it) {
                        val like = document.toObject(LikeData::class.java)
                        likes.add(like)
                    }
                    result.invoke(
                        UiState.Success(likes)
                    )
                }
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
}