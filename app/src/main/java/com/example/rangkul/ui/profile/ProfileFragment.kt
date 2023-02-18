package com.example.rangkul.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.rangkul.R
import com.example.rangkul.data.model.DiaryData
import com.example.rangkul.data.model.LikeData
import com.example.rangkul.data.model.PostData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.databinding.FragmentProfileBinding
import com.example.rangkul.ui.comment.CommentActivity
import com.example.rangkul.ui.post.PostAdapter
import com.example.rangkul.ui.post.PostOptionsBottomSheetFragment
import com.example.rangkul.ui.post.PostViewModel
import com.example.rangkul.utils.UiState
import com.example.rangkul.utils.hide
import com.example.rangkul.utils.show
import com.example.rangkul.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ProfileFragment : Fragment(),
    PostOptionsBottomSheetFragment.DeletePostStatusListener,
    DiaryOptionsBottomSheetFragment.DeleteDiaryStatusListener,
    PostAdapter.PostStatusListener {

    lateinit var binding: FragmentProfileBinding
    private val postViewModel: PostViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private var selectedType = "Public"
    private var postList: MutableList<PostData> = arrayListOf()
    private var diaryList: MutableList<DiaryData> = arrayListOf()
    private var currentItemPosition = -1
    private val postAdapter by lazy {
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
            onProfileClicked = { _, _ ->
                // Unable to click profile on any post from fragment profile
            },
            postStatusListener = this,
            context = requireContext()
        )
    }

    private val adapterDiary by lazy {
        DiaryAdapter (
            onOptionClicked = { pos, item ->
                val diaryOptionsBottomSheetFragment = DiaryOptionsBottomSheetFragment(this)

                val bundle = Bundle()
                bundle.putParcelable("OBJECT_DIARY", item)
                currentItemPosition = pos
                diaryOptionsBottomSheetFragment.arguments = bundle

                diaryOptionsBottomSheetFragment.show(
                    childFragmentManager,
                    "PostOptionsBottomSheetFragment"
                )
            },
            context = requireContext()
        )
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Update the comment count at selected post position
            val commentAddedAmount = result.data?.getIntExtra("COMMENT_ADDED", 0)
            commentAddedAmount?.let { updateCommentCount(it) }
        } else if (result.resultCode == 100) {
            // Restart fragment if user just edit the profile to get newest data from shared preference
            val fragmentId = findNavController().currentDestination?.id
            findNavController().popBackStack(fragmentId!!,true)
            findNavController().navigate(fragmentId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.srlProfileFragment.setOnRefreshListener {
            if (selectedType == "Diary")  postViewModel.getUserDiaries(currentUserData().userId)
            else postViewModel.getUserPosts(selectedType,  currentUserData().userId)
            profileViewModel.getProfileCountData(currentUserData().userId, selectedType)
        }

        setUserProfileData()

        profileViewModel.getProfileCountData(currentUserData().userId, selectedType)
        observeGetUserCountData()

        binding.btSettings.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.btEditProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            resultLauncher.launch(intent)
        }

        binding.linearFollowingCount.setOnClickListener {
            val intent = Intent(requireContext(), FollowListActivity::class.java)
            intent.putExtra("USER_ID", currentUserData().userId)
            intent.putExtra("FOLLOW_TYPE", "Following")
            intent.putExtra("USER_NAME", currentUserData().userName)
            startActivity(intent)
        }

        binding.linearFollowersCount.setOnClickListener {
            val intent = Intent(requireContext(), FollowListActivity::class.java)
            intent.putExtra("USER_ID", currentUserData().userId)
            intent.putExtra("FOLLOW_TYPE", "Followers")
            intent.putExtra("USER_NAME", currentUserData().userName)
            startActivity(intent)
        }

        // Configure Post RecyclerView
        binding.rvPost.adapter = postAdapter
        binding.rvPost.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPost.setHasFixedSize(true)
        binding.rvPost.isNestedScrollingEnabled = false

        // Get posts based on type
        // Use OnCheckedStateChangeListener to always sync the chip and post type
        binding.chipGroupPostType.setOnCheckedStateChangeListener { group, _ ->
            when (group.checkedChipId) {
                R.id.chipPublic -> {
                    selectedType = "Public"
                    postViewModel.getUserPosts(selectedType, currentUserData().userId)
                    binding.rvPost.adapter = postAdapter
                    profileViewModel.getProfileCountData(currentUserData().userId, selectedType)
                }

                R.id.chipAnonymous -> {
                    selectedType = "Anonymous"
                    postViewModel.getUserPosts(selectedType, currentUserData().userId)
                    binding.rvPost.adapter = postAdapter
                    profileViewModel.getProfileCountData(currentUserData().userId, selectedType)
                }

                else -> {
                    selectedType = "Diary"
                    postViewModel.getUserDiaries(currentUserData().userId)
                    binding.rvPost.adapter = adapterDiary
                    profileViewModel.getProfileCountData(currentUserData().userId, selectedType)
                }
            }
        }

        // Get post list based on the selected category
        postViewModel.getUserPosts(selectedType,  currentUserData().userId)
        observeGetUserPosts()
        observeGetUserDiaries()

    }

    private fun updateCommentCount(amount: Int) {
        postAdapter.updateCommentCount(currentItemPosition, amount)
    }

    private fun observeGetUserCountData() {
        profileViewModel.getProfileCountData.observe(viewLifecycleOwner) {state ->
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
                    binding.tvPostCount.text = state.data.post.toString()
                    binding.tvFollowingCount.text = state.data.following.toString()
                    binding.tvFollowersCount.text = state.data.followers.toString()
                }
            }
        }
    }

    private fun observeGetUserDiaries() {
        postViewModel.getUserDiaries.observe(viewLifecycleOwner) {state ->
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
                    binding.srlProfileFragment.isRefreshing = false
                    diaryList = state.data.toMutableList()
                    isDataEmpty(diaryList)
                }
            }
        }
    }

    private fun observeGetUserPosts() {
        postViewModel.getUserPosts.observe(viewLifecycleOwner) {state ->
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
                    binding.srlProfileFragment.isRefreshing = false
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
            when (selectedType) {
                "Anonymous" -> {
                    binding.tvMessageTitle.setText(R.string.title_no_post_message)
                    binding.tvMessageDescription.setText(R.string.description_no_anonymous_post_message)
                }
                "Public" -> {
                    binding.tvMessageTitle.setText(R.string.title_no_post_message)
                    binding.tvMessageDescription.setText(R.string.description_no_post_message)
                }
                else -> {
                    binding.tvMessageTitle.setText(R.string.title_no_diary_message)
                    binding.tvMessageDescription.setText(R.string.description_no_diary_message)
                }
            }

        } else {
            if (selectedType == "Diary") {
                binding.rvPost.show()
                binding.linearNoPostMessage.hide()
                adapterDiary.updateList(diaryList)
            } else {
                binding.rvPost.show()
                binding.linearNoPostMessage.hide()
                postAdapter.clearData()
                postAdapter.updateList(postList)
                postAdapter.updateCurrentUser(currentUserData().userId)
            }

        }
    }

    private fun addLike(item: String, position: Int) {
        // Add Like
        binding.progressBar.show()
        postViewModel.addLike(
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
                    postAdapter.dataUpdated(position)
                    if (state.data == "Liked") postAdapter.updateLikeCount(position, 1)
                    else postAdapter.updateLikeCount(position, -1)
                }
            }
        }
    }

    private fun setUserProfileData() {
        binding.tvUserName.text = currentUserData().userName
        binding.ivUserBadgePost.apply {
            when (currentUserData().badge) {
                "Trusted" -> {
                    setImageResource(R.drawable.ic_badge_trusted)
                }
                "Psychologist" -> {
                    setImageResource(R.drawable.ic_badge_psychologist)
                }
                else -> {
                    setImageResource(R.drawable.ic_badge_basic)
                }
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
        binding.tvProfileBio.apply {
            if (currentUserData().bio.isNullOrEmpty()) {
                hide()
            }
            else {
                show()
                text = currentUserData().bio
            }
        }
    }

    private fun currentUserData(): UserData {
        var user = UserData()
        postViewModel.getSessionData {
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
            postAdapter.updateList(postList)
            postAdapter.notifyItemChanged(currentItemPosition)
            profileViewModel.getProfileCountData(currentUserData().userId, selectedType) // update post count
        }
    }

    override fun deleteDiaryStatus(status: Boolean?) {
        if (status == true) {
            diaryList.removeAt(currentItemPosition)
            adapterDiary.updateList(diaryList)
            adapterDiary.notifyItemChanged(currentItemPosition)
            profileViewModel.getProfileCountData(currentUserData().userId, selectedType) // update post count
        }
    }

    override fun isPostBeingLiked(item: String, position: Int, callback: (Boolean) -> Unit) {
        postViewModel.isPostBeingLiked(currentUserData().userId, item) {
            callback.invoke(it)
        }
    }
}