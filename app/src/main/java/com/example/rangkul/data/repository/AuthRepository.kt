package com.example.rangkul.data.repository

import com.example.rangkul.data.model.UserData
import com.example.rangkul.utils.UiState

interface AuthRepository {
    fun registerUser(email: String, password: String, user: UserData, result: (UiState<String>) -> Unit)
    fun loginUser(email: String, password: String, result: (UiState<String>) -> Unit)
    fun forgotPassword(email: String, result: (UiState<String>) -> Unit)
    fun logOut(result: () -> Unit)
    fun storeSession(id: String, result: (UserData?) -> Unit)
    fun getSession(result: (UserData?) -> Unit)
}