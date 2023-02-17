package com.example.rangkul.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rangkul.data.model.ProfileCountData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.data.repository.ProfileRepository
import com.example.rangkul.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: ProfileRepository): ViewModel() {

    private val _getUserData = MutableLiveData<UiState<UserData?>>()
    val getUserData: LiveData<UiState<UserData?>>
        get() = _getUserData

    private val _getProfileCountData = MutableLiveData<UiState<ProfileCountData>>()
    val getProfileCountData: LiveData<UiState<ProfileCountData>>
        get() = _getProfileCountData

    private val _getUserDataList = MutableLiveData<UiState<List<UserData>>>()
    val getUserDataList: LiveData<UiState<List<UserData>>>
        get() = _getUserDataList

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

    fun addFollowData(currentUId: String, followedUId: String, result: (UiState<String>) -> Unit) {
        repository.addFollowData(currentUId, followedUId) {
            result.invoke(it)
        }
    }

    fun removeFollowData(currentUId: String, followedUId: String, result: (UiState<String>) -> Unit) {
        repository.removeFollowData(currentUId, followedUId) {
            result.invoke(it)
        }
    }

    fun getUserDataList(uid: String, type: String){
        _getUserDataList.value = UiState.Loading
        repository.getUserDataList(uid, type) {
            _getUserDataList.value = it
        }
    }

    fun isUserBeingFollowed(currentUId: String, targetUId: String, result: (Boolean) -> Unit){
        repository.isUserBeingFollowed(currentUId, targetUId) {
            result.invoke(it)
        }
    }

    fun getSessionData(result: (UserData?) -> Unit) {
        repository.getSessionData(result)
    }

}