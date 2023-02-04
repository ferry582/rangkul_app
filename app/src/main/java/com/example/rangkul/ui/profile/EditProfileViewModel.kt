package com.example.rangkul.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rangkul.data.model.UserData
import com.example.rangkul.data.repository.EditProfileRepository
import com.example.rangkul.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(private val repository: EditProfileRepository): ViewModel() {

    private val _updateUserProfile = MutableLiveData<UiState<String>>()
    val updateUserProfile: LiveData<UiState<String>>
        get() = _updateUserProfile

    fun updateUserProfile(user: UserData, isPhotoOrNameChange: Boolean){
        _updateUserProfile.value = UiState.Loading
        repository.updateUserProfile(user, isPhotoOrNameChange) {
            _updateUserProfile.value = it
        }
    }

    fun onUploadProfilePicture(fileUri: Uri, onResult: (UiState<Uri>) -> Unit) {
        onResult.invoke(UiState.Loading)
        viewModelScope.launch {
            repository.uploadProfilePicture(fileUri, onResult)
        }
    }

    fun deleteProfilePicture(imageUrl: String, result: (UiState<String>) -> Unit) {
        result.invoke(UiState.Loading)
        repository.deleteProfilePicture(imageUrl, result)
    }

    fun getSessionData(result: (UserData?) -> Unit) {
        repository.getSessionData(result)
    }

}