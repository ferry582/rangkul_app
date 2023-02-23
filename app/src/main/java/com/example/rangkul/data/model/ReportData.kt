package com.example.rangkul.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@kotlinx.parcelize.Parcelize
data class ReportData (
    @DocumentId
    var reportId: String = "",
    @ServerTimestamp
    val reportedAt: Date = Date(),
    val reportedBy: String = "",
    val reportedType: String ="", // post/comment/user
    val reportedId: String = "", // postId/commentId/userId
    val reason: String = "",
    val status: String = "" // approved/denied/waiting
): Parcelable
