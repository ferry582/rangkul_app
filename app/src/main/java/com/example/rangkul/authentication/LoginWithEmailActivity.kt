package com.example.rangkul.authentication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.lifecycle.ViewModelProvider
import com.example.rangkul.MainActivity
import com.example.rangkul.databinding.ActivityLoginWithEmailBinding

class LoginWithEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginWithEmailBinding
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginWithEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(application)
        )[AuthenticationViewModel::class.java]

        viewModel.getUserData().observe(this
        ) { firebaseUser ->
            if (firebaseUser != null) {
                val intent = Intent(this@LoginWithEmailActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        viewModel.getProgressBarStatus().observe(this) {
            if (!it){
                loadingVisibility(false)
            }
        }

        binding.btLogIn.setOnClickListener{
            val email: String = binding.etEmail.text.toString()
            val pass: String = binding.etPassword.text.toString()

            if (loginValidation(email, pass)) {
                loadingVisibility(true)
                viewModel.logIn(email, pass)
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadingVisibility(isLoading: Boolean) {
        if (isLoading) {
            binding.tvLogin.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.tvLogin.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun loginValidation(email: String, pass: String): Boolean {
        binding.tilPassword.isPasswordVisibilityToggleEnabled = true
        return if (email.isEmpty()) {
            binding.etEmail.error = "Please enter email"
            false
        } else if (pass.isEmpty()) {
            binding.tilPassword.isPasswordVisibilityToggleEnabled = false
            binding.etPassword.error = "Please enter password"
            false
        } else {
            true
        }
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