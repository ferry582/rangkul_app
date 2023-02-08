package com.example.rangkul.ui.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rangkul.data.model.UserData
import com.example.rangkul.data.repository.AuthRepository
import com.example.rangkul.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository): ViewModel() {

    private val _register = MutableLiveData<UiState<String>>()
    val register: LiveData<UiState<String>>
        get() = _register

    private val _login = MutableLiveData<UiState<String>>()
    val login: LiveData<UiState<String>>
        get() = _login

    private val _forgotPassword = MutableLiveData<UiState<String>>()
    val forgotPassword: LiveData<UiState<String>>
        get() = _forgotPassword

    fun register(
        email: String,
        password: String,
        user: UserData
    ){
        _register.value = UiState.Loading
        repository.registerUser(email, password, user) {
            _register.value = it
        }
    }

    fun login(
        email: String,
        password: String,
    ){
        _login.value = UiState.Loading
        repository.loginUser(email, password) {
            _login.value = it
        }
    }

    fun forgotPassword(
        email: String
    ){
        _forgotPassword.value = UiState.Loading
        repository.forgotPassword(email) {
            _forgotPassword.value = it
        }
    }

    fun logOut(result: () -> Unit) {
        repository.logOut(result)
    }

    fun getSession(result: (UserData?) -> Unit) {
        repository.getSession(result)
    }

}