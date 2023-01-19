package com.example.rangkul.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rangkul.R
import com.example.rangkul.home.PostData

class PostAdapter (private val context: Context, private val postData: List<PostData>, private val listener: (PostData) -> Unit)
    : RecyclerView.Adapter<PostAdapter.RestaurantViewHolder>(){

    class RestaurantViewHolder (view: View): RecyclerView.ViewHolder(view) {

        val name = view.findViewById<TextView>(R.id.tvUserNamePost)
        val category = view.findViewById<TextView>(R.id.tvCategoryPost)
        val time = view.findViewById<TextView>(R.id.tvTimePost)
        val writing = view.findViewById<TextView>(R.id.tvWritingPost)

        fun bindView(postData: PostData, listener: (PostData) -> Unit) {
            name.text = postData.userName
            category.text = postData.category
            time.text = postData.time
            writing.text = postData.writing

            itemView.setOnClickListener {
                listener(postData)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        return RestaurantViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_post, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bindView(postData[position], listener)
    }

    override fun getItemCount(): Int = postData.size

}