package com.example.rangkul.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.app.NavUtils
import com.example.rangkul.R
import com.example.rangkul.databinding.ActivitySignupOptionsBinding

class SignupOptionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupOptionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()
        setStatusBarColor()

        binding.btSignupEmail.setOnClickListener {
            val intent = Intent(this, SignupWithEmailActivity::class.java)
            startActivity(intent)
        }
        binding.tvLogIn.setOnClickListener {
            val intent = Intent(this, LoginOptionsActivity::class.java)
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

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
    }
}