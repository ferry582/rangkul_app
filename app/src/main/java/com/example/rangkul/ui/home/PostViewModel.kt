package com.example.rangkul.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rangkul.data.model.LikeData
import com.example.rangkul.data.model.PostData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.data.repository.PostRepository
import com.example.rangkul.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(private val repository: PostRepository): ViewModel() {

    private val _posts = MutableLiveData<UiState<List<PostData>>>()
    val post: LiveData<UiState<List<PostData>>>
        get() = _posts

    fun getPosts(){
        _posts.value = UiState.Loading
        repository.getPosts {
            _posts.value = it
        }
    }

    private val _getIsPostLiked = MutableLiveData<UiState<Boolean>>()
    val getIsPostLiked: LiveData<UiState<Boolean>>
        get() = _getIsPostLiked

    fun getIsPostLiked(postId: String, currentUserId: String){
        repository.getIsPostLiked(postId, currentUserId) {
            _getIsPostLiked.value = it
        }
    }

    private val _addLike = MutableLiveData<UiState<String>>()
    val addLike: LiveData<UiState<String>>
        get() = _addLike

    fun addLike(like: LikeData, postId: String, currentUserId: String){
        _addLike.value = UiState.Loading
        repository.addLike(like, postId, currentUserId) {
            _addLike.value = it
        }
    }

    fun getSessionData(result: (UserData?) -> Unit) {
        repository.getSessionData(result)
    }

}