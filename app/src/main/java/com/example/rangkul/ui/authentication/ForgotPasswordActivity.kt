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
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.app.NavUtils
import com.example.rangkul.R
import com.example.rangkul.databinding.ActivityForgotPasswordBinding
import com.example.rangkul.utils.UiState
import com.example.rangkul.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val viewModel: AuthViewModel by viewModels()
    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            NavUtils.navigateUpFromSameTask(this@ForgotPasswordActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        setToolbar()

        observer()

        binding.btSend.setOnClickListener{
            val email: String = binding.etEmail.text.toString()

            if (forgotPassValidation(email)) {
                viewModel.forgotPassword(email)
            }
        }
    }

    private fun observer() {
        viewModel.forgotPassword.observe(this) {state ->
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
                    sendSuccessfulMessage()
                }
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
        dialog.setContentView(R.layout.dialog_successful_message)

        val title = dialog.findViewById<TextView>(R.id.tvTitle)
        val description = dialog.findViewById<TextView>(R.id.tvDescription)

        title.setText(R.string.forgotpass_success_title)
        description.setText(R.string.forgotpass_success_description)

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

}