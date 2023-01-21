package com.example.rangkul.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.rangkul.R
import com.example.rangkul.databinding.ActivitySettingsBinding
import com.example.rangkul.ui.authentication.AuthenticationActivity
import com.example.rangkul.ui.authentication.AuthenticationViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(application)
        )[AuthenticationViewModel::class.java]

        viewModel.getLoggedStatus().observe(this) { t ->
            if (t) {
                val intent = Intent(this@SettingsActivity, AuthenticationActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

//        viewModel.getUserData().observe(this
//        ) { firebaseUser ->
//            binding.tvName.text = firebaseUser.displayName.toString()
//        }

        binding.outBtn.setOnClickListener {
            viewModel.logOut()
        }

        // Ganti version disini
        val currentVersion = "0.0.2"

        binding.verNum.text = currentVersion
    }

    private fun setToolbar() {
        setSupportActionBar(binding.settingToolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_grey)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}