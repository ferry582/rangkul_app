package com.example.rangkul.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.rangkul.R
import com.example.rangkul.data.model.LikeData
import com.example.rangkul.data.model.PostData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.databinding.FragmentHomeBinding
import com.example.rangkul.ui.comment.CommentActivity
import com.example.rangkul.ui.post.PostAdapter
import com.example.rangkul.ui.post.PostOptionsBottomSheetFragment
import com.example.rangkul.ui.post.PostViewModel
import com.example.rangkul.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment(), PostOptionsBottomSheetFragment.DeletePostStatusListener {

    lateinit var binding: FragmentHomeBinding
    private val viewModel: PostViewModel by viewModels()
    private var selectedType = "All"
    private var postList: MutableList<PostData> = arrayListOf()
    private val adapter by lazy {
        PostAdapter(
            onCommentClicked = { pos, item ->
                val intent = Intent(requireContext(), CommentActivity::class.java)
                intent.putExtra("OBJECT_POST", item)
                resultLauncher.launch(intent)
            },
            onLikeClicked = { pos, item ->
                addLike(item)
            },
            onOptionClicked = { pos, item ->
                val postOptionsBottomDialogFragment = PostOptionsBottomSheetFragment(this)

                val bundle = Bundle()
                bundle.putParcelable("OBJECT_POST", item)
                bundle.putInt("POST_POSITION", pos)
                postOptionsBottomDialogFragment.arguments = bundle

                postOptionsBottomDialogFragment.show(
                    childFragmentManager,
                    "PostOptionsBottomSheetFragment"
                )

            },
            onBadgeClicked = { pos, item ->

            },
            context = requireContext()
        )
    }
    // If the user just back from CommentActivity, then reload/call the getPosts method to refresh the comment count
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.getPosts(selectedType)
        }
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

        binding.srlHomeFragment.setOnRefreshListener {
            viewModel.getPosts(selectedType)
        }

        setMessage()

        // Get posts based on type
        binding.chipForYou.setOnClickListener {
            selectedType = "All"
            viewModel.getPosts(selectedType)
        }
        binding.chipFollowing.setOnClickListener {
            // call view model to retrieve only following posts
        }
        binding.chipSpeakUp.setOnClickListener {
            selectedType = "Anonymous"
            viewModel.getPosts(selectedType)
        }

        // Configure Post RecyclerView
        binding.rvPost.adapter = adapter
        binding.rvPost.layoutManager = LinearLayoutManager(context)
        binding.rvPost.setHasFixedSize(true)
        binding.rvPost.isNestedScrollingEnabled = false

        // Get user's like list
        isPostLiked()

        // Get post list
        viewModel.getPosts(selectedType)
        observeGetPosts()
    }

    private fun observeGetPosts() {
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
                    binding.srlHomeFragment.isRefreshing = false // hide swipe refresh loading
                    postList = state.data.toMutableList()
                    adapter.updateList(postList)
                    adapter.updateCurrentUser(currentUserData().userId)
                }
            }
        }
    }

    private fun addLike(item: PostData) {
        // Add Like
        viewModel.addLike(
            LikeData(
                likeId = "",
                likedAt = Date(),
            ), item.postId, currentUserData().userId
        )

        viewModel.addLike.observe(this) {state ->
            when(state) {
                is UiState.Loading -> {
                }

                is UiState.Failure -> {
                    toast(state.error)
                }

                is UiState.Success -> {
                    // Need to update this code, because it recall entire post everytime user addLike
                    viewModel.getPosts(selectedType)
                }
            }
        }
    }

    private fun isPostLiked() {
        //Get Like data list at users collection
        viewModel.getUserLikeData(currentUserData().userId)
        viewModel.getUserLikeData.observe(viewLifecycleOwner) {state ->
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
                    adapter.updateUserLikeDataList(state.data.toMutableList())
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

    // if post deleted, then notify the adapter
    override fun deletePostStatus(status: Boolean?, position: Int?) {
        if (status == true) {
            position?.let { postList.removeAt(it) }
            adapter.updateList(postList)
            position?.let { adapter.notifyItemChanged(it) }
        }
    }

}