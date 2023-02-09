package com.example.rangkul.ui.comment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.rangkul.R
import com.example.rangkul.data.model.CommentData
import com.example.rangkul.data.model.PostData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.databinding.ActivityPostCommentBinding
import com.example.rangkul.ui.profile.VisitedProfileActivity
import com.example.rangkul.utils.UiState
import com.example.rangkul.utils.hide
import com.example.rangkul.utils.show
import com.example.rangkul.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CommentActivity : AppCompatActivity(), CommentOptionsBottomSheetFragment.DeleteStatusListener {

    private lateinit var binding: ActivityPostCommentBinding
    private val viewModel: CommentViewModel by viewModels()
    private var objectPost: PostData? = null
    private var commentList: MutableList<CommentData> = arrayListOf()
    private var isPostNeedReload = false
    private val adapter by lazy {
        CommentAdapter(
            onOptionsCommentClicked = { pos, item ->
                val commentOptionsBottomDialogFragment = CommentOptionsBottomSheetFragment(this)

                val bundle = Bundle()
                bundle.putParcelable("OBJECT_COMMENT", item)
                bundle.putInt("COMMENT_POSITION", pos)
                bundle.putString("POST_TYPE", objectPost?.type)
                commentOptionsBottomDialogFragment.arguments = bundle

                commentOptionsBottomDialogFragment.show(
                    supportFragmentManager,
                    "CommentOptionsBottomSheetFragment"
                )
            },
            onProfileClicked = { pos, item ->
                if (item == currentUserData().userId) {
                    // navigate to profile fragment
                } else {
                    val intent = Intent(this, VisitedProfileActivity::class.java)
                    intent.putExtra("USER_ID", item)
                    startActivity(intent)
                }
            }
        )
    }
    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (isPostNeedReload) setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        objectPost = intent.getParcelableExtra("OBJECT_POST")
        setObjectPost()

        setToolbar()

        binding.srlPostCommentActivity.setOnRefreshListener {
            viewModel.getComments(objectPost!!.postId)
            isPostNeedReload = true
        }

        // Configure Comment RecyclerView
        binding.rvComment.adapter = adapter
        binding.rvComment.layoutManager = LinearLayoutManager(this)
        binding.rvComment.setHasFixedSize(true)
        binding.rvComment.isNestedScrollingEnabled = false

        // Get Comment
        viewModel.getComments(objectPost!!.postId)
        observeGetComments()

        // Add Comment
        binding.tvCommentPost.setOnClickListener {
            if (inputValidation()) {
                viewModel.addComment(
                    CommentData(
                        commentId = "",
                        commentedBy = currentUserData().userId,
                        commentedAt = Date(),
                        comment = binding.etComment.text.toString(),
                        userName = currentUserData().userName,
                        profilePicture = currentUserData().profilePicture,
                        userBadge = currentUserData().badge,
                        postId = objectPost!!.postId
                    )
                )
            }
            binding.etComment.text?.clear()
        }

        observeAddComment()
    }

    private fun observeGetComments() {
        viewModel.getComments.observe(this) {state ->
            when(state) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                }

                is UiState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.srlPostCommentActivity.isRefreshing = false
                    binding.progressBar.hide()
                    if (state.data.isNotEmpty()) {
                        binding.rvComment.show()
                        binding.linearNoCommentMessage.hide()
                        commentList = state.data.toMutableList()
                        adapter.updateList(commentList)
                        adapter.updatePostType(objectPost!!)
                    } else {
                        binding.rvComment.hide()
                        binding.linearNoCommentMessage.show()
                    }
                }
            }
        }
    }

    private fun observeAddComment() {
        viewModel.addComment.observe(this) {state ->
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
                    isPostNeedReload = true
                    viewModel.getComments(objectPost!!.postId)
                }
            }
        }
    }

    private fun currentUserData(): UserData {
        var user = UserData()
        viewModel.getSessionData {
            if (it != null) {
                user = it
            }
        }
        return user
    }

    private fun inputValidation(): Boolean {
        var isValid = true

        if (binding.etComment.text.toString().isEmpty()) {
            isValid = false
            binding.etComment.error = "Caption can't be empty!"
        }

        return isValid
    }

    private fun setObjectPost() {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH)

        binding.tvUserNamePost.apply {
            text = if (objectPost?.type == "Anonymous") {
                if (objectPost?.createdBy == currentUserData().userId) {
                    "Anonymous (You)"
                } else {
                    "Anonymous"
                }
            } else {
                if (objectPost?.userName?.length!! > 20) {
                    "${objectPost?.userName?.substring(0,20)}..."
                } else {
                    objectPost?.userName
                }
            }
        }

        binding.tvCategoryPost.text = objectPost?.category
        binding.tvTimePost.text = sdf.format(objectPost?.createdAt!!)
        binding.tvCaptionPost.text = objectPost?.caption

        // Set User Badge
        binding.ivUserBadgePost.apply {
            if (objectPost?.type == "Anonymous") {
                hide()
            } else {
                when (objectPost?.userBadge) {
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
                show()
            }
        }

        // Set Profile Picture
        binding.civProfilePicturePost.apply {
            if (objectPost?.type == "Anonymous") {
                setImageResource(R.drawable.ic_profile_picture_anonymous)
            } else {
                if (objectPost?.profilePicture.isNullOrEmpty()) setImageResource(R.drawable.ic_profile_picture_default)
                else {
                    Glide
                        .with(context)
                        .load(objectPost?.profilePicture)
                        .placeholder(R.drawable.ic_profile_picture_default)
                        .error(R.drawable.ic_baseline_error_24)
                        .into(binding.civProfilePicturePost)
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

    // Handle back button at toolbar
    override fun onSupportNavigateUp(): Boolean {
        if (isPostNeedReload) setResult(Activity.RESULT_OK)
        finish()
        return true
    }

    override fun deleteStatus(status: Boolean?, position: Int?) {
        if (status == true) {
            position?.let { commentList.removeAt(it) }
            adapter.updateList(commentList)
            position?.let { adapter.notifyItemChanged(it) }
            isPostNeedReload = true
        }
    }
}