package com.example.rangkul.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rangkul.R
import com.example.rangkul.data.model.PostData

class PostAdapter (private val context: Context, private val postData: List<PostData>, private val listener: (PostData) -> Unit)
    : RecyclerView.Adapter<PostAdapter.PostViewHolder>(){

//    private lateinit var postDataList: List<PostData>
//
//    fun setPostDataList(postData: List<PostData>) {
//        this.postDataList = postData
//    }

    class PostViewHolder (view: View): RecyclerView.ViewHolder(view) {

        private val userName = view.findViewById<TextView>(R.id.tvUserNamePost)
        private val postCategory = view.findViewById<TextView>(R.id.tvCategoryPost)
        private val postCreatedAt = view.findViewById<TextView>(R.id.tvTimePost)
        private val postCaption = view.findViewById<TextView>(R.id.tvCaptionPost)

        fun bindView(postData: PostData, listener: (PostData) -> Unit) {
            userName.text = postData.postId
            postCategory.text = postData.category
            postCreatedAt.text = postData.createdAt
            postCaption.text = postData.caption

            itemView.setOnClickListener {
                listener(postData)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_post, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bindView(postData[position], listener)
    }

    override fun getItemCount(): Int {
        return postData.size ?: 0
    }

}