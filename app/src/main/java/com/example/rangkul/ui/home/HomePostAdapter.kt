package com.example.rangkul.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rangkul.data.model.PostData
import com.example.rangkul.databinding.ItemPostBinding
import com.example.rangkul.utils.hide
import java.text.SimpleDateFormat
import java.util.*

class HomePostAdapter (
    val onOptionClicked: (Int, PostData) -> Unit,
    val onLikeClicked: (Int, PostData) -> Unit,
    val onCommentClicked: (Int, PostData) -> Unit,
    val onBadgeClicked: (Int, PostData) -> Unit
): RecyclerView.Adapter<HomePostAdapter.PostViewHolder>(){

    private var list: MutableList<PostData> = arrayListOf()
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH)

    inner class PostViewHolder (val binding: ItemPostBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PostData) {
            binding.tvUserNamePost.apply {
                text = if (item.userName.length > 20) {
                    "${item.userName.substring(0,20)}..."
                } else {
                    item.userName
                }
            }
            binding.tvCategoryPost.text = item.category
            binding.tvTimePost.text = sdf.format(item.createdAt)
            binding.tvCaptionPost.text = item.caption
            binding.tvLikeCountPost.text = item.likesCount.toString()
            binding.tvCommentCountPost.text = item.commentsCount.toString()

            binding.ivLikeButtonPost.setOnClickListener {

            }
            binding.ivCommentButtonPost.setOnClickListener {
                onCommentClicked.invoke(adapterPosition, item)
            }
            binding.ivPostOptions.setOnClickListener {

            }

            // Hide cvImagePost when the post has no image
            if (item.image == "null") {
                binding.cvImagePost.hide()
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

//    fun removeItem(position: Int) {
//        list.removeAt(position)
//        notifyItemChanged(position)
//    }
}