package com.example.rangkul.data.repository

import com.example.rangkul.data.model.*
import com.example.rangkul.utils.FirestoreCollection
import com.example.rangkul.utils.FirestoreDocumentField
import com.example.rangkul.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CategoryRepositoryImp(
    private val database: FirebaseFirestore
): CategoryRepository {

    override fun getCategoryContents(category: String, type: String, result: (UiState<List<CategoryContentData>>) -> Unit) {
        database.collection(FirestoreCollection.CATEGORY_CONTENT)
            .whereEqualTo(FirestoreDocumentField.CONTENT_TYPE, type)
            .whereEqualTo(FirestoreDocumentField.CONTENT_CATEGORY, category)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                error?.let {
                    result.invoke(
                        UiState.Failure(it.localizedMessage)
                    )
                }

                value?.let {
                    val contents = arrayListOf<CategoryContentData>()
                    for (document in it) {
                        val content = document.toObject(CategoryContentData::class.java)
                        contents.add(content)
                    }
                    result.invoke(
                        UiState.Success(contents)
                    )
                }
            }
    }

}