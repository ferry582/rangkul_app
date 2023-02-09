package com.example.rangkul.ui.profile

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.rangkul.R
import com.example.rangkul.data.model.UserData
import com.example.rangkul.databinding.ActivityEditProfileBinding
import com.example.rangkul.databinding.DialogBottomProfilePictureOptionsBinding
import com.example.rangkul.utils.*
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: EditProfileViewModel by viewModels()
    private val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    private var isProfilePhotoChange: Boolean = false
    private var isUserDataUpdated: Boolean = false
    private var imageLocalUri: Uri? = null
    private val startForPostImageResult = registerForActivityResult(ActivityResultContracts
        .StartActivityForResult()) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data
        when (resultCode) {
            Activity.RESULT_OK -> {
                val fileUri = data?.data!!
                imageLocalUri = fileUri
                isProfilePhotoChange = true
                binding.civProfilePicture.setImageURI(imageLocalUri)
            }
            ImagePicker.RESULT_ERROR -> {
                toast("ImagePicker: ${ImagePicker.getError(data)}")
            }
            else -> {
                Log.e("CreatePostActivity","Task Cancelled")
            }
        }
    }
    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (isUserDataUpdated) setResult(100)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        setToolbar()

        initUserData()

        setGenderOptions()

        binding.civProfilePicture.setOnClickListener {
            showProfilePictureOptions()
        }

        binding.etBirtDate.setOnClickListener {
            setBirthDate()
        }

        binding.btSave.setOnClickListener {
            hideKeyboard()
            val newUserData: UserData = currentUserData()
            newUserData.userName = binding.etName.text.toString().trim()
            newUserData.bio = binding.etBio.text.toString().ifEmpty { null }
            newUserData.gender = binding.acGender.text.toString().ifEmpty { null }
            newUserData.birthDate =
                if (binding.etBirtDate.text.toString().isEmpty()) null
                else sdf.parse(binding.etBirtDate.text.toString())

            if (updateProfileValidation()) {
                newUserData.userName = newUserData.userName.capitalizeWords()

                if (isProfilePhotoChange) {
                    if (!currentUserData().profilePicture.isNullOrEmpty()) {
                        // Delete previous profile picture if exist
                        deletePreviousProfilePicture()
                    }

                    if (imageLocalUri != null){
                        viewModel.onUploadProfilePicture(imageLocalUri!!){ state ->
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
                                    newUserData.profilePicture = state.data.toString()
                                    isPhotoOrNameChange(newUserData)
                                }
                            }
                        }
                    } else{
                        newUserData.profilePicture = null
                        isPhotoOrNameChange(newUserData)
                    }
                } else {
                    if (currentUserData() == newUserData){
                        Snackbar.make(binding.root, "There are no changes! Please enter new data to update your profile",
                            Snackbar.LENGTH_SHORT).show()
                    } else {
                        isPhotoOrNameChange(newUserData)
                    }
                }
            }
        }
    }

    private fun isPhotoOrNameChange(newUserData: UserData) {
        /* Before update user profile, do validation is the profile picture or user name change, if this
        validation is not performed then the updateAllUserPostsData() and updateAllUserCommentsData()
        methods in repository will always be executed even if there is no change in the profile picture
        or user name */

        if (
            currentUserData().profilePicture != newUserData.profilePicture
            || currentUserData().userName != newUserData.userName
        ) {
            updateUserProfile(newUserData, true)
        } else {
            updateUserProfile(newUserData, false)
        }
    }

    private fun deletePreviousProfilePicture() {
        viewModel.deleteProfilePicture(currentUserData().profilePicture!!) { state ->
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
                    Log.w("DeleteProfilePhoto", state.data)
                }
            }
        }
    }

    private fun updateUserProfile(newUserData: UserData, isPhotoOrNameChange: Boolean) {
        viewModel.updateUserProfile(newUserData, isPhotoOrNameChange)

        viewModel.updateUserProfile.observe(this) {state ->
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
                    isUserDataUpdated = true
                    showSuccessDialog()
                }
            }
        }
    }

    private fun showSuccessDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_successful_message)

        val title = dialog.findViewById<TextView>(R.id.tvTitle)
        val description = dialog.findViewById<TextView>(R.id.tvDescription)

        title.setText(R.string.update_profile_success_title)
        description.setText(R.string.update_profile_success_description)

        dialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
        }, 2000)
    }

    private fun showProfilePictureOptions() {
        val optionsViewBinding = DialogBottomProfilePictureOptionsBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(optionsViewBinding.root)
        dialog.show()

        optionsViewBinding.tvNewProfilePhoto.setOnClickListener {
            startImagePicker()
            dialog.dismiss()
        }

        optionsViewBinding.tvDeletePhoto.setOnClickListener {
            if (!currentUserData().profilePicture.isNullOrEmpty()) {
                // When user delete existing user profile
                binding.civProfilePicture.setImageResource(R.drawable.ic_profile_picture_default)
                imageLocalUri = null
                isProfilePhotoChange = true
            } else {
                // When user has no user profile or profilePicture = null
                binding.civProfilePicture.setImageResource(R.drawable.ic_profile_picture_default)
                imageLocalUri = null
                isProfilePhotoChange = false
            }

            dialog.dismiss()
        }
    }

    private fun startImagePicker() {
        ImagePicker.with(this)
            .cropSquare()
            .compress(1024)
            .galleryOnly()
            .createIntent { intent ->
                startForPostImageResult.launch(intent)
            }
    }

    private fun updateProfileValidation(): Boolean {
        return if (binding.etName.text.isEmpty()) {
            binding.etName.error = "Name Can't be Empty"
            false
        } else if (!isOnlyLettersAndSpace(binding.etName.text.toString().trim())) {
            binding.etName.error = "Name should be contain alphabet only"
            false
        } else {
            true
        }

    }

    private fun isOnlyLettersAndSpace(name: String): Boolean {
        var x = true
        for(i in name) {
            if (!i.isLetter() && !i.isWhitespace()) {
                x = false
                break
            }
        }
        return x
    }

    private fun initUserData() {
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

        binding.etName.setText(currentUserData().userName)
        binding.etBio.setText(currentUserData().bio)
        binding.acGender.setText(currentUserData().gender)

        if (currentUserData().birthDate != null) {
            binding.etBirtDate.setText(sdf.format(currentUserData().birthDate!!))
        }

    }

    private fun setBirthDate() {
        val c = Calendar.getInstance()

        if (currentUserData().birthDate != null) {
            c.time = currentUserData().birthDate!!
        }

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonthOfYear, selectedDayOfMonth ->
                val selectedDateStr = "$selectedDayOfMonth/${selectedMonthOfYear + 1}/$selectedYear"

                val sdfParse = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                val date = sdfParse.parse(selectedDateStr)

                binding.etBirtDate.setText(sdf.format(date!!))
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun setGenderOptions() {
        val listGender = arrayListOf("Male", "Female")
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listGender)
        binding.acGender.setAdapter(genderAdapter)
    }

    private fun loadingVisibility(isLoading: Boolean) {
        if (isLoading) {
            binding.tvSave.hide()
            binding.progressBar.show()
        } else {
            binding.tvSave.show()
            binding.progressBar.hide()
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

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_grey)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    // Handle back button at toolbar
    override fun onSupportNavigateUp(): Boolean {
        if (isUserDataUpdated) setResult(100)
        finish()
        return true
    }

}