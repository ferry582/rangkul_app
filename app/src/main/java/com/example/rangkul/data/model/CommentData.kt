package com.example.rangkul.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@kotlinx.parcelize.Parcelize
data class CommentData (
        @DocumentId
        var commentId: String = "",
        val commentedBy: String = "",
        @ServerTimestamp
        val commentedAt: Date = Date(),
        val comment: String = "",
        val userName: String = "",
        val profilePicture: String? = null,
        val userBadge: String = "",
        val postId: String = ""
): Parcelable
