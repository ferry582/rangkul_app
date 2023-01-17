package com.example.rangkul.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import com.example.rangkul.MainActivity
import com.example.rangkul.R
import com.example.rangkul.databinding.ActivityAuthenticationBinding

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarColor()

        binding.tvLoginButton.setOnClickListener {
            val intent = Intent(this, LoginOptionsActivity::class.java)
            startActivity(intent)
        }

        binding.btSignup.setOnClickListener {
            val intent = Intent(this, SignupOptionsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setStatusBarColor() {
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.greenBackground)
        window.decorView.systemUiVisibility = 0 //Change icon color in status bar
    }

    // Check if user already login, then skip auth and navigate to MainActivity
    override fun onStart() {
        super.onStart()

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(application)
        )[AuthenticationViewModel::class.java]

        viewModel.getUserData().observe(this
        ) { firebaseUser ->
            if (firebaseUser != null) {
                val intent = Intent(this@AuthenticationActivity, MainActivity::class.java)
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }
}