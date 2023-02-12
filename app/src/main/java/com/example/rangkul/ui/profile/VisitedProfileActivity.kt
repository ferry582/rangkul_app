package com.example.rangkul.ui.profile

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.rangkul.R
import com.example.rangkul.data.model.LikeData
import com.example.rangkul.data.model.PostData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.databinding.ActivityVisitedProfileBinding
import com.example.rangkul.ui.comment.CommentActivity
import com.example.rangkul.ui.post.PostAdapter
import com.example.rangkul.ui.post.PostOptionsBottomSheetFragment
import com.example.rangkul.ui.post.PostViewModel
import com.example.rangkul.utils.UiState
import com.example.rangkul.utils.hide
import com.example.rangkul.utils.show
import com.example.rangkul.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class VisitedProfileActivity : AppCompatActivity(),
    PostOptionsBottomSheetFragment.DeletePostStatusListener {

    private lateinit var binding: ActivityVisitedProfileBinding
    private lateinit var visitedUserId: String
    private var postList: MutableList<PostData> = arrayListOf()
    private val postViewModel: PostViewModel by viewModels()
    private val profileViewModel: VisitedProfileViewModel by viewModels()
    private val adapterPost by lazy {
        PostAdapter(
            onCommentClicked = { _, item ->
                val intent = Intent(this, CommentActivity::class.java)
                intent.putExtra("OBJECT_POST", item)
                resultLauncher.launch(intent)
            },
            onLikeClicked = { pos, item ->
                addLike(item)
            },
            onOptionClicked = { pos, item ->
                val postOptionsBottomDialogFragment = PostOptionsBottomSheetFragment(this)

                val bundle = Bundle()
                bundle.putParcelable("OBJECT_POST", item)
                bundle.putInt("POST_POSITION", pos)
                postOptionsBottomDialogFragment.arguments = bundle

                postOptionsBottomDialogFragment.show(
                    supportFragmentManager,
                    "PostOptionsBottomSheetFragment"
                )

            },
            onBadgeClicked = { pos, item ->

            },
            onProfileClicked = { _, _ ->
                // unable to click profile on any post from visited profile
            },
            context = this
        )
    }
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // If the user just back from CommentActivity, then reload/call the getPosts method to refresh the comment count
        if (result.resultCode == Activity.RESULT_OK) {
            postViewModel.getUserPosts("Public",  visitedUserId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitedProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        visitedUserId = intent.getStringExtra("USER_ID").toString()

        setToolbar()

        profileViewModel.getUserData(visitedUserId) // Get visited user data
        observeGetVisitedUserData()

        profileViewModel.getProfileCountData(visitedUserId, "Public")
        observeGetUserCountData()

        isUserBeingFollowed()

        binding.srlVisitedProfileActivity.setOnRefreshListener {
            postViewModel.getUserPosts("Public",  visitedUserId)
            profileViewModel.getProfileCountData(visitedUserId, "Public")
        }

        // Configure Post RecyclerView
        binding.rvPost.adapter = adapterPost
        binding.rvPost.layoutManager = LinearLayoutManager(this)
        binding.rvPost.setHasFixedSize(true)
        binding.rvPost.isNestedScrollingEnabled = false

        // Get user's like list
        isPostLiked()

        postViewModel.getUserPosts("Public",  visitedUserId)
        observeGetUserPosts()

        binding.btFollow.setOnClickListener {
            binding.btFollow.isClickable = false
            profileViewModel.addFollowData(currentUserData().userId, visitedUserId)
        }
        observeAddFollowData()

        binding.btUnfollow.setOnClickListener {
            binding.btUnfollow.isClickable = false
            profileViewModel.removeFollowData(currentUserData().userId, visitedUserId)
        }
        observeRemoveFollowData()

        binding.btMessage.setOnClickListener {
            toast("Under development")
        }
    }

    private fun observeGetUserCountData() {
        profileViewModel.getProfileCountData.observe(this) {state ->
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
                    binding.tvPostCount.text = state.data.post.toString()
                    binding.tvFollowingsCount.text = state.data.followings.toString()
                    binding.tvFollowersCount.text = state.data.followers.toString()
                }
            }
        }
    }

    private fun observeRemoveFollowData() {
        profileViewModel.removeFollowData.observe(this) {state ->
            when(state) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                }

                is UiState.Failure -> {
                    binding.progressBar.hide()
                    binding.btUnfollow.isClickable = true
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.progressBar.hide()
                    binding.btUnfollow.isClickable = true
                    profileViewModel.getProfileCountData(visitedUserId, "Public")
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
                    binding.btFollow.isClickable = true
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.progressBar.hide()
                    binding.btFollow.isClickable = true
                    profileViewModel.getProfileCountData(visitedUserId, "Public") // Reload profile count data
                }
            }
        }
    }

    private fun observeGetUserPosts() {
        postViewModel.getUserPosts.observe(this) {state ->
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
                    binding.srlVisitedProfileActivity.isRefreshing = false
                    postList = state.data.toMutableList()
                    isDataEmpty(postList)
                }
            }
        }
    }

    private fun isDataEmpty(data: List<Any>) {
        if (data.isEmpty()) {
            binding.rvPost.hide()
            binding.linearNoPostMessage.show()
        } else {
                binding.rvPost.show()
                binding.linearNoPostMessage.hide()
                adapterPost.updateList(postList)
        }
    }

    private fun observeGetVisitedUserData() {
        profileViewModel.getUserData.observe(this) {state ->
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

    private fun addLike(item: PostData) {
        // Add Like
        postViewModel.addLike(
            LikeData(
                likeId = "",
                likedAt = Date(),
            ), item.postId, currentUserData().userId
        )

        postViewModel.addLike.observe(this) {state ->
            when(state) {
                is UiState.Loading -> {
                }

                is UiState.Failure -> {
                    toast(state.error)
                }

                is UiState.Success -> {
                    postViewModel.getUserPosts("Public",  visitedUserId)
                }
            }
        }
    }

    private fun isPostLiked() {
        //Get Like data list at users collection
        postViewModel.getUserLikeData(currentUserData().userId)
        postViewModel.getUserLikeData.observe(this) {state ->
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
                    adapterPost.updateUserLikeDataList(state.data.toMutableList())
                }
            }
        }
    }

    private fun currentUserData(): UserData {
        var user = UserData()
        postViewModel.getSessionData {
            if (it != null) {
                user = it
            }
        }
        return user
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

    private fun isUserBeingFollowed() {
        profileViewModel.getUserFollowingsData(currentUserData().userId)
        profileViewModel.getUserFollowingsData.observe(this) { state ->
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
                    if (state.data.any {it.userId == visitedUserId}) {
                        binding.btFollow.hide()
                        binding.btUnfollow.show()
                    } else {
                        binding.btFollow.show()
                        binding.btUnfollow.hide()
                    }
                }
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

    override fun deletePostStatus(status: Boolean?, position: Int?) {}
}