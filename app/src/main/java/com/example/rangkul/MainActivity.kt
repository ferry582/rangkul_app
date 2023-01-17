package com.example.rangkul

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.rangkul.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Bottom Nav
        val navController = findNavController(R.id.fragment)
        binding.bottomView.setupWithNavController(navController)

    }

    override fun onBackPressed() {
        if (binding.bottomView.selectedItemId == R.id.homeFragment){
            // Close app (open home window) when onBackPressed, and prevent user navigate back to auth when onBackPress
            val a = Intent(Intent.ACTION_MAIN)
            a.addCategory(Intent.CATEGORY_HOME)
            a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(a)
        } else {
            // always back to homeFragment when back is clicked at search/notification/profile fragment
            binding.bottomView.selectedItemId = R.id.homeFragment
        }
    }
}