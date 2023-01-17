package com.example.rangkul.authentication

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.example.rangkul.R
import com.example.rangkul.databinding.ActivityLoginOptionsBinding

class LoginOptionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginOptionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()
        setStatusBarColor()

        binding.btLoginEmail.setOnClickListener {
            val intent = Intent(this, LoginWithEmailActivity::class.java)
            startActivity(intent)
        }
        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, SignupOptionsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_green)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setStatusBarColor() {
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.greenBackground)
        window.decorView.systemUiVisibility = 0 //Change icon color in status bar
    }

    // Navigate to parent activity, instead of finish() when onBackPressed
    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
    }
}