package com.example.rangkul.ui.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
): RecyclerView.Adapter<PostAdapter.PostViewHolder>(){

    private var list: MutableList<PostData> = arrayListOf()
    private var userLikeDataList: MutableList<LikeData> = arrayListOf()
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH)

    inner class PostViewHolder (val binding: ItemPostBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PostData) {
            binding.tvUserNamePost.apply {
                text = if (item.type == "Anonymous") {
                    "Anonymous"
                } else {
                    if (item.userName.length > 20) {
                        "${item.userName.substring(0,20)}..."
                    } else {
                        item.userName
                    }
                }
            }
            binding.tvCategoryPost.text = item.category
            binding.tvTimePost.text = sdf.format(item.createdAt)
            binding.tvCaptionPost.text = item.caption
            binding.tvLikeCountPost.text = item.likesCount.toString()
            binding.tvCommentCountPost.text = item.commentsCount.toString()

            // Set Profile Picture
            binding.civProfilePicturePost.apply {
                if (item.type == "Anonymous") {
                    setImageResource(R.drawable.ic_profile_picture_anonymous)
                } else {
                    setImageResource(R.drawable.ic_profile_picture_default)
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

            // Hide cvImagePost when the post has no image
            if (item.image == "null") {
                binding.cvImagePost.hide()
            }

             /* If list of like data in users collection contain current postId,
                means this post was liked by current user
              */
            if (userLikeDataList.any {it.likeId == item.postId}) {
                binding.ivLikeButtonPost.setImageResource(R.drawable.ic_like_solid)
            } else {
                binding.ivLikeButtonPost.setImageResource(R.drawable.ic_like_light)
            }

            binding.ivLikeButtonPost.setOnClickListener {
                onLikeClicked.invoke(adapterPosition, item)
            }
            binding.ivCommentButtonPost.setOnClickListener {
                onCommentClicked.invoke(adapterPosition, item)
            }
            binding.ivPostOptions.setOnClickListener {

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

//    fun removeItem(position: Int) {
//        list.removeAt(position)
//        notifyItemChanged(position)
//    }
}