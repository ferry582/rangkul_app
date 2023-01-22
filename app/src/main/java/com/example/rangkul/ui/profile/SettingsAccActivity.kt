package com.example.rangkul.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rangkul.R
import com.example.rangkul.databinding.ActivitySettingsAccBinding

class SettingsAccActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsAccBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsAccBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()

        binding.accInfoBtn.setOnClickListener {
            intent = Intent(this, SettingsAccInfoActivity::class.java)
            startActivity(intent)
        }

        binding.changePassBtn.setOnClickListener {
            intent = Intent(this, ChangePassActivity::class.java)
            startActivity(intent)
        }

    }


    private fun setToolbar() {
        setSupportActionBar(binding.settingToolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_grey)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"
        supportActionBar?.subtitle = "Account"
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}