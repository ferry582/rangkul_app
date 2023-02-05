package com.example.rangkul.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rangkul.data.model.ImageListData
import com.example.rangkul.databinding.ItemCategoryListBinding

class CategoryListAdapter (
    private val categoryList: ArrayList<ImageListData>,
    val onItemClicked: (Int, ImageListData) -> Unit)
    : RecyclerView.Adapter<CategoryListAdapter.CategoryListViewHolder>() {

    inner class CategoryListViewHolder (val binding: ItemCategoryListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ImageListData) {
            binding.ivCategory.setImageResource(item.image)

            binding.itemCategoryList.setOnClickListener {
                onItemClicked.invoke(adapterPosition, item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        val itemView = ItemCategoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
        val category = categoryList[position]
        holder.bind(category)
    }

}