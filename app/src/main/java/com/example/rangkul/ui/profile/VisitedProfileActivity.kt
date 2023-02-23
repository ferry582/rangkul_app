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
import com.example.rangkul.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class VisitedProfileActivity : AppCompatActivity(),
    PostOptionsBottomSheetFragment.PostOptionsStatusListener,
    PostAdapter.PostStatusListener {

    private lateinit var binding: ActivityVisitedProfileBinding
    private lateinit var visitedUserId: String
    private lateinit var visitedUserName: String
    private var postList: MutableList<PostData> = arrayListOf()
    private var currentItemPosition = -1
    private val postViewModel: PostViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val postAdapter by lazy {
        PostAdapter(
            onCommentClicked = { pos, item ->
                val intent = Intent(this, CommentActivity::class.java)
                intent.putExtra("OBJECT_POST", item)
                currentItemPosition = pos
                resultLauncher.launch(intent)
            },
            onLikeClicked = { pos, item ->
                addLike(item, pos)
            },
            onOptionClicked = { pos, item ->
                val postOptionsBottomDialogFragment = PostOptionsBottomSheetFragment(this)

                val bundle = Bundle()
                bundle.putParcelable("OBJECT_POST", item)
                currentItemPosition = pos
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
            postStatusListener = this,
            context = this
        )
    }
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // If the user just back from CommentActivity, then reload/call the getPosts method to refresh the comment count
        if (result.resultCode == Activity.RESULT_OK) {
            // Update the comment count at selected post position
            val commentAddedAmount = result.data?.getIntExtra("COMMENT_ADDED", 0)
            commentAddedAmount?.let { updateCommentCount(it) }
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
        binding.rvPost.adapter = postAdapter
        binding.rvPost.layoutManager = LinearLayoutManager(this)
        binding.rvPost.setHasFixedSize(true)
        binding.rvPost.isNestedScrollingEnabled = false

        postViewModel.getUserPosts("Public",  visitedUserId)
        observeGetUserPosts()

        binding.btFollow.setOnClickListener {
            binding.btFollow.isClickable = false
            addFollowData()
        }

        binding.btUnfollow.setOnClickListener {
            binding.btUnfollow.isClickable = false
            removeFollowData()
        }

        binding.btMessage.setOnClickListener {
            toast("Under development")
        }

        binding.btProfileOptions.setOnClickListener {
            val profileOptionsBottomDialogFragment = VisitedProfileOptionsBottomSheetFragment()

            val bundle = Bundle()
            bundle.putString("VISITED_UID", visitedUserId)
            profileOptionsBottomDialogFragment.arguments = bundle

            profileOptionsBottomDialogFragment.show(
                supportFragmentManager,
                "PostOptionsBottomSheetFragment"
            )
        }

        binding.linearFollowingCount.setOnClickListener {
            val intent = Intent(this, FollowListActivity::class.java)
            intent.putExtra("USER_ID", visitedUserId)
            intent.putExtra("FOLLOW_TYPE", "Following")
            intent.putExtra("USER_NAME", visitedUserName)
            startActivity(intent)
        }

        binding.linearFollowersCount.setOnClickListener {
            val intent = Intent(this, FollowListActivity::class.java)
            intent.putExtra("USER_ID", visitedUserId)
            intent.putExtra("FOLLOW_TYPE", "Followers")
            intent.putExtra("USER_NAME", visitedUserName)
            startActivity(intent)
        }
    }

    private fun updateCommentCount(amount: Int) {
        postAdapter.updateCommentCount(currentItemPosition, amount)
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
                    binding.tvFollowingCount.text = state.data.following.toString()
                    binding.tvFollowersCount.text = state.data.followers.toString()
                }
            }
        }
    }

    private fun removeFollowData() {
        binding.progressBar.show()
        profileViewModel.removeFollowData(currentUserData().userId, visitedUserId) { state ->
            when(state) {
                is UiState.Loading -> {}

                is UiState.Failure -> {
                    binding.progressBar.hide()
                    binding.btUnfollow.isClickable = true
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.progressBar.hide()
                    binding.btUnfollow.isClickable = true
                    profileViewModel.getProfileCountData(visitedUserId, "Public")
                    isUserBeingFollowed() // Update follow button
                    setResult(Activity.RESULT_OK) // Notify FollowListActivity to reload the data
                }
            }
        }
    }

    private fun addFollowData() {
        binding.progressBar.show()
        profileViewModel.addFollowData(currentUserData().userId, visitedUserId) { state ->
            when(state) {
                is UiState.Loading -> {}

                is UiState.Failure -> {
                    binding.progressBar.hide()
                    binding.btFollow.isClickable = true
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.progressBar.hide()
                    binding.btFollow.isClickable = true
                    profileViewModel.getProfileCountData(visitedUserId, "Public") // Reload profile count data
                    isUserBeingFollowed() // Update follow button
                    setResult(Activity.RESULT_OK) // Notify FollowListActivity to reload the data
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
            postAdapter.clearData()
            postAdapter.updateList(postList)
        }
    }

    private fun addLike(item: String, position: Int) {
        // Add Like
        binding.progressBar.show()
        postViewModel.addLike(
            LikeData(
                likeId = "",
                likedAt = Date(),
            ), item, currentUserData().userId
        ) { state ->
            when(state) {
                is UiState.Loading -> {}

                is UiState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.progressBar.hide()
                    postAdapter.dataUpdated(position)
                    if (state.data == "Liked") postAdapter.updateLikeCount(position, 1)
                    else postAdapter.updateLikeCount(position, -1)
                }
            }
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
        visitedUserName = visitedUserData?.userName!! // Will be passed to FollowListActivity

        // Set no post message textview
        binding.tvMessageDescription.text =
            visitedUserData.userName.getFirstWord().limitTextLength() + " hasn't posted anything yet"

        binding.tvUserName.text = visitedUserData.userName
        binding.ivUserBadgePost.apply {
            when (visitedUserData.badge) {
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
            if (visitedUserData.profilePicture.isNullOrEmpty()) setImageResource(R.drawable.ic_profile_picture_default)
            else {
                Glide
                    .with(context)
                    .load(visitedUserData.profilePicture)
                    .placeholder(R.drawable.ic_profile_picture_default)
                    .error(R.drawable.ic_baseline_error_24)
                    .into(binding.civProfilePicture)
            }
        }
        binding.tvProfileBio.apply {
            if (visitedUserData.bio.isNullOrEmpty()) {
                hide()
            }
            else {
                show()
                text = visitedUserData.bio
            }
        }
    }

    private fun isUserBeingFollowed() {
        profileViewModel.isUserBeingFollowed(currentUserData().userId, visitedUserId) {
            if (it) {
                binding.btFollow.hide()
                binding.btUnfollow.show()
            } else {
                binding.btFollow.show()
                binding.btUnfollow.hide()
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

    override fun deletePostStatus(status: Boolean?) {}

    override fun isPostBeingLiked(item: String, position: Int, callback: (Boolean) -> Unit) {
        postViewModel.isPostBeingLiked(currentUserData().userId, item) {
            callback.invoke(it)
        }
    }
}