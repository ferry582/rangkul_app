package com.example.rangkul.ui.comment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.example.rangkul.R
import com.example.rangkul.data.model.CommentData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.databinding.DialogBottomCommentOptionsBinding
import com.example.rangkul.utils.UiState
import com.example.rangkul.utils.hide
import com.example.rangkul.utils.show
import com.example.rangkul.utils.toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

// This is a Bottom Sheet Dialog Fragment for handle action in comment options
@AndroidEntryPoint
class CommentOptionsBottomSheetFragment(private val deleteStatusListener: DeleteStatusListener) : BottomSheetDialogFragment(){

    private var _binding: DialogBottomCommentOptionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var objectComment: CommentData
    private var commentPosition: Int? = null
    private lateinit var postType: String
    private val viewModel: CommentOptionsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        objectComment = arguments?.getParcelable("OBJECT_COMMENT")!!
        commentPosition = arguments?.getInt("COMMENT_POSITION")!!
        postType = arguments?.getString("POST_TYPE")!!
        _binding = DialogBottomCommentOptionsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOptionsVisibility()
        setFollowTextView()

        binding.tvDeleteComment.setOnClickListener {
            showDeleteConfirmation()
        }

        binding.tvFollow.setOnClickListener {
            toast("Under development")
        }

        binding.tvSeeAccount.setOnClickListener {
            toast("Under development")
        }

        binding.tvReportComment.setOnClickListener {
            toast("Under development")
        }

    }

    private fun setFollowTextView() {
        binding.tvFollow.apply {
            // Get the first Name
            val firstName: String = if(objectComment.userName.contains(" ")) {
                val firstSpace: Int = objectComment.userName.indexOf(" ") // detect the first space character
                objectComment.userName.substring(0,firstSpace) // get everything unto the first space character
            } else {
                objectComment.userName
            }

            text =
                if (firstName.length > 20) {
                    "Follow ${firstName.substring(0,20)}..."
                } else {
                    "Follow $firstName"
                }
        }
    }

    private fun showDeleteConfirmation() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_delete_confirmation)

        val tvMessageTitle = dialog.findViewById<TextView>(R.id.tvTitle)
        val tvMessageDescription = dialog.findViewById<TextView>(R.id.tvDescription)
        val tvCancel = dialog.findViewById<TextView>(R.id.tvCancel)
        val tvDelete = dialog.findViewById<TextView>(R.id.tvDelete)
        val progressBar = dialog.findViewById<ProgressBar>(R.id.progressBar)

        tvMessageTitle.text = getString(R.string.delete_comment_title)
        tvMessageDescription.text = getString(R.string.delete_comment_description)

        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        tvDelete.setOnClickListener {
            tvDelete.isClickable = false // Prevent multiple touch
            viewModel.deleteComment(objectComment)

            viewModel.deleteComment.observe(viewLifecycleOwner) {state ->
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
                        deleteStatusListener.deleteStatus(true, commentPosition)
                        dialog.dismiss()
                        dismiss()
                    }
                }
            }
        }

        dialog.show()

    }

    private fun setOptionsVisibility() {
        if (objectComment.commentedBy == currentUserData().userId) {
            binding.tvFollow.hide()
            binding.tvSeeAccount.hide()
            binding.tvReportComment.hide()
            binding.tvDeleteComment.show()
        } else if (postType == "Anonymous") {
            binding.tvFollow.hide()
            binding.tvSeeAccount.hide()
            binding.tvReportComment.show()
            binding.tvDeleteComment.hide()
        } else {
            binding.tvFollow.show()
            binding.tvSeeAccount.show()
            binding.tvReportComment.show()
            binding.tvDeleteComment.hide()
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

    interface DeleteStatusListener {
        fun deleteStatus(status: Boolean?, position: Int?)
    }

}