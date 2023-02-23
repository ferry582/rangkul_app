package com.example.rangkul.data.repository

import com.example.rangkul.data.model.*
import com.example.rangkul.utils.UiState

interface OptionsRepository {
    fun deletePost(post: PostData, result: (UiState<String>) -> Unit)
    fun deleteComment(comment: CommentData, result: (UiState<String>) -> Unit)
    fun deleteDiary(diary: DiaryData, result: (UiState<String>) -> Unit)
    fun addReportData(report: ReportData, result: (UiState<Boolean>) -> Unit)
    fun getSessionData(result: (UserData?) -> Unit)
}