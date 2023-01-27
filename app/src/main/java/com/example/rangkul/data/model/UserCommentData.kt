package com.example.rangkul.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@kotlinx.parcelize.Parcelize
data class UserCommentData (
        @DocumentId
        var commentId: String = "",
        @ServerTimestamp
        val commentedAt: Date = Date(),
        var postId: String = ""
): Parcelable
