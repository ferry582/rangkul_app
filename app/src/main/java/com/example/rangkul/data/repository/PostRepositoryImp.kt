package com.example.rangkul.data.repository

import com.example.rangkul.data.model.PostData
import com.example.rangkul.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore

class PostRepositoryImp(val database: FirebaseFirestore): PostRepository {

    override fun getPosts(): UiState<List<PostData>> {

        val data = arrayListOf(
            PostData(
                "test", "test", "test", "test", "test", "test", "test", "test"
            )
        )

        return if (data.isNullOrEmpty()) {
            UiState.Failure("Data is Empty")
        } else {
            UiState.Success(data)
        }
    }
}