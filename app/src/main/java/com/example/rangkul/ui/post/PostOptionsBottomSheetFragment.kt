package com.example.rangkul.ui.post

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
import com.example.rangkul.data.model.PostData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.databinding.DialogBottomPostOptionsBinding
import com.example.rangkul.utils.UiState
import com.example.rangkul.utils.hide
import com.example.rangkul.utils.show
import com.example.rangkul.utils.toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

// This is a Bottom Sheet Dialog Fragment for handle action in post options
@AndroidEntryPoint
class PostOptionsBottomSheetFragment(private val deleteStatusListener: DeleteStatusListener) : BottomSheetDialogFragment(){

    private var _binding: DialogBottomPostOptionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var objectPost: PostData
    private var postPosition: Int? = null
    private val viewModel: PostOptionsViewModel by viewModels()
//    private var mDeleteStatus: deleteStatusListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        objectPost = arguments?.getParcelable("OBJECT_POST")!!
        postPosition = arguments?.getInt("POST_POSITION")!!
        _binding = DialogBottomPostOptionsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOptionsVisibility()

        binding.tvDeletePost.setOnClickListener {
            showDeleteConfirmation()
        }

        binding.tvLikeCountOptions.setOnClickListener {

        }

    }

    private fun showDeleteConfirmation() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_delete_post_confirmation)

        val tvCancel = dialog.findViewById<TextView>(R.id.tvCancel)
        val tvDelete = dialog.findViewById<TextView>(R.id.tvDelete)
        val progressBar = dialog.findViewById<ProgressBar>(R.id.progressBar)

        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        tvDelete.setOnClickListener {
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
                        deleteStatusListener.deleteStatus(true, postPosition)
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
            binding.tvDeletePost.show()
            binding.tvLikeCountOptions.show()
            binding.tvCommentOptions.show()
            binding.tvReportPost.hide()
            binding.tvFollow.hide()
            binding.tvSeeAccount.hide()
        } else {
            binding.tvDeletePost.hide()
            binding.tvLikeCountOptions.hide()
            binding.tvCommentOptions.hide()
            binding.tvReportPost.show()
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

    interface DeleteStatusListener {
        fun deleteStatus(status: Boolean?, position: Int?)
    }


}