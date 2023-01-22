package com.example.rangkul.ui.profile

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rangkul.R
import com.example.rangkul.databinding.ActivitySettingsAccInfoBinding
import java.util.*

class SettingsAccInfoActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivitySettingsAccInfoBinding

    // Date of Birth variables
    private val calendar = Calendar.getInstance()
    private val formatter = SimpleDateFormat("dd MMMM yyy", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsAccInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()

        // Date of Birth dialog
        binding.changeBirthBtn.setOnClickListener {
            DatePickerDialog(
                this,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.changeGenderBtn.setOnClickListener {
            intent = Intent(this, ChangeGenderActivity::class.java)
            startActivity(intent)
        }

        binding.changeEmailBtn.setOnClickListener {
            intent = Intent(this, ChangeEmailActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setToolbar() {
        setSupportActionBar(binding.settingToolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_grey)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"
        supportActionBar?.subtitle = "Account Information"
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    // Date of Birth logic
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        Log.e("Calendar", "$dayOfMonth -- $month -- $year")
        calendar.set(year, month, dayOfMonth)
        displayFormattedDate(calendar.timeInMillis)
    }

    private fun displayFormattedDate(timestamp: Long) {
        findViewById<TextView>(R.id.textLahir).text = formatter.format(timestamp)
    }
}