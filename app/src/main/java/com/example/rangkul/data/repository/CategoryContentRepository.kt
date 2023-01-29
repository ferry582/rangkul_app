package com.example.rangkul.data.repository

import com.example.rangkul.data.model.*
import com.example.rangkul.utils.UiState

interface CategoryContentRepository {
    fun getCategoryContents(category: String, type: String, result: (UiState<List<CategoryContentData>>) -> Unit)
}