package com.example.rangkul.data.repository

import android.content.SharedPreferences
import com.example.rangkul.data.model.CommentData
import com.example.rangkul.data.model.LikeData
import com.example.rangkul.data.model.PostData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.utils.FirestoreCollection
import com.example.rangkul.utils.FirestoreDocumentField
import com.example.rangkul.utils.SharedPrefConstants
import com.example.rangkul.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson

class PostRepositoryImp(
    private val database: FirebaseFirestore,
    private val appPreferences: SharedPreferences,
    private val gson: Gson
): PostRepository {

    override fun getPosts(result: (UiState<List<PostData>>) -> Unit) {
        database.collection(FirestoreCollection.POST).orderBy("createdAt", Query.Direction.DESCENDING).get()
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

    override fun getComments(postId: String, result: (UiState<List<CommentData>>) -> Unit) {
        database.collection(FirestoreCollection.POST).document(postId)
            .collection(FirestoreCollection.COMMENT).orderBy("commentedAt", Query.Direction.DESCENDING).get()
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

        database.runTransaction {transaction ->

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

    override fun getIsPostLiked(
        postId: String,
        currentUserId: String,
        result: (UiState<Boolean>) -> Unit
    ) {
        val documentPost = database.collection(FirestoreCollection.POST).document(postId)
        val documentLike = documentPost.collection(FirestoreCollection.LIKE).document(currentUserId)

        documentLike.get().addOnSuccessListener {
            if (it.exists()) {
                result.invoke(
                    UiState.Success(true)
                )
            } else {
                result.invoke(
                    UiState.Success(false)
                )
            }
        }.addOnFailureListener {
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
        val post = documentPost.get()

        documentLike.get().addOnSuccessListener {
            // Add userId as documentId in Like collection if user haven't liked the post, else delete the document
            if (!it.exists()) {
                val likesAmount = post.result.get(FirestoreDocumentField.LIKES_COUNT).toString().toLong().plus(1)
                documentPost.update(FirestoreDocumentField.LIKES_COUNT, likesAmount)

                documentLike.set(like)
                result.invoke(
                    UiState.Success("Post Liked")
                )
            } else {
                val likesAmount = post.result.get(FirestoreDocumentField.LIKES_COUNT).toString().toLong().minus(1)
                documentPost.update(FirestoreDocumentField.LIKES_COUNT, likesAmount)

                documentLike.delete()
                result.invoke(
                    UiState.Success("Post Unliked")
                )
            }
        }.addOnFailureListener {
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
}