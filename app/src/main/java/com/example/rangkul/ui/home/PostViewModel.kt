package com.example.rangkul.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rangkul.data.model.PostData
import com.example.rangkul.data.repository.PostRepository
import com.example.rangkul.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(private val repository: PostRepository): ViewModel() {

    private val _posts = MutableLiveData<UiState<List<PostData>>>()
    val post: LiveData<UiState<List<PostData>>>
        get() = _posts


    fun getNotes(){
        _posts.value = UiState.Loading
        _posts.value = repository.getPosts()
    }

}