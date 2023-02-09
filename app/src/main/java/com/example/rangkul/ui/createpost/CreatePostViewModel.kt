package com.example.rangkul.ui.createpost

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rangkul.data.model.DiaryData
import com.example.rangkul.data.model.PostData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.data.repository.PostRepository
import com.example.rangkul.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(private val repository: PostRepository): ViewModel() {

    private val _addPost = MutableLiveData<UiState<String>>()
    val addPost: LiveData<UiState<String>>
        get() = _addPost

    private val _addDiary = MutableLiveData<UiState<String>>()
    val addDiary: LiveData<UiState<String>>
        get() = _addDiary

    private val _getProfanityCheck = MutableLiveData<UiState<String>>()
    val getProfanityCheck: LiveData<UiState<String>>
        get() = _getProfanityCheck


    fun addPost(post: PostData){
        _addPost.value = UiState.Loading
        repository.addPost(post) {
            _addPost.value = it
        }
    }

    fun addDiary(diary: DiaryData){
        _addDiary.value = UiState.Loading
        repository.addDiary(diary) {
            _addDiary.value = it
        }
    }

    fun getSessionData(result: (UserData?) -> Unit) {
        repository.getSessionData(result)
    }

    fun onUploadPostImage(fileUri: Uri, onResult: (UiState<Uri>) -> Unit) {
        onResult.invoke(UiState.Loading)
        viewModelScope.launch {
            repository.uploadPostImage(fileUri, onResult)
        }
    }

    fun getProfanityCheck(caption: String) {
        _getProfanityCheck.value = UiState.Loading
        viewModelScope.launch {
            repository.getProfanityCheck(caption) {
                _getProfanityCheck.value = it
            }
        }
    }

}