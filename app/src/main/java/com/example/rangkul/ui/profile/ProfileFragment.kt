package com.example.rangkul.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    private val viewModelPost: PostViewModel by viewModels()
    private var selectedType = "Public"
    private val adapterPost by lazy {
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
                val postOptionsBottomDialogFragment: PostOptionsBottomSheetFragment =
                    PostOptionsBottomSheetFragment.newInstance()
                postOptionsBottomDialogFragment.show(
                    parentFragmentManager,
                    PostOptionsBottomSheetFragment.TAG
                )
            },
            onBadgeClicked = { pos, item ->

            },
            context = requireContext()
        )
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

        setUserProfileData()

        binding.btSettings.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.chipPublic.setOnClickListener {
            selectedType = "Public"
            viewModelPost.getCurrentUserPosts(selectedType, currentUserData().userId)
        }
        binding.chipAnonymous.setOnClickListener {
            selectedType = "Anonymous"
            viewModelPost.getCurrentUserPosts(selectedType, currentUserData().userId)
        }
        binding.chipDiary.setOnCloseIconClickListener {
            selectedType = "Diary"
        }

        // Configure Post RecyclerView
        binding.rvPost.adapter = adapterPost
        binding.rvPost.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPost.setHasFixedSize(true)
        binding.rvPost.isNestedScrollingEnabled = false

        // Get post list based on the selected category
        viewModelPost.getCurrentUserPosts(selectedType,  currentUserData().userId)
        observeGetCurrentUserPosts()

        isPostLiked()
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
                    isPostDataEmpty(state.data)
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
            adapterPost.updateList(data.toMutableList())
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
}