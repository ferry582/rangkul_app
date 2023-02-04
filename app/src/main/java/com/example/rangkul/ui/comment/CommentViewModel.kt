package com.example.rangkul.ui.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rangkul.data.model.CommentData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.data.repository.PostRepository
import com.example.rangkul.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(private val repository: PostRepository): ViewModel() {

    private val _getComments = MutableLiveData<UiState<List<CommentData>>>()
    val getComments: LiveData<UiState<List<CommentData>>>
        get() = _getComments

    fun getComments(postId: String){
        _getComments.value = UiState.Loading
        repository.getComments(postId) {
            _getComments.value = it
        }
    }

    private val _addComment = MutableLiveData<UiState<String>>()
    val addComment: LiveData<UiState<String>>
        get() = _addComment

    fun addComment(comment: CommentData){
        _addComment.value = UiState.Loading
        repository.addComment(comment) {
            _addComment.value = it
        }
    }

    fun getSessionData(result: (UserData?) -> Unit) {
        repository.getSessionData(result)
    }


}