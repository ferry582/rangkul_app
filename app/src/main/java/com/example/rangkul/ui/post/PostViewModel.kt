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

    private val _getPosts = MutableLiveData<UiState<List<PostData>>>()
    val getPosts: LiveData<UiState<List<PostData>>>
        get() = _getPosts

    private val _getUserPosts = MutableLiveData<UiState<List<PostData>>>()
    val getUserPosts: LiveData<UiState<List<PostData>>>
        get() = _getUserPosts

    private val _getPostsWithCategory = MutableLiveData<UiState<List<PostData>>>()
    val getPostsWithCategory: LiveData<UiState<List<PostData>>>
        get() = _getPostsWithCategory

    private val _getLikedPosts = MutableLiveData<UiState<List<PostData>>>()
    val getLikedPosts: LiveData<UiState<List<PostData>>>
        get() = _getLikedPosts

    private val _getUserDiaries = MutableLiveData<UiState<List<DiaryData>>>()
    val getUserDiaries: LiveData<UiState<List<DiaryData>>>
        get() = _getUserDiaries

    fun getPosts(type: String, uid: String){
        _getPosts.value = UiState.Loading
        repository.getPosts(type, uid) {
            _getPosts.value = it
        }
    }

    fun getUserPosts(type: String, uid: String,){
        _getUserPosts.value = UiState.Loading
        repository.getUserPosts(type, uid) {
            _getUserPosts.value = it
        }
    }

    fun getPostsWithCategory(category: String){
        _getPostsWithCategory.value = UiState.Loading
        repository.getPostsWithCategory(category) {
            _getPostsWithCategory.value = it
        }
    }

    fun getLikedPosts(category: String){
        _getLikedPosts.value = UiState.Loading
        repository.getLikedPosts(category) {
            _getLikedPosts.value = it
        }
    }

    fun getUserDiaries(uid: String,){
        _getUserDiaries.value = UiState.Loading
        repository.getUserDiaries(uid) {
            _getUserDiaries.value = it
        }
    }

    fun addLike(
        like: LikeData,
        postId: String,
        currentUserId: String,
        result: (UiState<String>) -> Unit){
        repository.addLike(like, postId, currentUserId) {
           result.invoke(it)
        }
    }

    fun isPostBeingLiked(currentUserId: String, postId: String, result: (Boolean) -> Unit){
        repository.isPostBeingLiked(currentUserId, postId) {
            result.invoke(it)
        }
    }

    fun getSessionData(result: (UserData?) -> Unit) {
        repository.getSessionData(result)
    }

}