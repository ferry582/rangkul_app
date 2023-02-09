package com.example.rangkul.ui.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.example.rangkul.ui.MainActivity
import com.example.rangkul.databinding.ActivityLoginWithEmailBinding
import com.example.rangkul.utils.UiState
import com.example.rangkul.utils.hide
import com.example.rangkul.utils.hideKeyboard
import com.example.rangkul.utils.show
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginWithEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginWithEmailBinding
    private val viewModel: AuthViewModel by viewModels()
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                NavUtils.navigateUpFromSameTask(this@LoginWithEmailActivity)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginWithEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        setToolbar()

        observer()

        binding.btLogIn.setOnClickListener {
            val email: String = binding.etEmail.text.toString().trim()
            val pass: String = binding.etPassword.text.toString()

            if (loginValidation(email, pass)) {
                viewModel.login(email, pass)
            }

            hideKeyboard()
        }

        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observer() {
        viewModel.login.observe(this) { state ->
            when (state) {
                is UiState.Failure -> {
                    loadingVisibility(false)
                    Snackbar.make(binding.root, state.error.toString(), Snackbar.LENGTH_SHORT)
                        .show()
                }
                UiState.Loading -> {
                    loadingVisibility(true)
                }
                is UiState.Success -> {
                    loadingVisibility(false)
                    val intent = Intent(this@LoginWithEmailActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("LOGIN_SUCCESSFUL", "true")
                    startActivity(intent)
                }
            }
        }
    }

    private fun loadingVisibility(isLoading: Boolean) {
        if (isLoading) {
            binding.tvLogin.hide()
            binding.progressBar.show()
        } else {
            binding.tvLogin.show()
            binding.progressBar.hide()
        }
    }

    private fun loginValidation(email: String, pass: String): Boolean {
        return if (email.isEmpty()) {
            binding.etEmail.error = "Please enter email"
            false
        } else if (pass.isEmpty()) {
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

}