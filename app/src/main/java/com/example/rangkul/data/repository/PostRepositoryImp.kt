package com.example.rangkul.data.repository

import com.example.rangkul.data.model.CommentData
import com.example.rangkul.data.model.PostData
import com.example.rangkul.utils.FirestoreTables
import com.example.rangkul.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PostRepositoryImp(private val database: FirebaseFirestore): PostRepository {

    override fun getPosts(result: (UiState<List<PostData>>) -> Unit) {
        database.collection(FirestoreTables.POST).orderBy("createdAt", Query.Direction.DESCENDING).get()
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
        val document = database.collection(FirestoreTables.POST).document()
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
        database.collection(FirestoreTables.POST).document(postId)
            .collection(FirestoreTables.COMMENT).orderBy("commentedAt", Query.Direction.DESCENDING).get()
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
        val documentPost = database.collection(FirestoreTables.POST).document(postId)
        val documentComment = database.collection(FirestoreTables.POST).document(postId)
            .collection(FirestoreTables.COMMENT).document()
        comment.commentId = documentComment.id

        database.runTransaction {transaction ->

            // Update Comment Count in Posts Collection
            val post = transaction.get(documentPost)
            val commentsAmount = post.getLong(FirestoreTables.COMMENTS_COUNT)?.plus(1)
            transaction.update(documentPost, FirestoreTables.COMMENTS_COUNT, commentsAmount)

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
}