package com.example.rangkul.data.repository

import com.example.rangkul.data.model.PostData
import com.example.rangkul.utils.UiState

interface PostRepository {
    fun getPosts(result: (UiState<List<PostData>>) -> Unit)
    fun addPost(post: PostData, result: (UiState<String>) -> Unit)

}