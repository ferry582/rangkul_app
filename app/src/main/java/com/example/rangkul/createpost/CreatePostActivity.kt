package com.example.rangkul.createpost

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.rangkul.databinding.ActivityCreatePostBinding
import com.google.android.material.chip.Chip

class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding
    private lateinit var selectedCategory: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()

        // Set Category
        selectedCategory = intent.getStringExtra("SELECTED_CATEGORY").toString()
        binding.tvSelectedCategory.text = selectedCategory

        binding.chipGroupPostType.setOnCheckedChangeListener { group, _ ->
            val ids = group.checkedChipIds
            for (id in ids) {
                val chip: Chip = group.findViewById(id!!)
                selectedCategory = chip.text.toString()

                when (chip.text) {
                    "Public" -> {
                        updateHeaderUI(chip.text.toString())
                    }

                    "Anonymous" -> {
                        updateHeaderUI(chip.text.toString())
                    }

                    else -> {
                       updateHeaderUI(chip.text.toString())
                    }
                }
            }

        }
    }

    fun updateHeaderUI(type: String) {
        if (type == "Diary") {
            binding.clPublicAnonymous.visibility = View.GONE
            binding.clDiary.visibility = View.VISIBLE
        } else {
            binding.clPublicAnonymous.visibility = View.VISIBLE
            binding.clDiary.visibility = View.GONE
        }

    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(com.example.rangkul.R.drawable.ic_back_grey)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

}