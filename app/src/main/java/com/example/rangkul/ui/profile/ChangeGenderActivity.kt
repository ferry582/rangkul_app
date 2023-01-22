package com.example.rangkul.ui.profile

import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rangkul.R
import com.example.rangkul.databinding.ActivityChangeGenderBinding

class ChangeGenderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangeGenderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeGenderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()

        binding.btConfirm.setOnClickListener {
            val radioGrup = findViewById<RadioGroup>(R.id.radioGrup)
            val id: Int = radioGrup.checkedRadioButtonId
            if (id != -1) {
                val radio: RadioButton = findViewById(id)
                Toast.makeText(
                    applicationContext,
                    "${radio.text}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Please choose one",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setToolbar() {
        setSupportActionBar(binding.settingToolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_grey)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Change Gender"
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}