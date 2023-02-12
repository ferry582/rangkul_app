package com.example.rangkul.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rangkul.data.model.FollowData
import com.example.rangkul.data.model.ProfileCountData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.data.repository.ProfileRepository
import com.example.rangkul.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VisitedProfileViewModel @Inject constructor(private val repository: ProfileRepository): ViewModel() {

    private val _getUserData = MutableLiveData<UiState<UserData?>>()
    val getUserData: LiveData<UiState<UserData?>>
        get() = _getUserData

    private val _getProfileCountData = MutableLiveData<UiState<ProfileCountData>>()
    val getProfileCountData: LiveData<UiState<ProfileCountData>>
        get() = _getProfileCountData

    private val _addFollowData = MutableLiveData<UiState<String>>()
    val addFollowData: LiveData<UiState<String>>
        get() = _addFollowData

    private val _removeFollowData = MutableLiveData<UiState<String>>()
    val removeFollowData: LiveData<UiState<String>>
        get() = _removeFollowData

    private val _getUserFollowingsData = MutableLiveData<UiState<List<FollowData>>>()
    val getUserFollowingsData: LiveData<UiState<List<FollowData>>>
        get() = _getUserFollowingsData

    fun getUserData(uid: String) {
        _getUserData.value = UiState.Loading
        repository.getUserData(uid) {
            _getUserData.value = it
        }
    }

    fun getProfileCountData(uid: String, postType: String) {
        _getProfileCountData.value = UiState.Loading
        repository.getProfileCountData(uid, postType) {
            _getProfileCountData.value = it
        }
    }

    fun addFollowData(currentUId: String, followedUId: String) {
        _addFollowData.value = UiState.Loading
        repository.addFollowData(currentUId, followedUId) {
            _addFollowData.value = it
        }
    }

    fun removeFollowData(currentUId: String, followedUId: String) {
        _removeFollowData.value = UiState.Loading
        repository.removeFollowData(currentUId, followedUId) {
            _removeFollowData.value = it
        }
    }

    fun getUserFollowingsData(currentUserId: String){
        _getUserFollowingsData.value = UiState.Loading
        repository.getUserFollowingsData(currentUserId) {
            _getUserFollowingsData.value = it
        }
    }

    fun getSessionData(result: (UserData?) -> Unit) {
        repository.getSessionData(result)
    }

}