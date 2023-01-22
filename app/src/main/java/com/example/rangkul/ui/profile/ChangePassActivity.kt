package com.example.rangkul.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rangkul.R
import com.example.rangkul.databinding.ActivityChangePassBinding

class ChangePassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePassBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()

        binding.btConfirmPass.setOnClickListener {
            val pass: String = binding.etConfirmNewPassword.text.toString()

            if (newPassValid(pass)) {
                Toast.makeText(
                    applicationContext,
                    "Successfully Change Password!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // New Pass Confirmation
    private fun newPassValid(pass: String): Boolean {
        binding.tilNewPassword.isPasswordVisibilityToggleEnabled = true
        binding.tilConfirmNewPassword.isPasswordVisibilityToggleEnabled = true
        return if (pass.isEmpty()) {
            binding.tilNewPassword.isPasswordVisibilityToggleEnabled = false
            binding.etNewPassword.error = "Please enter new password"
            binding.tilConfirmNewPassword.isPasswordVisibilityToggleEnabled = false
            binding.etConfirmNewPassword.error = "Please enter new password"
            false
        } else {
            if (isValidPassword(pass)) {
                true
            } else {
                binding.tilNewPassword.isPasswordVisibilityToggleEnabled = false
                binding.etNewPassword.error =
                    "New password must be longer than 8 characters, contain numbers, " +
                            "special character and uppercase letter"
                binding.tilConfirmNewPassword.isPasswordVisibilityToggleEnabled = false
                binding.etConfirmNewPassword.error =
                    "New password must be longer than 8 characters, contain numbers, " +
                            "special character and uppercase letter"
                false
            }
        }
    }

    // Pass validity
    private fun isValidPassword(pass: String): Boolean {
        if (pass.length < 8) return false
        if (pass.firstOrNull { it.isDigit() } == null) return false
        if (pass.filter { it.isLetter() }.firstOrNull { it.isUpperCase() } == null) return false
        if (pass.filter { it.isLetter() }.firstOrNull { it.isLowerCase() } == null) return false
        if (pass.firstOrNull { !it.isLetterOrDigit() } == null) return false // Check special character

        return true
    }

    private fun setToolbar() {
        setSupportActionBar(binding.settingToolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_grey)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Change Password"
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}