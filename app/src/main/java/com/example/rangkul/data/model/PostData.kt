package com.example.rangkul.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@kotlinx.parcelize.Parcelize
data class PostData (
        @DocumentId
        var postId: String = "",
        val createdBy: String = "",
        @ServerTimestamp
        val createdAt: Date = Date(),
        val caption: String = "",
        val category: String = "",
        val image: String = "",
        val type: String = "",
        @ServerTimestamp
        val modifiedAt: Date = Date(),
        val userName: String = "",
        val profilePicture: String = "",
        val userBadge: String = "",
        val commentsCount: Int? = 0,
        val likesCount: Int = 0
): Parcelable
