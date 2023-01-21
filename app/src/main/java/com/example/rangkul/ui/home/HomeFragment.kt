package com.example.rangkul.ui.home

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.rangkul.databinding.FragmentHomeBinding
import com.example.rangkul.utils.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private val viewModel: PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getNotes()
        viewModel.post.observe(viewLifecycleOwner) {state ->
            when(state) {
                is UiState.Loading -> {
                    Log.e(TAG, "Loading")
                }

                is UiState.Failure -> {
                    Log.e(TAG, state.error.toString())
                }

                is UiState.Success -> {
                    state.data.forEach {
                        Log.e(TAG, it.toString())
                    }
                }
            }
        }
    }

}