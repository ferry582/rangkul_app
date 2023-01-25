package com.example.rangkul.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rangkul.data.model.LikeData
import com.example.rangkul.data.model.PostData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.databinding.FragmentHomeBinding
import com.example.rangkul.ui.comment.CommentActivity
import com.example.rangkul.ui.post.PostAdapter
import com.example.rangkul.ui.post.PostViewModel
import com.example.rangkul.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private val viewModel: PostViewModel by viewModels()
    private val adapter by lazy {
        PostAdapter(
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

        setMessage()

        // Get posts based on type
        binding.chipForYou.setOnClickListener {
            viewModel.getPosts("All", "null", "null")
        }
        binding.chipFollowing.setOnClickListener {

        }
        binding.chipSpeakUp.setOnClickListener {
            viewModel.getPosts("Anonymous", "null", "null")
        }

        // Configure Post RecyclerView
        binding.rvPost.adapter = adapter
        binding.rvPost.layoutManager = LinearLayoutManager(context)
        binding.rvPost.setHasFixedSize(true)
        binding.rvPost.isNestedScrollingEnabled = false

        //Get post list
        viewModel.getPosts("All", "null", "null")
        viewModel.getPosts.observe(viewLifecycleOwner) {state ->
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

    private fun setMessage() {
        val c = Calendar.getInstance()
        val timeOfDay = c[Calendar.HOUR_OF_DAY]

        binding.tvMessage.text =
            when (timeOfDay) {
                in 0..11 -> "Good Morning"
                in 12..15 -> "Good Afternoon"
                in 16..20 -> "Good Evening"
                in 21..23 -> "Good Night"
                else -> "Something wrong!"
        }

        // Get the first Name
        binding.tvUserName.apply {
            val firstName: String = if(currentUserData().userName.contains(" ")) {
                val firstSpace: Int = currentUserData().userName.indexOf(" ") // detect the first space character
                currentUserData().userName.substring(0,firstSpace) // get everything unto the first space character
            } else {
                currentUserData().userName
            }

            text =
                if (firstName.length > 20) {
                    "${firstName.substring(0,20)}...!"
                } else {
                    "${firstName}!"
                }
        }
    }

    private fun isPostLiked(item: PostData): Boolean {

        viewModel.getIsPostLiked(item.postId, currentUserData().userId)
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
        viewModel.addLike(
            LikeData(
                likedBy = "",
                likedAt = Date(),
            ), item.postId, currentUserData().userId
        )

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

    private fun currentUserData(): UserData {
        var user = UserData()
        viewModel.getSessionData {
            if (it != null) {
                user = it
            }
        }
        return user
    }
}