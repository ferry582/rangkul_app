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
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.rangkul.R
import com.example.rangkul.data.model.DiaryData
import com.example.rangkul.data.model.ImageListData
import com.github.dhaval2404.imagepicker.ImagePicker
import com.example.rangkul.data.model.PostData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.databinding.ActivityCreatePostBinding
import com.example.rangkul.ui.MainActivity
import com.example.rangkul.utils.*
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class CreatePostActivity : AppCompatActivity(), SelectMoodBottomSheetFragment.SelectedMoodListener {

    private lateinit var binding: ActivityCreatePostBinding
    private lateinit var selectedCategory: String
    private var postType: String = "Public" // Default post type
    private val viewModel: CreatePostViewModel by viewModels()
    private var imageLocalUri: Uri? = null
    private var selectedMood: ImageListData? = null

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

    // Get selected mood from bottom sheet dialog fragment
    override fun selectedMood(selectedMood: ImageListData) {
        this.selectedMood = selectedMood
        binding.civEmoticon.setImageResource(selectedMood.image)
        binding.tvUserMood.text = selectedMood.name
        binding.tvUserMood.setTextColor(ContextCompat.getColor(this, R.color.black))
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

        // Set profile picture
        binding.civProfilePicture.apply {
            if (currentUserData().profilePicture.isNullOrEmpty()) setImageResource(R.drawable.ic_profile_picture_default)
            else {
                Glide
                    .with(context)
                    .load(currentUserData().profilePicture)
                    .placeholder(R.drawable.ic_profile_picture_default)
                    .error(R.drawable.ic_baseline_error_24)
                    .into(binding.civProfilePicture)
            }
        }

        binding.clDiary.setOnClickListener {
            val commentOptionsBottomDialogFragment = SelectMoodBottomSheetFragment(this)
            commentOptionsBottomDialogFragment.show(
                supportFragmentManager,
                "SelectMoodBottomSheetFragment"
            )
        }

        binding.chipGroupPostType.setOnCheckedStateChangeListener { group, _ ->
            val ids = group.checkedChipIds
            for (id in ids) {
                val chip: Chip = group.findViewById(id)
                postType = chip.text.toString()

                when (chip.text) {
                    "Public" -> {
                        updateUI(chip.text.toString())
                        postType = chip.text.toString()
                    }

                    "Anonymous" -> {
                        updateUI(chip.text.toString())
                        postType = chip.text.toString()
                    }

                    else -> {
                       updateUI(chip.text.toString())
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
            hideKeyboard()
            if (inputValidation()) {
                isPostImageExist()
            }
        }

        observeAddPost()

        observeAddDiary()
    }

    private fun observeAddDiary() {
        viewModel.addDiary.observe(this) {state ->
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
                    intent.putExtra("PUBLISH_DIARY_SUCCESSFUL", "true")
                    startActivity(intent)
                }
            }
        }
    }

    private fun observeAddPost() {
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
                    intent.putExtra("PUBLISH_POST_SUCCESSFUL", "true")
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

    private fun addDiaryData(imageUrl: String?) {
        viewModel.addDiary(
            DiaryData(
                diaryId = "",
                createdBy = currentUserData().userId,
                createdAt = Date(),
                caption = binding.etCaption.text.toString(),
                image = imageUrl,
                type = postType,
                modifiedAt = Date(),
                moodTitle = selectedMood!!.name,
                moodImage = selectedMood!!.image
            )
        )
    }

    private fun addPostData(imageUrl: String?) {
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
                likesCount = 0,
                likeVisibility = true,
                commentVisibility = true
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
                        if (postType == "Diary") addDiaryData(state.data.toString())
                        else addPostData(state.data.toString())
                    }
                }
            }
        }else{
            if (postType == "Diary") addDiaryData(null)
            else addPostData(null)
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

        if (postType == "Diary" && selectedMood == null) {
            isValid = false
            Snackbar.make(binding.root, "You haven't chosen any mood yet", Snackbar.LENGTH_SHORT).show()
        }

        if (binding.etCaption.text.toString().isEmpty()) {
            isValid = false
            binding.etCaption.error = "Caption can't be empty!"
        }

        return isValid
    }

    private fun updateUI(type: String) {
        if (type == "Diary") {
            binding.clPublicAnonymous.hide()
            binding.clDiary.show()
            binding.tvTitleToolbar.text = getString(R.string.title_create_post_diary)
        } else {
            binding.clPublicAnonymous.show()
            binding.clDiary.hide()
            if (type == "Public") binding.tvTitleToolbar.text = getString(R.string.title_create_post_public)
            else binding.tvTitleToolbar.text = getString(R.string.title_create_post_anonymous)
        }

    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_grey)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

}