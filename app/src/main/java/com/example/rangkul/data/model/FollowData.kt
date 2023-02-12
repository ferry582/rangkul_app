package com.example.rangkul.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

@kotlinx.parcelize.Parcelize
data class FollowData (
    @DocumentId
    var userId: String = "",
    @ServerTimestamp
    var followedAt: Date = Date(),
): Parcelable