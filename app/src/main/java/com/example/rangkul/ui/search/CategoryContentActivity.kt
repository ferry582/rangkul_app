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
class CategoryContentActivity :
    AppCompatActivity(),
    PostOptionsBottomSheetFragment.PostOptionsStatusListener,
    PostAdapter.PostStatusListener {

    private lateinit var binding: ActivityCategoryContentBinding
    private var selectedCategory = ""
    private var selectedChip = ""
    private var postList: MutableList<PostData> = arrayListOf()
    private var currentItemPosition = -1
    private val postViewModel: PostViewModel by viewModels()
    private val viewModelCategoryContent: CategoryContentViewModel by viewModels()
    private val postAdapter by lazy {
        PostAdapter(
            onCommentClicked = { pos, item ->
                val intent = Intent(this, CommentActivity::class.java)
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
                    supportFragmentManager,
                    "PostOptionsBottomSheetFragment"
                )
            },
            onBadgeClicked = { pos, item ->

            },
            onProfileClicked = { _, item ->
                if (item == currentUserData().userId) {
                    // navigate to profile fragment
                } else {
                    val intent = Intent(this, VisitedProfileActivity::class.java)
                    intent.putExtra("USER_ID", item)
                    startActivity(intent)
                }
            },
            postStatusListener = this,
            context = this
        )
    }
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Update the comment count at selected post position
            val commentAddedAmount = result.data?.getIntExtra("COMMENT_ADDED", 0)
            commentAddedAmount?.let { updateCommentCount(it) }
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
                "Post" -> postViewModel.getPostsWithCategory(selectedCategory)
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
            binding.rvContents.adapter = postAdapter
            binding.rvContents.layoutManager = LinearLayoutManager(this)
            postViewModel.getPostsWithCategory(selectedCategory)
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
        binding.rvContents.adapter = postAdapter
        binding.rvContents.layoutManager = LinearLayoutManager(this)
        binding.rvContents.setHasFixedSize(true)
        binding.rvContents.isNestedScrollingEnabled = false

        // Get post list based on the selected category
        postViewModel.getPostsWithCategory(selectedCategory)
        observeGetPostsWithCategory()

        // Get Article list based on the selected category
        observeGetContent()
    }

    private fun updateCommentCount(amount: Int) {
        postAdapter.updateCommentCount(currentItemPosition, amount)
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
        postViewModel.getPostsWithCategory.observe(this) {state ->
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
                    postAdapter.clearData()
                    postAdapter.updateList(postList)
                    postAdapter.updateCurrentUser(currentUserData().userId)
                }
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

    private fun getPixelValue(dimenId: Int): Int {
        val resources: Resources = this.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dimenId.toFloat(),
            resources.displayMetrics
        ).toInt()
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
    override fun deletePostStatus(status: Boolean?) {
        if (status == true) {
            postList.removeAt(currentItemPosition)
            postAdapter.updateList(postList)
            postAdapter.notifyItemChanged(currentItemPosition)
        }
    }

    override fun isPostBeingLiked(item: String, position: Int, callback: (Boolean) -> Unit) {
        postViewModel.isPostBeingLiked(currentUserData().userId, item) {
            callback.invoke(it)
        }
    }
}