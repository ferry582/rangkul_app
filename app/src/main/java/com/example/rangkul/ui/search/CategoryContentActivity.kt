package com.example.rangkul.ui.search

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rangkul.data.model.LikeData
import com.example.rangkul.data.model.PostData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.databinding.ActivityCategoryContentBinding
import com.example.rangkul.ui.comment.CommentActivity
import com.example.rangkul.ui.post.PostAdapter
import com.example.rangkul.ui.post.PostOptionsBottomSheetFragment
import com.example.rangkul.ui.post.PostViewModel
import com.example.rangkul.ui.profile.VisitedProfileActivity
import com.example.rangkul.utils.UiState
import com.example.rangkul.utils.hide
import com.example.rangkul.utils.show
import com.example.rangkul.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CategoryContentActivity : AppCompatActivity(), PostOptionsBottomSheetFragment.DeletePostStatusListener {

    private lateinit var binding: ActivityCategoryContentBinding
    private var selectedCategory = ""
    private var selectedChip = ""
    private var postList: MutableList<PostData> = arrayListOf()
    private val viewModelPost: PostViewModel by viewModels()
    private val viewModelCategoryContent: CategoryContentViewModel by viewModels()
    private val adapterPost by lazy {
        PostAdapter(
            onCommentClicked = { pos, item ->
                val intent = Intent(this, CommentActivity::class.java)
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
                    supportFragmentManager,
                    "PostOptionsBottomSheetFragment"
                )
            },
            onBadgeClicked = { pos, item ->

            },
            onProfileClicked = { pos, item ->
                if (item == currentUserData().userId) {
                    // navigate to profile fragment
                } else {
                    val intent = Intent(this, VisitedProfileActivity::class.java)
                    intent.putExtra("USER_ID", item)
                    startActivity(intent)
                }
            },
            context = this
        )
    }
    // If the user just back from CommentActivity, then reload/call the getPosts method to refresh the comment count
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModelPost.getPostsWithCategory(selectedCategory)
        }
    }
    private val articleAdapter by lazy {
        ArticleAdapter(
            onItemClick = { pos, item ->
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(item.url)
                startActivity(intent)
            },
            context = this
        )
    }
    private val videoAdapter by lazy {
        VideoAdapter(
            onItemClick = { pos, item ->
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(item.url)
                startActivity(intent)
            },
            context = this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.srlCategoryContent.setOnRefreshListener {
            when (selectedChip) {
                "Post" -> viewModelPost.getPostsWithCategory(selectedCategory)
                "Article" -> viewModelCategoryContent.getContents(selectedCategory, "Article")
                else -> viewModelCategoryContent.getContents(selectedCategory, "Video")
            }
        }

        setToolbar()

        // Get selected category data
        selectedCategory = intent.getStringExtra("CATEGORY").toString()
        binding.tvSelectedCategory.text = selectedCategory

        // Handle chip button clicked
        // Change recyclerview margin to fit the gridlayout item
        val param = binding.rvContents.layoutParams as ViewGroup.MarginLayoutParams
        binding.chipPost.setOnClickListener {
            selectedChip = "Post"
            binding.rvContents.adapter = adapterPost
            binding.rvContents.layoutManager = LinearLayoutManager(this)
            viewModelPost.getPostsWithCategory(selectedCategory)
            param.setMargins(getPixelValue(16),getPixelValue(15),getPixelValue(16),0)
        }
        binding.chipArticle.setOnClickListener {
            selectedChip = "Article"
            binding.rvContents.adapter = articleAdapter
            binding.rvContents.layoutManager = LinearLayoutManager(this)
            viewModelCategoryContent.getContents(selectedCategory, "Article")
            param.setMargins(getPixelValue(16),getPixelValue(15),getPixelValue(16),0)
        }
        binding.chipVideo.setOnClickListener {
            selectedChip = "Video"
            binding.rvContents.adapter = videoAdapter
            binding.rvContents.layoutManager = GridLayoutManager(this, 2)
            viewModelCategoryContent.getContents(selectedCategory, "Video")
            param.setMargins(getPixelValue(8),getPixelValue(15),getPixelValue(8),0)
        }

        // Configure Post RecyclerView
        binding.rvContents.adapter = adapterPost
        binding.rvContents.layoutManager = LinearLayoutManager(this)
        binding.rvContents.setHasFixedSize(true)
        binding.rvContents.isNestedScrollingEnabled = false

        // Get user's like list
        isPostLiked()

        // Get post list based on the selected category
        viewModelPost.getPostsWithCategory(selectedCategory)
        observeGetPostsWithCategory()

        // Get Article list based on the selected category
        observeGetContent()
    }

    private fun observeGetContent() {
        viewModelCategoryContent.getContents.observe(this) {state ->
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
                    binding.srlCategoryContent.isRefreshing = false // hide swipe refresh loading
                    articleAdapter.updateList(state.data.toMutableList())
                    videoAdapter.updateList(state.data.toMutableList())
                }
            }
        }
    }

    private fun observeGetPostsWithCategory() {
        viewModelPost.getPostsWithCategory.observe(this) {state ->
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
                    binding.srlCategoryContent.isRefreshing = false // hide swipe refresh loading
                    postList = state.data.toMutableList()
                    adapterPost.updateList(postList)
                    adapterPost.updateCurrentUser(currentUserData().userId)
                }
            }
        }
    }

    private fun getPixelValue(dimenId: Int): Int {
        val resources: Resources = this.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dimenId.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    private fun isPostLiked() {
        //Get Like data list at users collection
        viewModelPost.getUserLikeData(currentUserData().userId)
        viewModelPost.getUserLikeData.observe(this) {state ->
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
                    binding.progressBar.show()
                }

                is UiState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.progressBar.hide()
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

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(com.example.rangkul.R.drawable.ic_back_grey)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    // if post deleted, then notify the adapter
    override fun deletePostStatus(status: Boolean?, position: Int?) {
        if (status == true) {
            position?.let { postList.removeAt(it) }
            adapterPost.updateList(postList)
            position?.let { adapterPost.notifyItemChanged(it) }
        }
    }
}