package com.example.rangkul.ui.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rangkul.R
import com.example.rangkul.data.model.DiaryData
import com.example.rangkul.databinding.ItemDiaryBinding
import com.example.rangkul.utils.hide
import com.example.rangkul.utils.show
import java.text.SimpleDateFormat
import java.util.*

class DiaryAdapter (
    val onOptionClicked: (Int, DiaryData) -> Unit,
    val context: Context
): RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>(){

    private var list: MutableList<DiaryData> = arrayListOf()
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH)

    inner class DiaryViewHolder (val binding: ItemDiaryBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DiaryData) {
            binding.tvMood.text = item.moodTitle

            // Set diary emoticon mood
            binding.civEmoticonMood.setImageResource(item.moodImage)

            binding.tvTimeDiary.text = sdf.format(item.createdAt)
            binding.tvCaptionDiary.text = item.caption

            // Set diary image
            if (item.image == null) {
                binding.cvImageDiary.hide()
            } else {
                binding.cvImageDiary.show()
                Glide
                    .with(context)
                    .load(item.image)
                    .placeholder(R.drawable.shape_image_content)
                    .error(R.drawable.ic_baseline_error_24)
                    .into(binding.ivImageDiary)
            }

            binding.ivDiaryOptions.setOnClickListener {
                onOptionClicked.invoke(adapterPosition, item)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val itemView = ItemDiaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiaryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(list: MutableList<DiaryData>) {
        this.list = list
        notifyDataSetChanged()
    }

}