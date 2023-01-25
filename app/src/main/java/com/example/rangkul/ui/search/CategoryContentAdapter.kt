package com.example.rangkul.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rangkul.data.model.CategoryContentData
import com.example.rangkul.databinding.ItemArticleBinding
import java.text.SimpleDateFormat
import java.util.*

class CategoryContentAdapter (
    val onItemClick: (Int, CategoryContentData) -> Unit,
): RecyclerView.Adapter<CategoryContentAdapter.ArticleViewHolder>(){

    private var list: MutableList<CategoryContentData> = arrayListOf()
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)

    inner class ArticleViewHolder (val binding: ItemArticleBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryContentData) {
            binding.tvTitleArticle.text = item.title
            binding.tvSourceArticle.text = item.source
            binding.tvTimeCreated.text = sdf.format(item.createdAt)

            binding.itemArticle.setOnClickListener {
                onItemClick.invoke(adapterPosition, item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val itemView = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(list: MutableList<CategoryContentData>) {
        this.list = list
        notifyDataSetChanged()
    }

}