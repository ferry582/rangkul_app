package com.example.rangkul.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rangkul.R
import com.example.rangkul.databinding.ActivitySettingsPrivacyBinding

class SettingsPrivacyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsPrivacyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsPrivacyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()

        binding.btPrivacyPolicy.setOnClickListener {
            intent = Intent(this, PolicyActivity::class.java)
            startActivity(intent)
        }

        binding.btToS.setOnClickListener {
            intent = Intent(this, ToserviceActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setToolbar() {
        setSupportActionBar(binding.settingToolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_grey)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"
        supportActionBar?.subtitle = "Privacy"
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}