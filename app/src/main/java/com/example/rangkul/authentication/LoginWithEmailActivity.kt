package com.example.rangkul.authentication

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.app.NavUtils
import com.example.rangkul.R
import com.example.rangkul.databinding.ActivityLoginWithEmailBinding

class LoginWithEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginWithEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginWithEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()

        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
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