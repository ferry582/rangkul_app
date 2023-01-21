package com.example.rangkul.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

@kotlinx.parcelize.Parcelize
data class PostData (
        @DocumentId
        val postId: String,
        @ServerTimestamp
        val createdBy: String,
        val createdAt: String,
        val caption: String,
        val category: String,
        val image: String,
        val type: String,
        @ServerTimestamp
        val modifiedAt: String
): Parcelable
