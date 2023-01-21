package com.example.rangkul.ui.authentication

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import androidx.core.app.NavUtils
import androidx.lifecycle.ViewModelProvider
import com.example.rangkul.R
import com.example.rangkul.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(application)
        )[AuthenticationViewModel::class.java]

        viewModel.getProgressBarStatus().observe(this) {
            if (!it){
                loadingVisibility(false)
            }
        }

        viewModel.getForgotPasswordStatus().observe(this) {
            if (it){
                sendSuccessfulMessage()
            }
        }

        binding.btSend.setOnClickListener{
            val email: String = binding.etEmail.text.toString()

            if (forgotPassValidation(email)) {
                loadingVisibility(true)
                viewModel.forgotPassword(email)
            }
        }
    }

    private fun loadingVisibility(isLoading: Boolean) {
        if (isLoading) {
            binding.tvSend.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.tvSend.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun sendSuccessfulMessage() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_forgot_pass_successful)

        dialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
        }, 2500)
    }

    private fun forgotPassValidation(email: String): Boolean {
        return if (email.isEmpty()) {
            binding.etEmail.error = "Please enter email"
            false
        } else {
            true
        }
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_grey)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
    }
}