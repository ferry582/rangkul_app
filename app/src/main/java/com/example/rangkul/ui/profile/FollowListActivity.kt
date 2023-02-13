package com.example.rangkul.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rangkul.R
import com.example.rangkul.data.model.UserData
import com.example.rangkul.databinding.ActivityFollowListBinding
import com.example.rangkul.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFollowListBinding
    private val profileViewModel: VisitedProfileViewModel by viewModels()
    private var userDataList: MutableList<UserData> = arrayListOf()
    private lateinit var targetUserId: String
    private lateinit var targetUserName: String
    private lateinit var followType: String // Following or Followers
    private val adapter by lazy {
        FollowListAdapter (
            onItemClicked = { pos, uid ->
                if (uid == currentUserData().userId) {
                    // navigate to profile fragment
                } else {
                    val intent = Intent(this, VisitedProfileActivity::class.java)
                    intent.putExtra("USER_ID", uid)
                    startActivity(intent)
                }
            },
            onFollowClicked = { pos, uid ->
                profileViewModel.addFollowData(currentUserData().userId, uid)
            },
            onUnfollowClicked = { pos, uid ->
                profileViewModel.removeFollowData(currentUserData().userId, uid)
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        targetUserId = intent.getStringExtra("USER_ID").toString()
        followType = intent.getStringExtra("FOLLOW_TYPE").toString()
        targetUserName = intent.getStringExtra("USER_NAME").toString()

        setToolbar()

        // Configure Post RecyclerView
        binding.rvFollowList.adapter = adapter
        binding.rvFollowList.layoutManager = LinearLayoutManager(this)
        binding.rvFollowList.setHasFixedSize(true)
        binding.rvFollowList.isNestedScrollingEnabled = false

        profileViewModel.getUserDataList(targetUserId, followType)
        observeGetUserDataList()

        getFollowingData()

        observeAddFollowData()

        observeRemoveFollowData()

    }

    private fun observeRemoveFollowData() {
        profileViewModel.removeFollowData.observe(this) {state ->
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
//                    adapter.notifyItemChanged(position)
                }
            }
        }
    }

    private fun observeAddFollowData() {
        profileViewModel.addFollowData.observe(this) {state ->
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
//                    adapter.notifyItemChanged(position)
                }
            }
        }
    }

    private fun getFollowingData() {
        profileViewModel.getUserFollowingData(currentUserData().userId)
        profileViewModel.getUserFollowingData.observe(this) { state ->
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
                    adapter.updateFollowingList(state.data.toMutableList())
                }
            }
        }
    }

    private fun observeGetUserDataList() {
        profileViewModel.getUserDataList.observe(this) {state ->
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
                    userDataList = state.data.toMutableList()
                    isDataEmpty()
                }
            }
        }
    }

    private fun isDataEmpty() {
        if (userDataList.isEmpty()) {
            if (followType == "Following") {
                binding.rvFollowList.hide()
                binding.linearNoFollowMessage.show()
                binding.tvMessageTitle.setText(R.string.title_no_following)
                binding.tvMessageDescription.text = targetUserName.getFirstWord().limitTextLength() + " haven’t followed anyone"
            } else {
                binding.rvFollowList.hide()
                binding.linearNoFollowMessage.show()
                binding.tvMessageTitle.setText(R.string.title_no_followers)
                binding.tvMessageDescription.text = targetUserName.getFirstWord().limitTextLength() + " don’t have any followers"
            }

        } else {
            binding.rvFollowList.show()
            binding.linearNoFollowMessage.hide()
            adapter.updateList(userDataList)
            adapter.updateCurrentUser(currentUserData().userId)
        }
    }

    private fun currentUserData(): UserData {
        var user = UserData()
        profileViewModel.getSessionData {
            if (it != null) {
                user = it
            }
        }
        return user
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_grey)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        if (followType == "Following") binding.tvTitleToolbar.text = "Following"
        else binding.tvTitleToolbar.text = "Followers"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}