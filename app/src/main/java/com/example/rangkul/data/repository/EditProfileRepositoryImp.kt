package com.example.rangkul.data.repository

import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.example.rangkul.data.model.*
import com.example.rangkul.utils.*
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class EditProfileRepositoryImp(
    private val database: FirebaseFirestore,
    private val appPreferences: SharedPreferences,
    private val gson: Gson,
    private val storageReference: FirebaseStorage
): EditProfileRepository {

    override suspend fun uploadProfilePicture(fileUri: Uri, onResult: (UiState<Uri>) -> Unit) {
        try {
            val uri: Uri = withContext(Dispatchers.IO) {
                storageReference
                    .getReference(FirebaseStorageConstants.ROOT_DIRECTORY)
                    .child("${FirebaseStorageConstants.USER_IMAGE}/${UUID.randomUUID()}")
                    .putFile(fileUri)
                    .await()
                    .storage
                    .downloadUrl
                    .await()
            }
            onResult.invoke(
                UiState.Success(uri)
            )
        } catch (e: FirebaseException) {
            onResult.invoke(
                UiState.Failure(e.message)
            )
        } catch (e: Exception) {
            onResult.invoke(
                UiState.Failure(e.message)
            )
        }
    }

    override fun deleteProfilePicture(imageUrl: String, result: (UiState<String>) -> Unit){
        storageReference.getReferenceFromUrl(imageUrl).delete()
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Profile picture has been deleted")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun updateUserProfile(newUserData: UserData, isPhotoOrNameChange: Boolean, result: (UiState<String>) -> Unit) {
        updateSessionData(newUserData)

        if (isPhotoOrNameChange) {
            updateAllUserPostsData(newUserData) {
                when (it) {
                    is UiState.Failure -> {
                        Log.e("updateUserProfile", it.error.toString())
                    }

                    is UiState.Success -> {
                        Log.w("updateUserProfile", it.data)
                    }
                    UiState.Loading -> {}
                }
            }

            updateAllUserCommentsData(newUserData) {
                when (it) {
                    is UiState.Failure -> {
                        Log.e("updateUserProfile", it.error.toString())
                    }

                    is UiState.Success -> {
                        Log.w("updateUserProfile", it.data)
                    }
                    UiState.Loading -> {}
                }
            }
        }

        // Update entire user document with the new one
        database.collection(FirestoreCollection.USER)
            .document(newUserData.userId)
            .set(newUserData)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("User data has been updated")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    private fun updateAllUserCommentsData(newUserData: UserData, result: (UiState<String>) -> Unit){
        database.collection(FirestoreCollection.COMMENT)
            .whereEqualTo(FirestoreDocumentField.COMMENT_OWNER, newUserData.userId)
            .get()
            .addOnSuccessListener {
                val batch: WriteBatch = database.batch()
                for (document in it) {

                    batch.update(
                        document.reference, FirestoreDocumentField.COMMENT_PROFILE_PICTURE, newUserData.profilePicture,
                        FirestoreDocumentField.COMMENT_USER_NAME, newUserData.userName
                    )
                }
                batch.commit()
                    .addOnSuccessListener {
                        result.invoke(
                            UiState.Success("Update comments data successful")
                        )
                    }
                    .addOnFailureListener {
                        result.invoke(
                            UiState.Failure(it.localizedMessage)
                        )
                    }
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
        /*
            Note : It would be better to use callable cloud function
                   to update the entire comments document
         */
    }

    private fun updateAllUserPostsData(newUserData: UserData, result: (UiState<String>) -> Unit){
        database.collection(FirestoreCollection.POST)
            .whereEqualTo(FirestoreDocumentField.POST_CREATED_BY, newUserData.userId)
            .get()
            .addOnSuccessListener {
                val batch: WriteBatch = database.batch()
                for (document in it) {

                    batch.update(
                        document.reference, FirestoreDocumentField.POST_PROFILE_PICTURE, newUserData.profilePicture,
                        FirestoreDocumentField.POST_USER_NAME, newUserData.userName
                    )
                }
                batch.commit()
                    .addOnSuccessListener {
                        result.invoke(
                            UiState.Success("Update posts data successful")
                        )
                    }
                    .addOnFailureListener {
                        result.invoke(
                            UiState.Failure(it.localizedMessage)
                        )
                    }
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
        /*
            Note : It would be better to use callable cloud function
                   to update the entire posts document
         */
    }

    private fun updateSessionData(newUserData: UserData) {
        appPreferences.edit()
            .putString(SharedPrefConstants.USER_SESSION, gson.toJson(newUserData))
            .apply()

        Log.w("UpdateUserProfile", "Session data at shared preferences has been updated")
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