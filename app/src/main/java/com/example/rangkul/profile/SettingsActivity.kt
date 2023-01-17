package com.example.rangkul.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.rangkul.authentication.AuthenticationActivity
import com.example.rangkul.authentication.AuthenticationViewModel
import com.example.rangkul.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(application)
        )[AuthenticationViewModel::class.java]

        viewModel.getLoggedStatus().observe(this) { t ->
            if (t) {
                val intent =  Intent(this@SettingsActivity, AuthenticationActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
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
}