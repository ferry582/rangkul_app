package com.example.rangkul.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NavUtils
import com.example.rangkul.R
import com.example.rangkul.databinding.ActivitySignupWithEmailBinding

class SignupWithEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupWithEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupWithEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()
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