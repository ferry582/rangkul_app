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
    val image: String? = null,
    val type: String = "",
    @ServerTimestamp
        val modifiedAt: Date = Date(),
    var userName: String = "",
    var profilePicture: String? = null,
    val userBadge: String = "",
    var commentsCount: Int = 0,
    var likesCount: Int = 0,
    val likeVisibility: Boolean = true,
    val commentVisibility: Boolean = true
): Parcelable
