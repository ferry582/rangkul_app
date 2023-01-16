package com.example.rangkul.authentication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

class AuthenticationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AuthenticationRepository
    private val userData: MutableLiveData<FirebaseUser>
    private val loggedStatus: MutableLiveData<Boolean>

    fun getUserData(): MutableLiveData<FirebaseUser> {
        return userData
    }

    fun getLoggedStatus(): MutableLiveData<Boolean> {
        return loggedStatus
    }

    init {
        repository = AuthenticationRepository(application)
        userData = repository.getFirebaseUserMutableLiveData()
        loggedStatus = repository.getUserLoggedMutableLiveData()
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
}