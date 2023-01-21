package com.example.rangkul.data.repository

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class AuthenticationRepository(private val application: Application) {
    private val firebaseUserMutableLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()
    private val userLoggedMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val progressBarStatusMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val forgotPasswordStatusMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun getFirebaseUserMutableLiveData(): MutableLiveData<FirebaseUser> {
        return firebaseUserMutableLiveData
    }

    fun getUserLoggedMutableLiveData(): MutableLiveData<Boolean> {
        return userLoggedMutableLiveData
    }

    fun getProgressBarStatusMutableLiveData(): MutableLiveData<Boolean>{
        return progressBarStatusMutableLiveData
    }

    fun getForgotPasswordStatusMutableLiveData(): MutableLiveData<Boolean> {
        return forgotPasswordStatusMutableLiveData
    }

    init {
        if (auth.currentUser != null) {
            firebaseUserMutableLiveData.postValue(auth.currentUser)
        }
    }

    fun register(email: String?, pass: String?, name: String?) {
        auth.createUserWithEmailAndPassword(email!!, pass!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                firebaseUserMutableLiveData.postValue(auth.currentUser)

                // setDisplayName
                val profileUpdates =
                    UserProfileChangeRequest.Builder().setDisplayName(name).build()
                auth.currentUser?.updateProfile(profileUpdates)

            } else {
                Toast.makeText(application, task.exception!!.message, Toast.LENGTH_SHORT)
                    .show()
                progressBarStatusMutableLiveData.postValue(false)
            }
        }
    }

    fun login(email: String?, pass: String?) {
        auth.signInWithEmailAndPassword(email!!, pass!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                firebaseUserMutableLiveData.postValue(auth.currentUser)
            } else {
                Toast.makeText(application, task.exception!!.message, Toast.LENGTH_SHORT)
                    .show()
                progressBarStatusMutableLiveData.postValue(false)
            }
        }
    }

    fun signOut() {
        auth.signOut()
        userLoggedMutableLiveData.postValue(true)
    }

    fun forgotPassword(email: String?) {
        auth.sendPasswordResetEmail(email!!).addOnCompleteListener { task ->
            if (task.isSuccessful){
                progressBarStatusMutableLiveData.postValue(false)
                forgotPasswordStatusMutableLiveData.postValue(true)
            }else{
                Toast.makeText(application, task.exception!!.message, Toast.LENGTH_LONG).show()
                progressBarStatusMutableLiveData.postValue(false)
                forgotPasswordStatusMutableLiveData.postValue(false)
            }
        }
    }

}