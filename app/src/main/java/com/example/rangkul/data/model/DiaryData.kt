package com.example.rangkul.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@kotlinx.parcelize.Parcelize
data class DiaryData (
    @DocumentId
    var diaryId: String = "",
    val createdBy: String = "",
    @ServerTimestamp
    val createdAt: Date = Date(),
    val caption: String = "",
    val image: String? = null,
    val type: String = "",
    @ServerTimestamp
    val modifiedAt: Date = Date(),
    val moodTitle: String = "",
    val moodImage: Int = 0
): Parcelable
