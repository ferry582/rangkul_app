package com.example.rangkul.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rangkul.data.model.DiaryData
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
    val getPosts: LiveData<UiState<List<PostData>>>
        get() = _posts

    fun getPosts(type: String){
        _posts.value = UiState.Loading
        repository.getPosts(type) {
            _posts.value = it
        }
    }

    private val _getCurrentUserPosts = MutableLiveData<UiState<List<PostData>>>()
    val getCurrentUserPosts: LiveData<UiState<List<PostData>>>
        get() = _getCurrentUserPosts

    fun getCurrentUserPosts(type: String, uid: String,){
        _getCurrentUserPosts.value = UiState.Loading
        repository.getCurrentUserPosts(type, uid) {
            _getCurrentUserPosts.value = it
        }
    }

    private val _getPostsWithCategory = MutableLiveData<UiState<List<PostData>>>()
    val getPostsWithCategory: LiveData<UiState<List<PostData>>>
        get() = _getPostsWithCategory

    fun getPostsWithCategory(category: String){
        _getPostsWithCategory.value = UiState.Loading
        repository.getPostsWithCategory(category) {
            _getPostsWithCategory.value = it
        }
    }

    private val _getUserDiaries = MutableLiveData<UiState<List<DiaryData>>>()
    val getUserDiaries: LiveData<UiState<List<DiaryData>>>
        get() = _getUserDiaries

    fun getUserDiaries(uid: String,){
        _getUserDiaries.value = UiState.Loading
        repository.getUserDiaries(uid) {
            _getUserDiaries.value = it
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

    private val _getUserLikeData = MutableLiveData<UiState<List<LikeData>>>()
    val getUserLikeData: LiveData<UiState<List<LikeData>>>
        get() = _getUserLikeData

    fun getUserLikeData(currentUserId: String){
        _getUserLikeData.value = UiState.Loading
        repository.getUserLikeData(currentUserId) {
            _getUserLikeData.value = it
        }
    }

    fun getSessionData(result: (UserData?) -> Unit) {
        repository.getSessionData(result)
    }

}