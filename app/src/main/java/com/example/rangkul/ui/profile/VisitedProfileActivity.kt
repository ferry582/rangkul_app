package com.example.rangkul.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.rangkul.R
import com.example.rangkul.data.model.UserData
import com.example.rangkul.databinding.ActivityOtherProfileBinding
import com.example.rangkul.utils.UiState
import com.example.rangkul.utils.hide
import com.example.rangkul.utils.show
import com.example.rangkul.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VisitedProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtherProfileBinding
    private lateinit var userId: String
    private val profileViewModel: VisitedProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("USER_ID").toString()

        setToolbar()

        profileViewModel.getVisitedUserData(userId)
        observeGetVisitedUserData()
    }

    private fun observeGetVisitedUserData() {
        profileViewModel.getVisitedUserData.observe(this) {state ->
            when(state) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                }

                is UiState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.progressBar.hide()
                    setVisitedProfileData(state.data)
                }
            }
        }
    }

    private fun setVisitedProfileData(visitedUserData: UserData?) {
        binding.tvUserName.text = visitedUserData?.userName
        binding.ivUserBadgePost.apply {
            when (visitedUserData?.badge) {
                "Trusted" -> {
                    setImageResource(R.drawable.ic_badge_trusted)
                }
                "Psychologist" -> {
                    setImageResource(R.drawable.ic_badge_psychologist)
                }
                else -> {
                    setImageResource(R.drawable.ic_badge_basic)
                }
            }
        }
        binding.civProfilePicture.apply {
            if (visitedUserData?.profilePicture.isNullOrEmpty()) setImageResource(R.drawable.ic_profile_picture_default)
            else {
                Glide
                    .with(context)
                    .load(visitedUserData?.profilePicture)
                    .placeholder(R.drawable.ic_profile_picture_default)
                    .error(R.drawable.ic_baseline_error_24)
                    .into(binding.civProfilePicture)
            }
        }
        binding.tvProfileBio.apply {
            if (visitedUserData?.bio.isNullOrEmpty()) {
                hide()
            }
            else {
                show()
                text = visitedUserData?.bio
            }
        }
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_grey)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}