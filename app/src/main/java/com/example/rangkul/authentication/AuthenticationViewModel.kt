package com.example.rangkul.authentication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

class AuthenticationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AuthenticationRepository
    private val userData: MutableLiveData<FirebaseUser>
    private val loggedStatus: MutableLiveData<Boolean>
    private val progressBarStatus: MutableLiveData<Boolean>
    private val forgotPasswordStatus: MutableLiveData<Boolean>

    fun getUserData(): MutableLiveData<FirebaseUser> {
        return userData
    }

    fun getLoggedStatus(): MutableLiveData<Boolean> {
        return loggedStatus
    }

    fun getProgressBarStatus(): MutableLiveData<Boolean>{
        return progressBarStatus
    }

    fun getForgotPasswordStatus(): MutableLiveData<Boolean>{
        return forgotPasswordStatus
    }

    init {
        repository = AuthenticationRepository(application)
        userData = repository.getFirebaseUserMutableLiveData()
        loggedStatus = repository.getUserLoggedMutableLiveData()
        progressBarStatus = repository.getProgressBarStatusMutableLiveData()
        forgotPasswordStatus = repository.getForgotPasswordStatusMutableLiveData()
    }

    fun signUp(email: String?, pass: String?, name: String?) {
        repository.register(email, pass, name)
    }

    fun logIn(email: String?, pass: String?) {
        repository.login(email, pass)
    }

    fun logOut() {
        repository.signOut()
    }

    fun forgotPassword(email: String?) {
        repository.forgotPassword(email)
    }

}