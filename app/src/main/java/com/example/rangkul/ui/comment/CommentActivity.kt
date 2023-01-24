package com.example.rangkul.ui.comment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rangkul.R
import com.example.rangkul.data.model.CommentData
import com.example.rangkul.data.model.PostData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.databinding.ActivityPostCommentBinding
import com.example.rangkul.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CommentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostCommentBinding
    private val viewModel: CommentViewModel by viewModels()
    private var objectPost: PostData? = null
    private val adapter by lazy {
        CommentAdapter(
            onOptionsCommentClicked = { pos, item ->
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        objectPost = intent.getParcelableExtra("OBJECT_POST")

        setToolbar()
        setObjectPost()

        // Configure Comment RecyclerView
        binding.rvComment.adapter = adapter
        binding.rvComment.layoutManager = LinearLayoutManager(this)
        binding.rvComment.setHasFixedSize(true)
        binding.rvComment.isNestedScrollingEnabled = true

        // Get Comment
        viewModel.getComments(objectPost!!.postId)
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
                    binding.progressBar.hide()
                    adapter.updateList(state.data.toMutableList())
                }
            }
        }

        // Add Comment
        binding.tvCommentPost.setOnClickListener {
            if (inputValidation()) {
                viewModel.addComment(
                    CommentData(
                        commentId = "",
                        commentedBy = currentUserData()!!.userId,
                        commentedAt = Date(),
                        comment = binding.etComment.text.toString(),
                        userName = currentUserData()!!.userName,
                        profilePicture = currentUserData()!!.profilePicture,
                        userBadge =currentUserData()!!.badge
                    ), objectPost!!.postId
                )
            }
            binding.etComment.text?.clear()
        }

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
                    toast(state.data)
                }
            }
        }
    }

    private fun currentUserData(): UserData? {
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
            text = if (objectPost?.userName?.length!! > 20) {
                "${objectPost?.userName?.substring(0,20)}..."
            } else {
                objectPost?.userName
            }
        }
        binding.tvCategoryPost.text = objectPost?.category
        binding.tvTimePost.text = sdf.format(objectPost?.createdAt!!)
        binding.tvCaptionPost.text = objectPost?.caption

        // Set User Badge
        binding.ivUserBadgePost.apply {
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