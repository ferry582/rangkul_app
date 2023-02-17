package com.example.rangkul.ui.profile

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
import com.example.rangkul.data.model.DiaryData
import com.example.rangkul.databinding.DialogBottomDiaryOptionsBinding
import com.example.rangkul.utils.UiState
import com.example.rangkul.utils.hide
import com.example.rangkul.utils.show
import com.example.rangkul.utils.toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiaryOptionsBottomSheetFragment(private val deleteDiaryStatusListener: DeleteDiaryStatusListener) : BottomSheetDialogFragment(){

    private var _binding: DialogBottomDiaryOptionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var objectDiary: DiaryData
    private val viewModel: DiaryOptionsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        objectDiary = arguments?.getParcelable("OBJECT_DIARY")!!
        _binding = DialogBottomDiaryOptionsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDeleteDiary.setOnClickListener {
            showDeleteConfirmation()
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

        tvMessageTitle.text = getString(R.string.delete_diary_title)
        tvMessageDescription.text = getString(R.string.delete_diary_description)
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        tvDelete.setOnClickListener {
            viewModel.deleteDiary(objectDiary)

            viewModel.deleteDiary.observe(viewLifecycleOwner) {state ->
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
                        deleteDiaryStatusListener.deleteDiaryStatus(true)
                        dialog.dismiss()
                        dismiss()
                    }
                }
            }
        }

        dialog.show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface DeleteDiaryStatusListener {
        fun deleteDiaryStatus(status: Boolean?)
    }

}