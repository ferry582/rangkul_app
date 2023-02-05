package com.example.rangkul.data.repository

import android.net.Uri
import com.example.rangkul.data.model.*
import com.example.rangkul.utils.UiState

interface PostRepository {
    fun getPosts(type: String, result: (UiState<List<PostData>>) -> Unit)
    fun getCurrentUserPosts(type: String, uid: String, result: (UiState<List<PostData>>) -> Unit)
    fun getPostsWithCategory(category: String, result: (UiState<List<PostData>>) -> Unit)
    fun getUserDiaries(uid: String, result: (UiState<List<DiaryData>>) -> Unit)
    fun addPost(post: PostData, result: (UiState<String>) -> Unit)
    fun addDiary(diary: DiaryData, result: (UiState<String>) -> Unit)
    fun getComments(postId: String, result: (UiState<List<CommentData>>) -> Unit)
    fun addComment(comment: CommentData, result: (UiState<String>) -> Unit)
    fun addLike(like: LikeData, postId: String, currentUserId: String, result: (UiState<String>) -> Unit)
    fun getUserLikeData(currentUserId: String, result: (UiState<List<LikeData>>) -> Unit)
    fun getSessionData(result: (UserData?) -> Unit)
    suspend fun uploadPostImage(fileUri: Uri, onResult: (UiState<Uri>) -> Unit)
}