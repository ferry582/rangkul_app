package com.example.rangkul.data.repository

import com.example.rangkul.data.model.PostData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.utils.UiState

interface OptionsRepository {
    fun deletePost(post: PostData, result: (UiState<String>) -> Unit)
    fun getSessionData(result: (UserData?) -> Unit)
}