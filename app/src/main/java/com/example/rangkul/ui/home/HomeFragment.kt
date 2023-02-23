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
import com.example.rangkul.ui.profile.VisitedProfileActivity
import com.example.rangkul.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HomeFragment :
    Fragment(),
    PostOptionsBottomSheetFragment.PostOptionsStatusListener,
    PostAdapter.PostStatusListener {

    lateinit var binding: FragmentHomeBinding
    private val viewModel: PostViewModel by viewModels()
    private var selectedType = "All"
    private var postList: MutableList<PostData> = arrayListOf()
    private var currentItemPosition = -1
    private val adapter by lazy {
        PostAdapter(
            onCommentClicked = { pos, item ->
                val intent = Intent(requireContext(), CommentActivity::class.java)
                intent.putExtra("OBJECT_POST", item)
                currentItemPosition = pos
                resultLauncher.launch(intent)
            },
            onLikeClicked = { pos, item ->
                addLike(item, pos)
            },
            onOptionClicked = { pos, item ->
                val postOptionsBottomDialogFragment = PostOptionsBottomSheetFragment(this)

                val bundle = Bundle()
                bundle.putParcelable("OBJECT_POST", item)
                currentItemPosition = pos
                postOptionsBottomDialogFragment.arguments = bundle

                postOptionsBottomDialogFragment.show(
                    childFragmentManager,
                    "PostOptionsBottomSheetFragment"
                )

            },
            onBadgeClicked = { pos, item ->

            },
            onProfileClicked = { _, item ->
                if (item == currentUserData().userId) {
                    // navigate to profile fragment
                } else {
                    val intent = Intent(requireContext(), VisitedProfileActivity::class.java)
                    intent.putExtra("USER_ID", item)
                    startActivity(intent)
                }
            },
            postStatusListener = this,
            context = requireContext()
        )
    }
    // If the user just back from CommentActivity, then reload/call the getPosts method to refresh the comment count
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Update the comment count at selected post position
            val commentAddedAmount = result.data?.getIntExtra("COMMENT_ADDED", 0)
            commentAddedAmount?.let { updateCommentCount(it) }
        } else if (result.resultCode == 101) {
            // Update entire post after user make changes in UserLikedActivity
            viewModel.getPosts(selectedType, currentUserData().userId)
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
            viewModel.getPosts(selectedType, currentUserData().userId)
        }

        binding.btYouLiked.setOnClickListener {
            val intent = Intent(requireContext(), UserLikedActivity::class.java)
            resultLauncher.launch(intent)
        }

        binding.btMessage.setOnClickListener {
            toast("Under Development")
        }

        setMessage()

        // Get posts based on type
        binding.chipForYou.setOnClickListener {
            selectedType = "All"
            viewModel.getPosts(selectedType, currentUserData().userId)
        }
        binding.chipFollowing.setOnClickListener {
            selectedType = "Following"
            viewModel.getPosts(selectedType, currentUserData().userId)
        }
        binding.chipSpeakUp.setOnClickListener {
            selectedType = "Anonymous"
            viewModel.getPosts(selectedType, currentUserData().userId)
        }

        // Configure Post RecyclerView
        binding.rvPost.adapter = adapter
        binding.rvPost.layoutManager = LinearLayoutManager(context)
        binding.rvPost.setHasFixedSize(true)
        binding.rvPost.isNestedScrollingEnabled = false

        // Get post list
        viewModel.getPosts(selectedType, currentUserData().userId)
        observeGetPosts()
    }

    private fun updateCommentCount(amount: Int) {
        adapter.updateCommentCount(currentItemPosition, amount)
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
                    isDataEmpty(postList)
                }
            }
        }
    }

    private fun isDataEmpty(data: List<Any>) {
        if (data.isEmpty()) {
            binding.rvPost.hide()
            binding.linearNoPostMessage.show()
        } else {
            binding.rvPost.show()
            binding.linearNoPostMessage.hide()
            adapter.clearData()
            adapter.updateList(postList)
            adapter.updateCurrentUser(currentUserData().userId)
        }
    }

    private fun addLike(item: String, position: Int) {
        // Add Like
        binding.progressBar.show()
        viewModel.addLike(
            LikeData(
                likeId = "",
                likedAt = Date(),
            ), item, currentUserData().userId
        ) { state ->
            when(state) {
                is UiState.Loading -> {}

                is UiState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.progressBar.hide()
                    adapter.dataUpdated(position)
                    if (state.data == "Liked") adapter.updateLikeCount(position, 1)
                    else adapter.updateLikeCount(position, -1)
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
        binding.tvUserName.text = currentUserData().userName.getFirstWord().limitTextLength() + "!"

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
    override fun deletePostStatus(status: Boolean?) {
        if (status == true) {
            postList.removeAt(currentItemPosition)
            adapter.updateList(postList)
            adapter.notifyItemChanged(currentItemPosition)
        }
    }

    override fun isPostBeingLiked(item: String, position: Int, callback: (Boolean) -> Unit) {
        viewModel.isPostBeingLiked(currentUserData().userId, item) {
            callback.invoke(it)
        }
    }

}