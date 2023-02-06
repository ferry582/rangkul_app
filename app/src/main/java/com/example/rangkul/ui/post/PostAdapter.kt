package com.example.rangkul.ui.post

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rangkul.R
import com.example.rangkul.data.model.LikeData
import com.example.rangkul.data.model.PostData
import com.example.rangkul.databinding.ItemPostBinding
import com.example.rangkul.utils.hide
import com.example.rangkul.utils.show
import java.text.SimpleDateFormat
import java.util.*

class PostAdapter (
    val onOptionClicked: (Int, PostData) -> Unit,
    val onLikeClicked: (Int, PostData) -> Unit,
    val onCommentClicked: (Int, PostData) -> Unit,
    val onBadgeClicked: (Int, PostData) -> Unit,
    val onProfileClicked: (Int, String) -> Unit,
    val context: Context
): RecyclerView.Adapter<PostAdapter.PostViewHolder>(){

    private var list: MutableList<PostData> = arrayListOf()
    private var userLikeDataList: MutableList<LikeData> = arrayListOf()
    private lateinit var currentUserId: String
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH)

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
                    if (item.userName.length > 20) {
                        "${item.userName.substring(0,20)}..."
                    } else {
                        item.userName
                    }
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
            // If list of like data in users collection contain current postId,
            // means this post was liked by current user
            if (userLikeDataList.any {it.likeId == item.postId}) {
                binding.ivLikeButtonPost.setImageResource(R.drawable.ic_like_solid)
            } else {
                binding.ivLikeButtonPost.setImageResource(R.drawable.ic_like_light)
            }

            /*
                ** Handle onClick **
             */
            binding.ivLikeButtonPost.setOnClickListener {
                onLikeClicked.invoke(adapterPosition, item)
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

    fun updateUserLikeDataList(list: MutableList<LikeData>) {
        this.userLikeDataList = list
    }

    fun updateCurrentUser(userId: String) {
        this.currentUserId = userId
    }

}