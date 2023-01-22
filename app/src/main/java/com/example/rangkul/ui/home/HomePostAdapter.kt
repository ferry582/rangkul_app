package com.example.rangkul.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rangkul.data.model.PostData
import com.example.rangkul.databinding.ItemPostBinding
import com.example.rangkul.utils.hide

class HomePostAdapter (
    val onOptionClicked: (Int, PostData) -> Unit,
    val onLikeClicked: (Int, PostData) -> Unit,
    val onCommentClicked: (Int, PostData) -> Unit
): RecyclerView.Adapter<HomePostAdapter.PostViewHolder>(){

    private var list: MutableList<PostData> = arrayListOf()

    class PostViewHolder (val binding: ItemPostBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PostData) {
            binding.tvUserNamePost.text = limitNameLength(item.createdBy)
            binding.tvCategoryPost.text = item.category
            binding.tvTimePost.text = item.createdAt.toLocaleString()
            binding.tvCaptionPost.text = item.caption

            binding.ivLikeButtonPost.setOnClickListener {

            }
            binding.ivCommentButtonPost.setOnClickListener {

            }
            binding.ivPostOptions.setOnClickListener {

            }

            // Hide cvImagePost when the post has no image
            if (item.image == "null") {
                binding.cvImagePost.hide()
            }
        }

        private fun limitNameLength(name: String): String {
            var userName = name
            if (name.length > 23) {
                userName = "${name.substring(0,20)}..."
            }
            return userName
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