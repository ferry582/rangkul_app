package com.example.rangkul.ui.createpost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rangkul.data.model.PostData
import com.example.rangkul.data.repository.PostRepository
import com.example.rangkul.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(private val repository: PostRepository): ViewModel() {

    private val _addPost = MutableLiveData<UiState<String>>()
    val addPost: LiveData<UiState<String>>
        get() = _addPost

    fun addPost(post: PostData){
        _addPost.value = UiState.Loading
        repository.addPost(post) {
            _addPost.value = it
        }
    }

}