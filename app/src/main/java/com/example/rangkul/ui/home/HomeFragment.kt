package com.example.rangkul.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rangkul.databinding.FragmentHomeBinding
import com.example.rangkul.utils.UiState
import com.example.rangkul.utils.hide
import com.example.rangkul.utils.show
import com.example.rangkul.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private val viewModel: PostViewModel by viewModels()
    private val adapter by lazy {
        HomePostAdapter(
            onCommentClicked = { pos, item ->

            },
            onLikeClicked = { pos, item ->

            },
            onOptionClicked = { pos, item ->

            }
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configure Post RecyclerView
        binding.rvPost.adapter = adapter
        binding.rvPost.layoutManager = LinearLayoutManager(context)
        binding.rvPost.setHasFixedSize(true)
        binding.rvPost.isNestedScrollingEnabled = true


        viewModel.getPosts()
        viewModel.post.observe(viewLifecycleOwner) {state ->
            when(state) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                }

                is UiState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.progressBar.hide()
                    adapter.updateList(state.data.toMutableList())
                }
            }
        }
    }

}