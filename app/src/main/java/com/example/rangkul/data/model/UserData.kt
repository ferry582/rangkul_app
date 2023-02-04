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
    var userName: String = "",
    var bio: String? = null,
    var profilePicture: String? = null,
    val email: String = "",
    var gender: String? = null,
    var birthDate: Date? = null,
    val badge: String = ""
): Parcelable