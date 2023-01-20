package com.example.rangkul.home

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId

@kotlinx.parcelize.Parcelize
data class PostData (
        @DocumentId
        val postId: String,
        val createdBy: String,
        val createdAt: String,
        val caption: String,
        val category: String,
        val image: String,
        val type: String,
        val modifiedAt: String
): Parcelable
