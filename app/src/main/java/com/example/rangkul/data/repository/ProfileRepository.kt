package com.example.rangkul.data.repository

import com.example.rangkul.data.model.FollowData
import com.example.rangkul.data.model.ProfileCountData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.utils.UiState

interface ProfileRepository {
    fun getSessionData(result: (UserData?) -> Unit)
    fun getUserData(uid: String, result: (UiState<UserData?>) -> Unit)
    fun getProfileCountData(uid: String, postType: String, result: (UiState<ProfileCountData>) -> Unit)
    fun addFollowData(currentUId: String, followedUId: String, result: (UiState<String>) -> Unit)
    fun removeFollowData(currentUId: String, followedUId: String, result: (UiState<String>) -> Unit)
    fun getUserFollowingsData(uid: String, result: (UiState<List<FollowData>>) -> Unit)
}