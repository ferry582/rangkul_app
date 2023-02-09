package com.example.rangkul.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.NavUtils
import androidx.core.content.res.ResourcesCompat
import com.example.rangkul.R
import com.example.rangkul.databinding.ActivitySignupOptionsBinding

class SignupOptionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupOptionsBinding
    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            NavUtils.navigateUpFromSameTask(this@SignupOptionsActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

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
        binding.btSignupGoogle.setOnClickListener {
            Toast.makeText(application, "Coming Soon!", Toast.LENGTH_SHORT)
                .show()
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
        window.statusBarColor = ResourcesCompat.getColor(resources, R.color.greenBackground, null)
        window.decorView.systemUiVisibility = 0 //Change icon color in status bar
    }

}