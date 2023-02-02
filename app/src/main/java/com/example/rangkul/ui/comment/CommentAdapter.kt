package com.example.rangkul.ui.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rangkul.R
import com.example.rangkul.data.model.CommentData
import com.example.rangkul.databinding.ItemCommentBinding
import java.text.SimpleDateFormat
import java.util.*

class CommentAdapter (
    val onOptionsCommentClicked: (Int, CommentData) -> Unit,
): RecyclerView.Adapter<CommentAdapter.PostViewHolder>(){

    private var list: MutableList<CommentData> = arrayListOf()
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH)

    inner class PostViewHolder (val binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CommentData) {
            binding.tvUserNameComment.apply {
                text = if (item.userName.length > 20) {
                    "${item.userName.substring(0,20)}..."
                } else {
                    item.userName
                }
            }
            binding.tvTimeComment.text = sdf.format(item.commentedAt)
            binding.tvComment.text = item.comment

            binding.ivCommentOptions.setOnClickListener {
                onOptionsCommentClicked.invoke(adapterPosition, item)
            }

            // Set User Badge
            binding.ivUserBadgeComment.apply {
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
            }

            binding.ivCommentOptions.setOnClickListener {
                onOptionsCommentClicked.invoke(adapterPosition, item)
            }

//            binding.civProfilePictureComment.setImageResource("")
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

}