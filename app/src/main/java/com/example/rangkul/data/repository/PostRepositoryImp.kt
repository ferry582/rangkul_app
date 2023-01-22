package com.example.rangkul.data.repository

import com.example.rangkul.data.model.PostData
import com.example.rangkul.utils.FirestoreTables
import com.example.rangkul.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PostRepositoryImp(val database: FirebaseFirestore): PostRepository {

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
}