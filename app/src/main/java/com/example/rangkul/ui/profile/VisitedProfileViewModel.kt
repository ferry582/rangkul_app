package com.example.rangkul.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rangkul.data.model.UserData
import com.example.rangkul.data.repository.ProfileRepository
import com.example.rangkul.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VisitedProfileViewModel @Inject constructor(private val repository: ProfileRepository): ViewModel() {

    private val _getVisitedUserData = MutableLiveData<UiState<UserData?>>()
    val getVisitedUserData: LiveData<UiState<UserData?>>
        get() = _getVisitedUserData

    fun getVisitedUserData(uid: String) {
        _getVisitedUserData.value = UiState.Loading
        repository.getVisitedUserData(uid) {
            _getVisitedUserData.value = it
        }
    }

    fun getSessionData(result: (UserData?) -> Unit) {
        repository.getSessionData(result)
    }

}