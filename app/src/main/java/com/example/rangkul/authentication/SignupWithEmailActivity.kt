package com.example.rangkul.authentication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.lifecycle.ViewModelProvider
import com.example.rangkul.MainActivity
import com.example.rangkul.databinding.ActivitySignupWithEmailBinding

class SignupWithEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupWithEmailBinding
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupWithEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(application)
        )[AuthenticationViewModel::class.java]

        viewModel.getUserData().observe(this
        ) { firebaseUser ->
            if (firebaseUser != null) {
                val intent = Intent(this@SignupWithEmailActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("SIGNUP_SUCCESSFUL", "true")
                startActivity(intent)
            }
        }

        viewModel.getProgressBarStatus().observe(this) {
            if (!it){
                loadingVisibility(false)
            }
        }

        binding.btSignUp.setOnClickListener{
            val email: String = binding.etEmail.text.toString()
            val pass: String = binding.etPassword.text.toString()
            val name: String = binding.etName.text.toString()

            if (signupValidation(email, pass, name)) {
                loadingVisibility(true)
                viewModel.signUp(email, pass, name)
            }
        }
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
        } else {
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
            binding.tvSignup.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.tvSignup.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
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