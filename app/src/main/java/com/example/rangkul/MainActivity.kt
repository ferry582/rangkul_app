package com.example.rangkul

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.rangkul.authentication.AuthenticationActivity
import com.example.rangkul.authentication.AuthenticationViewModel
import com.example.rangkul.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(application)
        )[AuthenticationViewModel::class.java]

        viewModel.getLoggedStatus().observe(this) { t ->
            if (t) {
                val intent =  Intent(this@MainActivity, AuthenticationActivity::class.java)
                startActivity(intent)
            }
        }

        viewModel.getUserData().observe(this
        ) { firebaseUser ->
            binding.tvName.text = firebaseUser.displayName.toString()
        }

        binding.btLogout.setOnClickListener {
                viewModel.logOut()
        }

    }

    // Close app (open home window) when onBackPress, and prevent user navigate back to auth when onBackPress
    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }
}