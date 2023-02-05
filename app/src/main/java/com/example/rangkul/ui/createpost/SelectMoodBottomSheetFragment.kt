package com.example.rangkul.ui.createpost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rangkul.R
import com.example.rangkul.data.model.ImageListData
import com.example.rangkul.databinding.DialogBottomSelectMoodBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectMoodBottomSheetFragment(private val selectedMoodListener: SelectedMoodListener) : BottomSheetDialogFragment(){

    private var _binding: DialogBottomSelectMoodBinding? = null
    private val binding get() = _binding!!
    lateinit var moodList: ArrayList<ImageListData>
    lateinit var moodListAdapter: MoodListAdapter
    private var selectedMood: ImageListData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogBottomSelectMoodBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configure Post RecyclerView
        binding.rvMoodList.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvMoodList.setHasFixedSize(true)
        binding.rvMoodList.isNestedScrollingEnabled = false

        moodList = ArrayList()
        addMoodListData()

        moodListAdapter =
            MoodListAdapter(
                moodList,
                onItemClicked = { item ->
                    selectedMood = item
                }
            )

        binding.rvMoodList.adapter = moodListAdapter

        binding.btNext.setOnClickListener {
            if (selectedMood != null) selectedMoodListener.selectedMood(selectedMood!!)
            dismiss()
        }

    }

    private fun addMoodListData() {
        moodList.add(ImageListData(R.drawable.emoticon_happy, "Happy"))
        moodList.add(ImageListData(R.drawable.emoticon_relaxed, "Relaxed"))
        moodList.add(ImageListData(R.drawable.emoticon_sad, "Sad"))
        moodList.add(ImageListData(R.drawable.emoticon_afraid, "Afraid"))
        moodList.add(ImageListData(R.drawable.emoticon_angry, "Angry"))
        moodList.add(ImageListData(R.drawable.emoticon_bored, "Bored"))
        moodList.add(ImageListData(R.drawable.emoticon_ashamed, "Ashamed"))
        moodList.add(ImageListData(R.drawable.emoticon_frustrated, "Frustrated"))
        moodList.add(ImageListData(R.drawable.emoticon_sick, "Sick"))
        moodList.add(ImageListData(R.drawable.emoticon_depressed, "Depressed"))
        moodList.add(ImageListData(R.drawable.emoticon_anxious, "Anxious"))
        moodList.add(ImageListData(R.drawable.emoticon_exhausted, "Exhausted"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface SelectedMoodListener {
        fun selectedMood(selectedMood: ImageListData)
    }

}