package com.example.rangkul.ui.createpost

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.rangkul.data.model.PostData
import com.example.rangkul.databinding.ActivityCreatePostBinding
import com.example.rangkul.ui.MainActivity
import com.example.rangkul.utils.*
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding
    private lateinit var selectedCategory: String
    private var postType: String = "Public" // Default post type
    private val viewModel: CreatePostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()

        // Set Category
        selectedCategory = intent.getStringExtra("SELECTED_CATEGORY").toString()
        binding.tvSelectedCategory.text = selectedCategory

        // Set User name
        viewModel.getSessionData {
            binding.tvUserName.text = it?.userName?.capitalizeWords()
        }

        binding.chipGroupPostType.setOnCheckedChangeListener { group, _ ->
            val ids = group.checkedChipIds
            for (id in ids) {
                val chip: Chip = group.findViewById(id!!)
                postType = chip.text.toString()

                when (chip.text) {
                    "Public" -> {
                        updateHeaderUI(chip.text.toString())
                        postType = chip.text.toString()
                    }

                    "Anonymous" -> {
                        updateHeaderUI(chip.text.toString())
                        postType = chip.text.toString()
                    }

                    else -> {
                       updateHeaderUI(chip.text.toString())
                        postType = chip.text.toString()
                    }
                }
            }

        }

        // publish post
        binding.btPublish.setOnClickListener {
            if (inputValidation()) {
                viewModel.getSessionData { user ->
                    viewModel.addPost(
                        PostData(
                            postId = "",
                            createdBy = user?.userId ?: "",
                            createdAt = Date(),
                            caption = binding.etCaption.text.toString(),
                            category = selectedCategory,
                            image = "null",
                            type = postType,
                            modifiedAt = Date(),
                            userName = user?.userName ?: "",
                            profilePicture = user?.profilePicture ?: "",
                            userBadge = user?.badge ?: "",
                            commentsCount = 0,
                            likesCount = 0
                        )
                    )
                }
            }
        }

        viewModel.addPost.observe(this) {state ->
            when(state) {
                is UiState.Loading -> {
                    loadingVisibility(true)
                }

                is UiState.Failure -> {
                    loadingVisibility(false)
                    toast(state.error)
                }

                is UiState.Success -> {
                    loadingVisibility(false)
                    val intent = Intent(this@CreatePostActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("PUBLISH_SUCCESSFUL", "true")
                    startActivity(intent)
                }
            }
        }
    }

    private fun loadingVisibility(isLoading: Boolean) {
        if (isLoading) {
            binding.tvPublish.hide()
            binding.progressBar.show()
        } else {
            binding.tvPublish.show()
            binding.progressBar.hide()
        }
    }

    private fun inputValidation(): Boolean {
        var isValid = true

        if (binding.etCaption.text.toString().isEmpty()) {
            isValid = false
            binding.etCaption.error = "Caption can't be empty!"
        }

        return isValid
    }

    private fun updateHeaderUI(type: String) {
        if (type == "Diary") {
            binding.clPublicAnonymous.hide()
            binding.clDiary.show()
        } else {
            binding.clPublicAnonymous.show()
            binding.clDiary.hide()
        }

    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(com.example.rangkul.R.drawable.ic_back_grey)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

}