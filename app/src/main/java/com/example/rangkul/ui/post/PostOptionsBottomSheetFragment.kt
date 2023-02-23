package com.example.rangkul.ui.post

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rangkul.R
import com.example.rangkul.data.model.PostData
import com.example.rangkul.data.model.ReportData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.databinding.DialogBottomOptionsBinding
import com.example.rangkul.databinding.DialogBottomReportOptionsBinding
import com.example.rangkul.databinding.DialogBottomReportSuccessfulBinding
import com.example.rangkul.ui.profile.ProfileViewModel
import com.example.rangkul.ui.profile.VisitedProfileActivity
import com.example.rangkul.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

// This is a Bottom Sheet Dialog Fragment for handle action in post options
@AndroidEntryPoint
class PostOptionsBottomSheetFragment(private val deletePostStatusListener: PostOptionsStatusListener) : BottomSheetDialogFragment(){

    private var _binding: DialogBottomOptionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var objectPost: PostData
    private var isUserBeingFollowed = false
    private val viewModel: PostOptionsViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var reportOptionsAdapter: ReportOptionsAdapter
    private lateinit var selectedReport: String
    private val reportOptionsData: List<Map<String, String>>
        get() {
            val titleData = resources.getStringArray(R.array.title_report_options)
            val descData = resources.getStringArray(R.array.desc_report_options)

            val lists = mutableListOf<Map<String, String>>()
            for (i in titleData.indices) {
                lists.add(mapOf("title" to titleData[i], "description" to descData[i]))
            }
            return lists
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        objectPost = arguments?.getParcelable("OBJECT_POST")!!
        _binding = DialogBottomOptionsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOptionsVisibility()

        isUserBeingFollowed()

        binding.tvDelete.setOnClickListener {
            showDeleteConfirmation()
        }

        binding.tvLikeCountVisibility.setOnClickListener {
            toast("Under development")
        }

        binding.tvCommentVisibility.setOnClickListener {
            toast("Under development")
        }

        binding.tvReport.setOnClickListener {
            showReportPostOptions()
        }

        binding.tvFollow.setOnClickListener {
            if (isUserBeingFollowed) {
                binding.tvFollow.isClickable = false
                removeFollowData()
            } else {
                binding.tvFollow.isClickable = false
                addFollowData()
            }
        }

        binding.tvSeeAccount.setOnClickListener {
            dismiss()
            val intent = Intent(requireContext(), VisitedProfileActivity::class.java)
            intent.putExtra("USER_ID", objectPost.createdBy)
            startActivity(intent)
        }

    }

    private fun showReportPostOptions() {
        // Init Views
        val reportOptionsBinding = DialogBottomReportOptionsBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(reportOptionsBinding.root)
        dialog.show()

        reportOptionsBinding.btReport.isEnabled = false
        reportOptionsAdapter =
            ReportOptionsAdapter(
                reportOptionsList = reportOptionsData,
                onItemClicked = { item ->
                    selectedReport = item
                    reportOptionsBinding.btReport.isEnabled = true
                }
            )

        // Configure Report Options RecyclerView
        reportOptionsBinding.rvReportOptions.layoutManager = LinearLayoutManager(requireContext())
        reportOptionsBinding.rvReportOptions.setHasFixedSize(true)
        reportOptionsBinding.rvReportOptions.isNestedScrollingEnabled = false
        reportOptionsBinding.rvReportOptions.adapter = reportOptionsAdapter

        reportOptionsBinding.btReport.setOnClickListener {
            // set progressBar to visible
            reportOptionsBinding.tvReport.hide()
            reportOptionsBinding.progressBar.show()
            reportOptionsBinding.btReport.isClickable = false // Prevent multiple clicks when the task has started

            viewModel.addReportData(
                ReportData(
                    reportId = "",
                    reportedAt = Date(),
                    reportedBy = currentUserData().userId,
                    reportedType = "post",
                    reportedId = objectPost.postId,
                    reason = selectedReport,
                    status = "waiting"
                )
            ) { state ->

                when(state) {
                    is UiState.Loading -> {}

                    is UiState.Failure -> {
                        reportOptionsBinding.tvReport.show()
                        reportOptionsBinding.progressBar.hide()
                        toast(state.error)
                        reportOptionsBinding.btReport.isClickable = true
                    }

                    is UiState.Success -> {
                        dismiss() // Dismiss post options bottom sheet
                        dialog.dismiss() // Dismiss report options bottom sheet
                        if (state.data) { // Check is user already report this post before
                            toast("You already reported this post")
                        } else showReportSuccessful()
                    }
                }
            }
        }
    }

    private fun showReportSuccessful() {
        val reportSuccessfulBinding = DialogBottomReportSuccessfulBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(reportSuccessfulBinding.root)
        dialog.show()

        reportSuccessfulBinding.btDone.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun removeFollowData() {
        binding.progressBar.show()
        profileViewModel.removeFollowData(currentUserData().userId, objectPost.createdBy) { state ->
            when(state) {
                is UiState.Loading -> {}

                is UiState.Failure -> {
                    binding.progressBar.hide()
                    binding.tvFollow.isClickable = true
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.progressBar.hide()
                    binding.tvFollow.isClickable = true
                    profileViewModel.getProfileCountData(objectPost.createdBy, "Public")
                    isUserBeingFollowed() // Update follow button
                }
            }
        }
    }

    private fun addFollowData() {
        binding.progressBar.show()
        profileViewModel.addFollowData(currentUserData().userId, objectPost.createdBy) { state ->
            when(state) {
                is UiState.Loading -> {}

                is UiState.Failure -> {
                    binding.progressBar.hide()
                    binding.tvFollow.isClickable = true
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.progressBar.hide()
                    binding.tvFollow.isClickable = true
                    profileViewModel.getProfileCountData(objectPost.createdBy, "Public") // Reload profile count data
                    isUserBeingFollowed() // Update follow button
                }
            }
        }
    }

    private fun isUserBeingFollowed() {
        profileViewModel.isUserBeingFollowed(currentUserData().userId, objectPost.createdBy) {
            if (it) {
                isUserBeingFollowed = true
                setFollowTextView(true)
            } else {
                isUserBeingFollowed = false
                setFollowTextView(false)
            }
        }
    }

    private fun setFollowTextView(isFollowed: Boolean) {
        if (isFollowed) {
            binding.tvFollow.text = "Unfollow " + objectPost.userName.getFirstWord().limitTextLength()
            binding.tvFollow.setCompoundDrawablesWithIntrinsicBounds(
                ResourcesCompat.getDrawable(resources, R.drawable.ic_unfollow, null),
                null,
                null,
                null
            )
        } else {
            binding.tvFollow.text = "Follow " + objectPost.userName.getFirstWord().limitTextLength()
            binding.tvFollow.setCompoundDrawablesWithIntrinsicBounds(
                ResourcesCompat.getDrawable(resources, R.drawable.ic_follow, null),
                null,
                null,
                null
            )
        }
    }

    private fun showDeleteConfirmation() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_delete_confirmation)

        val tvCancel = dialog.findViewById<TextView>(R.id.tvCancel)
        val tvDelete = dialog.findViewById<TextView>(R.id.tvDelete)
        val progressBar = dialog.findViewById<ProgressBar>(R.id.progressBar)

        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        tvDelete.setOnClickListener {
            tvDelete.isClickable = false // Prevent multiple touch
            viewModel.deletePost(objectPost)

            viewModel.deletePost.observe(viewLifecycleOwner) {state ->
                when(state) {
                    is UiState.Loading -> {
                        progressBar.show()
                    }

                    is UiState.Failure -> {
                        progressBar.hide()
                        toast(state.error)
                        dialog.dismiss()
                        dismiss()
                    }

                    is UiState.Success -> {
                        progressBar.hide()
                        toast(state.data)
                        deletePostStatusListener.deletePostStatus(true)
                        dialog.dismiss()
                        dismiss()
                    }
                }
            }
        }

        dialog.show()

    }

    private fun setOptionsVisibility() {
        if (objectPost.createdBy == currentUserData().userId) {
            binding.tvDelete.show()
            binding.tvLikeCountVisibility.show()
            binding.tvCommentVisibility.show()
            binding.tvReport.hide()
            binding.tvFollow.hide()
            binding.tvSeeAccount.hide()
        } else if (objectPost.type == "Anonymous") {
            binding.tvDelete.hide()
            binding.tvLikeCountVisibility.hide()
            binding.tvCommentVisibility.hide()
            binding.tvReport.show()
            binding.tvFollow.hide()
            binding.tvSeeAccount.hide()
        }
        else {
            binding.tvDelete.hide()
            binding.tvLikeCountVisibility.hide()
            binding.tvCommentVisibility.hide()
            binding.tvReport.show()
            binding.tvFollow.show()
            binding.tvSeeAccount.show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface PostOptionsStatusListener {
        fun deletePostStatus(status: Boolean?)
    }

}