package com.example.rangkul.ui.home

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rangkul.data.model.LikeData
import com.example.rangkul.data.model.PostData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.databinding.ActivityUserLikedBinding
import com.example.rangkul.ui.comment.CommentActivity
import com.example.rangkul.ui.post.PostAdapter
import com.example.rangkul.ui.post.PostOptionsBottomSheetFragment
import com.example.rangkul.ui.post.PostViewModel
import com.example.rangkul.ui.profile.VisitedProfileActivity
import com.example.rangkul.utils.UiState
import com.example.rangkul.utils.hide
import com.example.rangkul.utils.show
import com.example.rangkul.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class UserLikedActivity :
    AppCompatActivity(),
    PostOptionsBottomSheetFragment.PostOptionsStatusListener,
    PostAdapter.PostStatusListener {

    private lateinit var binding: ActivityUserLikedBinding
    private var postList: MutableList<PostData> = arrayListOf()
    private var currentItemPosition = -1
    private val postViewModel: PostViewModel by viewModels()
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
            onProfileClicked = { _, item ->
                if (item == currentUserData().userId) {
                    // navigate to profile fragment
                } else {
                    val intent = Intent(this, VisitedProfileActivity::class.java)
                    intent.putExtra("USER_ID", item)
                    startActivity(intent)
                }
            },
            postStatusListener = this,
            context = this
        )
    }
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Update the comment count at selected post position
            val commentAddedAmount = result.data?.getIntExtra("COMMENT_ADDED", 0)
            commentAddedAmount?.let { updateCommentCount(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserLikedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()

        binding.srlUserLikedActivity.setOnRefreshListener {
            postViewModel.getLikedPosts(currentUserData().userId)
        }

        // Configure Post RecyclerView
        binding.rvPost.adapter = postAdapter
        binding.rvPost.layoutManager = LinearLayoutManager(this)
        binding.rvPost.setHasFixedSize(true)
        binding.rvPost.isNestedScrollingEnabled = true

        // Get user liked posts
        postViewModel.getLikedPosts(currentUserData().userId)
        observeGetLikedPosts()

    }

    private fun updateCommentCount(amount: Int) {
        postAdapter.updateCommentCount(currentItemPosition, amount)
    }

    private fun observeGetLikedPosts() {
        postViewModel.getLikedPosts.observe(this) {state ->
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
                    binding.srlUserLikedActivity.isRefreshing = false // hide swipe refresh loading
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
            postAdapter.updateCurrentUser(currentUserData().userId)
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
                    setResult(101) // Notify HomeFragment to update post
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

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(com.example.rangkul.R.drawable.ic_back_grey)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    // if post deleted, then notify the adapter
    override fun deletePostStatus(status: Boolean?) {
        if (status == true) {
            postList.removeAt(currentItemPosition)
            postAdapter.updateList(postList)
            postAdapter.notifyItemChanged(currentItemPosition)
            setResult(101) // Notify HomeFragment to update post
        }
    }

    override fun isPostBeingLiked(item: String, position: Int, callback: (Boolean) -> Unit) {
        postViewModel.isPostBeingLiked(currentUserData().userId, item) {
            callback.invoke(it)
        }
    }
}