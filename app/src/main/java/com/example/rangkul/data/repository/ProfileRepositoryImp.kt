package com.example.rangkul.data.repository

import android.content.SharedPreferences
import com.example.rangkul.data.model.*
import com.example.rangkul.utils.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson


class ProfileRepositoryImp(
    private val database: FirebaseFirestore,
    private val appPreferences: SharedPreferences,
    private val gson: Gson,
    private val storageReference: FirebaseStorage
): ProfileRepository {

    override fun getVisitedUserData(uid: String, result: (UiState<UserData?>) -> Unit) {
        database.collection(FirestoreCollection.USER)
            .document(uid)
            .get()
            .addOnSuccessListener {
                val user = it.toObject(UserData::class.java)
                result.invoke(
                    UiState.Success(user)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun getSessionData(result: (UserData?) -> Unit) {
        val userStr = appPreferences.getString(SharedPrefConstants.USER_SESSION, null)
        if (userStr == null) {
            result.invoke(null)
        } else {
            val user = gson.fromJson(userStr, UserData::class.java)
            result.invoke(user)
        }
    }

}