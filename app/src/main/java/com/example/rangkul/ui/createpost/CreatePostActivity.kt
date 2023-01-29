package com.example.rangkul.ui.createpost

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.github.dhaval2404.imagepicker.ImagePicker
import com.example.rangkul.data.model.PostData
import com.example.rangkul.data.model.UserData
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
    private var imageLocalUri: Uri? = null

    private val startForPostImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data
        when (resultCode) {
            Activity.RESULT_OK -> {
                val fileUri = data?.data!!
                imageLocalUri = fileUri
                binding.ivImagePost.setImageURI(imageLocalUri)
                binding.chipAddImage.hide()
                binding.chipChangePhoto.show()
                binding.chipDeletePhoto.show()
                binding.cvImagePost.show()
                loadingVisibility(false)
            }
            ImagePicker.RESULT_ERROR -> {
                loadingVisibility(false)
                toast("ImagePicker: ${ImagePicker.getError(data)}")
            }
            else -> {
                loadingVisibility(false)
                Log.e("CreatePostActivity","Task Cancelled")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()

        // Set Category
        selectedCategory = intent.getStringExtra("SELECTED_CATEGORY").toString()
        binding.tvSelectedCategory.text = selectedCategory

        // Set User name
        binding.tvUserName.text = currentUserData().userName

        binding.chipGroupPostType.setOnCheckedStateChangeListener { group, _ ->
            val ids = group.checkedChipIds
            for (id in ids) {
                val chip: Chip = group.findViewById(id)
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

        binding.chipAddImage.setOnClickListener {
            startImagePicker()
        }

        binding.chipChangePhoto.setOnClickListener {
            startImagePicker()
        }

        binding.chipDeletePhoto.setOnClickListener {
            binding.chipAddImage.show()
            binding.chipChangePhoto.hide()
            binding.chipDeletePhoto.hide()
            binding.cvImagePost.hide()
            imageLocalUri = null
        }

        // publish post
        binding.btPublish.setOnClickListener {
            if (inputValidation()) {
                isPostImageExist()
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

    private fun startImagePicker() {
        loadingVisibility(true)
        ImagePicker.with(this)
            .cropSquare()
            .compress(1024)
            .galleryOnly()
            .createIntent { intent ->
                startForPostImageResult.launch(intent)
            }
    }

    private fun addPostData(imageUrl: String) {
        viewModel.addPost(
            PostData(
                postId = "",
                createdBy = currentUserData().userId,
                createdAt = Date(),
                caption = binding.etCaption.text.toString(),
                category = selectedCategory,
                image = imageUrl,
                type = postType,
                modifiedAt = Date(),
                userName = currentUserData().userName,
                profilePicture = currentUserData().profilePicture,
                userBadge = currentUserData().badge,
                commentsCount = 0,
                likesCount = 0
            )
        )
    }
    private fun isPostImageExist() {
        if (imageLocalUri != null){
            viewModel.onUploadPostImage(imageLocalUri!!){ state ->
                when (state) {
                    is UiState.Loading -> {
                        loadingVisibility(true)
                    }
                    is UiState.Failure -> {
                        loadingVisibility(false)
                        toast(state.error)
                    }
                    is UiState.Success -> {
                        loadingVisibility(false)
                        addPostData(state.data.toString())
                    }
                }
            }
        }else{
            addPostData("null")
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