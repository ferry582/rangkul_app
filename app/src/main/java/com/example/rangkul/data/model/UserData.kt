package com.example.rangkul.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

@kotlinx.parcelize.Parcelize
data class UserData (
    @DocumentId
    var userId: String = "",
    @ServerTimestamp
    val createdAt: Date = Date(),
    val userName: String = "",
    val bio: String = "",
    val profilePicture: String = "",
    val email: String = "",
    val gender: String = "",
    val birthDate: Date = Date(),
    val badge: String = ""
): Parcelable