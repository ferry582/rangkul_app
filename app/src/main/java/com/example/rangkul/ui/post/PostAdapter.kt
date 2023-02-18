package com.example.rangkul.ui.post

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rangkul.R
import com.example.rangkul.data.model.PostData
import com.example.rangkul.databinding.ItemPostBinding
import com.example.rangkul.utils.hide
import com.example.rangkul.utils.limitTextLength
import com.example.rangkul.utils.show
import java.text.SimpleDateFormat
import java.util.*

class PostAdapter (
    val onOptionClicked: (Int, PostData) -> Unit,
    val onLikeClicked: (Int, String) -> Unit,
    val onCommentClicked: (Int, PostData) -> Unit,
    val onBadgeClicked: (Int, PostData) -> Unit,
    val onProfileClicked: (Int, String) -> Unit,
    private val postStatusListener: PostStatusListener,
    val context: Context
): RecyclerView.Adapter<PostAdapter.PostViewHolder>(){

    private var list: MutableList<PostData> = arrayListOf()
    private lateinit var currentUserId: String
    private val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH)
    private val taskPerformedSet = mutableSetOf<Int>() // To make sure the task is only performed once for each item

    inner class PostViewHolder (val binding: ItemPostBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PostData) {
            /*
                ** Set User Info **
             */
            binding.tvUserNamePost.apply {
                text = if (item.type == "Anonymous") {
                    // check if post belongs to the current user
                    if (item.createdBy == currentUserId) {
                        "Anonymous (You)"
                    } else {
                        "Anonymous"
                    }

                } else {
                    item.userName.limitTextLength()
                }
            }
            // Set User Badge
            binding.ivUserBadgePost.apply {
                if (item.type == "Anonymous") {
                    hide()
                } else {
                    when (item.userBadge) {
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
                    show()
                }
            }
            // Set Profile Picture
            binding.civProfilePicturePost.apply {
                if (item.type == "Anonymous") {
                    setImageResource(R.drawable.ic_profile_picture_anonymous)
                } else {
                    if (item.profilePicture.isNullOrEmpty()) setImageResource(R.drawable.ic_profile_picture_default)
                    else {
                        Glide
                            .with(context)
                            .load(item.profilePicture)
                            .placeholder(R.drawable.ic_profile_picture_default)
                            .error(R.drawable.ic_baseline_error_24)
                            .into(binding.civProfilePicturePost)
                    }
                }
            }

            /*
                ** Set Post Data **
             */
            binding.tvCategoryPost.text = item.category
            binding.tvTimePost.text = sdf.format(item.createdAt)
            binding.tvCaptionPost.text = item.caption
            binding.tvLikeCountPost.text = item.likesCount.toString()
            binding.tvCommentCountPost.text = item.commentsCount.toString()
            // Set post's image
            if (item.image == null) {
                // Hide cvImagePost when the post has no image
                binding.cvImagePost.hide()
            } else {
                binding.cvImagePost.show()
                Glide
                    .with(context)
                    .load(item.image)
                    .placeholder(R.drawable.shape_image_content)
                    .error(R.drawable.ic_baseline_error_24)
                    .into(binding.ivImagePost)
            }

            // Set like button
            if (!taskPerformedSet.contains(adapterPosition)) {
                postStatusListener.isPostBeingLiked(item.postId, adapterPosition) {
                    if (it) {
                        binding.ivLikeButtonPost.setImageResource(R.drawable.ic_like_solid)
                    }
                    else {
                        binding.ivLikeButtonPost.setImageResource(R.drawable.ic_like_light)
                    }
                }
                taskPerformedSet.add(adapterPosition)
            }

            /*
                ** Handle onClick **
             */
            binding.ivLikeButtonPost.setOnClickListener {
                onLikeClicked.invoke(adapterPosition, item.postId)
            }
            binding.ivCommentButtonPost.setOnClickListener {
                onCommentClicked.invoke(adapterPosition, item)
            }
            binding.ivPostOptions.setOnClickListener {
                onOptionClicked.invoke(adapterPosition, item)
            }
            binding.ivUserBadgePost.setOnClickListener {
                onBadgeClicked.invoke(adapterPosition, item)
            }
            binding.civProfilePicturePost.setOnClickListener {
                if (item.type  != "Anonymous") onProfileClicked.invoke(adapterPosition, item.createdBy)
            }
            binding.tvUserNamePost.setOnClickListener {
                if (item.type  != "Anonymous") onProfileClicked.invoke(adapterPosition, item.createdBy)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(list: MutableList<PostData>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun updateCurrentUser(userId: String) {
        this.currentUserId = userId
    }

    fun dataUpdated(position: Int) {
        // Reset the taskPerformedSet for the new data
        taskPerformedSet.remove(position)
        notifyItemChanged(position)
    }

    fun updateLikeCount(position: Int, value: Int) {
        list[position].likesCount = list[position].likesCount + value
        notifyItemChanged(position, list[position])
    }

    fun updateCommentCount(position: Int, value: Int) {
        list[position].commentsCount = list[position].commentsCount + value
        notifyItemChanged(position, list[position])
    }

    fun clearData() {
        taskPerformedSet.clear()
    }

    interface PostStatusListener {
        fun isPostBeingLiked(item: String, position: Int, callback: (Boolean) -> Unit)
    }

}