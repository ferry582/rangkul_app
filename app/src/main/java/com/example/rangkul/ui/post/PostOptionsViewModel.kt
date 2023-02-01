package com.example.rangkul.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rangkul.data.model.PostData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.data.repository.OptionsRepository
import com.example.rangkul.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostOptionsViewModel @Inject constructor(private val repository: OptionsRepository): ViewModel() {

    private val _deletePost = MutableLiveData<UiState<String>>()
    val deletePost: LiveData<UiState<String>>
        get() = _deletePost

    fun deletePost(post: PostData){
        _deletePost.value = UiState.Loading
        repository.deletePost(post) {
            _deletePost.value = it
        }
    }

    fun getSessionData(result: (UserData?) -> Unit) {
        repository.getSessionData(result)
    }

}