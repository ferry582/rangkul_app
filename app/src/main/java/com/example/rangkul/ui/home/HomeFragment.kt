package com.example.rangkul.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rangkul.data.model.LikeData
import com.example.rangkul.data.model.PostData
import com.example.rangkul.databinding.FragmentHomeBinding
import com.example.rangkul.ui.comment.CommentActivity
import com.example.rangkul.utils.UiState
import com.example.rangkul.utils.hide
import com.example.rangkul.utils.show
import com.example.rangkul.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private val viewModel: PostViewModel by viewModels()
    private val adapter by lazy {
        HomePostAdapter(
            onCommentClicked = { pos, item ->
                val intent = Intent(requireContext(), CommentActivity::class.java)
                intent.putExtra("OBJECT_POST", item)
                startActivity(intent)
            },
            onLikeClicked = { pos, item ->
                addLike(item)
            },
            onOptionClicked = { pos, item ->

            },
            onBadgeClicked = { pos, item ->

            },
            getIsPostLikedData = { pos, item ->
                isPostLiked(item)
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

        //Get post list
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

    private fun isPostLiked(item: PostData): Boolean {

        viewModel.getSessionData { user ->
            viewModel.getIsPostLiked(item.postId, user?.userId ?: "")
        }
        var isLiked = false

        viewModel.getIsPostLiked.observe(viewLifecycleOwner) {state ->
            when(state) {
                is UiState.Loading -> {
//                    binding.progressBar.show()
                }

                is UiState.Failure -> {
                    toast(state.error)
                }

                is UiState.Success -> {
                    isLiked = state.data
                }
            }
        }

        return isLiked
    }

    private fun addLike(item: PostData) {
        // Add Like
        viewModel.getSessionData { user ->
            viewModel.addLike(
                LikeData(
                    likedBy = "",
                    likedAt = Date(),
                ), item.postId, user?.userId ?: ""
            )
        }

        viewModel.addLike.observe(this) {state ->
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
                    toast(state.data)
                }
            }
        }
    }

}