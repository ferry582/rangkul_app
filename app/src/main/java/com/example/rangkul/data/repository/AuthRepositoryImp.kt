package com.example.rangkul.data.repository

import android.content.SharedPreferences
import com.example.rangkul.data.model.UserData
import com.example.rangkul.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class AuthRepositoryImp(private val database: FirebaseFirestore,
                        private val auth: FirebaseAuth,
                        private val appPreferences: SharedPreferences,
                        private val gson: Gson
                        ): AuthRepository {

    override fun registerUser(
        email: String,
        password: String,
        user: UserData, result: (UiState<String>) -> Unit) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    user.userId = it.result.user?.uid ?: ""
                    updateUserInfo(user) { state ->
                        when(state) {
                            is UiState.Success -> {
                                storeSession(
                                    id = it.result.user?.uid ?: "") {user ->
                                    if (user == null) {
                                        result.invoke(
                                            UiState.Failure("Session Failed to store")
                                        )
                                    } else {
                                        result.invoke(
                                            UiState.Success("Sign Up Successful")
                                        )
                                    }
                                }
                            }

                            is UiState.Failure -> {
                                result.invoke(
                                    UiState.Failure(state.error)
                                )
                            }
                            UiState.Loading -> {}
                        }
                    }
                } else {
                    result.invoke(
                        UiState.Failure(
                            it.exception!!.message
                        )
                    )
                }

            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }

    }

    private fun updateUserInfo(user: UserData, result: (UiState<String>) -> Unit) {
        val document = database.collection(FirestoreCollection.USER).document(user.userId)
        document
            .set(user)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("User has been updated")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun loginUser(email: String, password: String, result: (UiState<String>) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storeSession(id = task.result.user?.uid ?: "") {user ->
                        if (user == null) {
                            result.invoke(
                                UiState.Failure("Failed store local session")
                            )
                        } else {
                            result.invoke(
                                UiState.Success("Login Successful")
                            )
                        }
                    }
                } else {
                    result.invoke(
                        UiState.Failure(task.exception?.message)
                    )
                }
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun forgotPassword(email: String, result: (UiState<String>) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    result.invoke(
                        UiState.Success("Email has been sent")
                    )
                }else{
                    result.invoke(
                        UiState.Failure(
                            task.exception?.message
                        )
                    )
                }
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun logOut(result: () -> Unit) {
        auth.signOut()
        appPreferences.edit().putString(SharedPrefConstants.USER_SESSION, null).apply()
        result.invoke()
    }

    override fun storeSession(id: String, result: (UserData?) -> Unit) {
        database.collection(FirestoreCollection.USER).document(id)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = it.result.toObject(UserData::class.java)
                    appPreferences.edit().putString(SharedPrefConstants.USER_SESSION, gson.toJson(user)).apply()
                    result.invoke(user)
                } else {
                    result.invoke(null)
                }
            }
            .addOnFailureListener {
                result.invoke(null)
            }
    }

    override fun getSession(result: (UserData?) -> Unit) {
        val userStr = appPreferences.getString(SharedPrefConstants.USER_SESSION, null)
        if (userStr == null) {
            result.invoke(null)
        } else {
            val user = gson.fromJson(userStr, UserData::class.java)
            result.invoke(user)
        }
    }

}