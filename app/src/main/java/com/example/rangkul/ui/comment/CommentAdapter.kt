package com.example.rangkul.ui.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rangkul.R
import com.example.rangkul.data.model.CommentData
import com.example.rangkul.data.model.PostData
import com.example.rangkul.databinding.ItemCommentBinding
import com.example.rangkul.utils.hide
import com.example.rangkul.utils.show
import java.text.SimpleDateFormat
import java.util.*

class CommentAdapter (
    val onOptionsCommentClicked: (Int, CommentData) -> Unit
): RecyclerView.Adapter<CommentAdapter.PostViewHolder>(){

    private var list: MutableList<CommentData> = arrayListOf()
    private lateinit var objectPost: PostData
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH)

    inner class PostViewHolder (val binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CommentData) {
            // Set User name
            binding.tvUserNameComment.apply {
                text = if (objectPost.type == "Anonymous" && objectPost.createdBy == item.commentedBy) {
                    "Anonymous (Post Owner)"
                } else {
                    if (item.userName.length > 20) {
                        "${item.userName.substring(0,20)}..."
                    } else {
                        item.userName
                    }
                }
            }

            binding.tvTimeComment.text = sdf.format(item.commentedAt)
            binding.tvComment.text = item.comment

            binding.ivCommentOptions.setOnClickListener {
                onOptionsCommentClicked.invoke(adapterPosition, item)
            }

            // Set User Badge
            binding.ivUserBadgeComment.apply {
                if (objectPost.type == "Anonymous" && objectPost.createdBy == item.commentedBy) {
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

            binding.ivCommentOptions.setOnClickListener {
                onOptionsCommentClicked.invoke(adapterPosition, item)
            }

            // Set Profile Picture
            binding.civProfilePictureComment.apply {
                if (objectPost.type == "Anonymous" && objectPost.createdBy == item.commentedBy) {
                    setImageResource(R.drawable.ic_profile_picture_anonymous)
                } else {
                    if (item.profilePicture.isNullOrEmpty()) setImageResource(R.drawable.ic_profile_picture_default)
                    else {
                        Glide
                            .with(context)
                            .load(item.profilePicture)
                            .placeholder(R.drawable.ic_profile_picture_default)
                            .error(R.drawable.ic_baseline_error_24)
                            .into(binding.civProfilePictureComment)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(list: MutableList<CommentData>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun updatePostType(objectPost: PostData) {
        this.objectPost = objectPost
    }

}