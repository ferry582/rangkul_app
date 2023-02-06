package com.example.rangkul.data.repository

import android.net.Uri
import com.example.rangkul.data.model.DiaryData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.utils.UiState

interface ProfileRepository {
    fun getSessionData(result: (UserData?) -> Unit)
    fun getVisitedUserData(uid: String, result: (UiState<UserData?>) -> Unit)
}