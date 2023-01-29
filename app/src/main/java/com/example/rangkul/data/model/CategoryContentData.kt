package com.example.rangkul.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@kotlinx.parcelize.Parcelize
data class CategoryContentData (
        @DocumentId
        var contentId: String = "",
        @ServerTimestamp
        val listedAt: Date = Date(),
        val title: String = "",
        @ServerTimestamp
        val publishedAt: Date = Date(),
        val image: String = "",
        val url: String = "",
        val type: String = "",
        val source: String = "",
        val category: String = "",
        val duration: String = "",
        val language: String = ""
): Parcelable
