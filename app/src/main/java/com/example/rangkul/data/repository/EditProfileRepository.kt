package com.example.rangkul.data.repository

import android.net.Uri
import com.example.rangkul.data.model.UserData
import com.example.rangkul.utils.UiState

interface EditProfileRepository {
    suspend fun uploadProfilePicture(fileUri: Uri, onResult: (UiState<Uri>) -> Unit)
    fun deleteProfilePicture(imageUrl: String, result: (UiState<String>) -> Unit)
    fun updateUserProfile(newUserData: UserData, isPhotoOrNameChange: Boolean, result: (UiState<String>) -> Unit)
    fun getSessionData(result: (UserData?) -> Unit)
}