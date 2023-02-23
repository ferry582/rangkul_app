package com.example.rangkul.ui.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rangkul.data.model.CommentData
import com.example.rangkul.data.model.ReportData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.data.repository.OptionsRepository
import com.example.rangkul.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommentOptionsViewModel @Inject constructor(private val repository: OptionsRepository): ViewModel() {

    private val _deleteComment = MutableLiveData<UiState<String>>()
    val deleteComment: LiveData<UiState<String>>
        get() = _deleteComment

    fun deleteComment(comment: CommentData){
        _deleteComment.value = UiState.Loading
        repository.deleteComment(comment) {
            _deleteComment.value = it
        }
    }

    fun addReportData(report: ReportData, result: (UiState<Boolean>) -> Unit){
        repository.addReportData(report) {
            result.invoke(it)
        }
    }

    fun getSessionData(result: (UserData?) -> Unit) {
        repository.getSessionData(result)
    }

}