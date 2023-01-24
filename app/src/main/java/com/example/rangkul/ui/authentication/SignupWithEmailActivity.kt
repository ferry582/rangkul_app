package com.example.rangkul.ui.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.example.rangkul.data.model.UserData
import com.example.rangkul.ui.MainActivity
import com.example.rangkul.databinding.ActivitySignupWithEmailBinding
import com.example.rangkul.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SignupWithEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupWithEmailBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupWithEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()

        observer()

        binding.btSignUp.setOnClickListener{
            val email: String = binding.etEmail.text.toString()
            val pass: String = binding.etPassword.text.toString()
            val name: String = binding.etName.text.toString()

            if (signupValidation(email, pass, name.trim())) {
                viewModel.register(
                    email = email,
                    password = pass,
                    user = getUserObj()
                )
            }
        }
    }

    private fun observer() {
        viewModel.register.observe(this) {state ->
            when (state) {
                is UiState.Failure -> {
                    loadingVisibility(false)
                    toast(state.error)
                }
                UiState.Loading -> {
                    loadingVisibility(true)
                }
                is UiState.Success -> {
                    loadingVisibility(false)
                    val intent = Intent(this@SignupWithEmailActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("SIGNUP_SUCCESSFUL", "true")
                    startActivity(intent)
                }
            }
        }
    }

    private fun getUserObj(): UserData {
        return UserData(
            userId = "",
            userName = binding.etName.text.toString().capitalizeWords(),
            createdAt = Date(),
            bio = "",
            profilePicture = "",
            email = binding.etEmail.text.toString(),
            gender = "",
            birthDate = Date(),
            badge = "Basic" // Default value for new user
        )
    }

    private fun signupValidation(email: String, pass: String, name: String): Boolean {
        binding.tilPassword.isPasswordVisibilityToggleEnabled = true
        return if (email.isEmpty()) {
            binding.etEmail.error = "Please enter email"
            false
        } else if (name.isEmpty()){
            binding.etName.error = "Please enter your name"
            false
        } else if (pass.isEmpty()) {
            binding.tilPassword.isPasswordVisibilityToggleEnabled = false
            binding.etPassword.error = "Please enter password"
            false
        } else if (!name.onlyLettersAndSpace()) {
            binding.etName.error = "Name should be contain alphabet only"
            false
        }
        else {
            if (isValidPassword(pass)) {
                true
            } else {
                binding.tilPassword.isPasswordVisibilityToggleEnabled = false
                binding.etPassword.error = "must be longer than 8 characters, contain numbers, " +
                        "special character and uppercase letter"
                false
            }
        }
    }

    private fun loadingVisibility(isLoading: Boolean) {
        if (isLoading) {
            binding.tvSignup.hide()
            binding.progressBar.show()
        } else {
            binding.tvSignup.show()
            binding.progressBar.hide()
        }
    }

    private fun isValidPassword(pass: String): Boolean {
        if (pass.length < 8) return false
        if (pass.firstOrNull { it.isDigit() } == null) return false
        if (pass.filter { it.isLetter() }.firstOrNull { it.isUpperCase() } == null) return false
        if (pass.filter { it.isLetter() }.firstOrNull { it.isLowerCase() } == null) return false
        if (pass.firstOrNull { !it.isLetterOrDigit() } == null) return false // Check special character

        return true
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(com.example.rangkul.R.drawable.ic_back_grey)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
    }
}