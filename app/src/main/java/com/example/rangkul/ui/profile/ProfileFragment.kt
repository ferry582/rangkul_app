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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rangkul.R
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
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ProfileFragment : Fragment(), PostOptionsBottomSheetFragment.DeleteStatusListener {

    lateinit var binding: FragmentProfileBinding
    private val viewModelPost: PostViewModel by viewModels()
    private var selectedType = "Public"
    private var postList: MutableList<PostData> = arrayListOf()
    private val adapterPost by lazy {
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
            viewModelPost.getCurrentUserPosts(selectedType,  currentUserData().userId)
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

//        view.findViewById<AppCompatButton>(R.id.btSettings).setOnClickListener {
//            val intent = Intent(context, SettingsActivity::class.java)
//            startActivity(intent)
//        }

        binding.srlProfileFragment.setOnRefreshListener {
            viewModelPost.getCurrentUserPosts(selectedType,  currentUserData().userId)
        }

        setUserProfileData()

        binding.btSettings.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        // Get posts based on type
        // Use OnCheckedStateChangeListener to always sync the chip and post type
        binding.chipGroupPostType.setOnCheckedStateChangeListener { group, _ ->
            val ids = group.checkedChipIds
            for (id in ids) {
                val chip: Chip = group.findViewById(id!!)

                when (chip.text) {
                    "Public" -> {
                        selectedType = "Public"
                        viewModelPost.getCurrentUserPosts(selectedType, currentUserData().userId)
                    }

                    "Anonymous" -> {
                        selectedType = "Anonymous"
                        viewModelPost.getCurrentUserPosts(selectedType, currentUserData().userId)
                    }

                    else -> {
                        selectedType = "Diary"
                    }
                }
            }
        }

        // Configure Post RecyclerView
        binding.rvPost.adapter = adapterPost
        binding.rvPost.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPost.setHasFixedSize(true)
        binding.rvPost.isNestedScrollingEnabled = false

        // Get user's like list
        isPostLiked()

        // Get post list based on the selected category
        viewModelPost.getCurrentUserPosts(selectedType,  currentUserData().userId)
        observeGetCurrentUserPosts()

    }

    private fun observeGetCurrentUserPosts() {
        viewModelPost.getCurrentUserPosts.observe(viewLifecycleOwner) {state ->
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
                    isPostDataEmpty(postList)
                }
            }
        }
    }

    private fun isPostDataEmpty(data: List<PostData>) {
        if (data.isEmpty()) {
            binding.rvPost.hide()
            binding.linearNoPostMessage.show()
            when (selectedType) {
                "Anonymous" -> {
                    binding.tvMessageDescription.text = "You haven't published anonymous post yet"
                }
                "Public" -> {
                    binding.tvMessageDescription.text = "You haven't published any post yet"
                }
                else -> {
                    binding.tvMessageDescription.text = "You haven't published any diary yet"
                }
            }

        } else {
            binding.rvPost.show()
            binding.linearNoPostMessage.hide()
            adapterPost.updateList(postList)
        }
    }

    private fun isPostLiked() {
        //Get Like data list at users collection
        viewModelPost.getUserLikeData(currentUserData().userId)
        viewModelPost.getUserLikeData.observe(viewLifecycleOwner) {state ->
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
                    adapterPost.updateUserLikeDataList(state.data.toMutableList())
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
    }

    private fun addLike(item: PostData) {
        // Add Like
        viewModelPost.addLike(
            LikeData(
                likeId = "",
                likedAt = Date(),
            ), item.postId, currentUserData().userId
        )

        viewModelPost.addLike.observe(this) {state ->
            when(state) {
                is UiState.Loading -> {
                }

                is UiState.Failure -> {
                    toast(state.error)
                }

                is UiState.Success -> {
                    viewModelPost.getCurrentUserPosts(selectedType,  currentUserData().userId)
                }
            }
        }
    }

    private fun currentUserData(): UserData {
        var user = UserData()
        viewModelPost.getSessionData {
            if (it != null) {
                user = it
            }
        }
        return user
    }

    // if post deleted, then notify the adapter
    override fun deleteStatus(status: Boolean?, position: Int?) {

        if (status == true) {
            position?.let { postList.removeAt(it) }
            adapterPost.updateList(postList)
            position?.let { adapterPost.notifyItemChanged(it) }
        }
    }
}