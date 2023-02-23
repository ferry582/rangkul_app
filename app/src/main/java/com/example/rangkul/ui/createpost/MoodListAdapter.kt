package com.example.rangkul.ui.createpost

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rangkul.data.model.ImageListData
import com.example.rangkul.databinding.ItemMoodBinding

class MoodListAdapter (
    private val moodList: ArrayList<ImageListData>,
    val onItemClicked: (ImageListData) -> Unit)
    : RecyclerView.Adapter<MoodListAdapter.MoodListViewHolder>() {

    private var selectedPosition = -1

    inner class MoodListViewHolder (val binding: ItemMoodBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ImageListData, position: Int) {
            binding.ivImageMood.setImageResource(item.image)
            binding.tvMood.text = item.name

            binding.itemMood.isSelected = selectedPosition == position
            binding.itemMood.setOnClickListener {
                onItemClicked.invoke(item)
                if (selectedPosition >= 0) notifyItemChanged(selectedPosition)
                selectedPosition = position
                notifyItemChanged(selectedPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodListViewHolder {
        val itemView = ItemMoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoodListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return moodList.size
    }

    override fun onBindViewHolder(holder: MoodListViewHolder, position: Int) {
        val category = moodList[position]
        holder.bind(category, position)
    }

}