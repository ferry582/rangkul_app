package com.example.rangkul.ui.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rangkul.databinding.ItemReportOptionsBinding

class ReportOptionsAdapter (
    private val reportOptionsList: List<Map<String, String>>,
    val onItemClicked: (String) -> Unit)
    : RecyclerView.Adapter<ReportOptionsAdapter.ReportOptionsViewHolder>() {

    private var selectedPosition = -1

    inner class ReportOptionsViewHolder (val binding: ItemReportOptionsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Map<String, String>, position: Int) {
            binding.tvReportTitle.text = item["title"]
            binding.tvReportDescription.text = item["description"]

            binding.radioButton.isChecked = selectedPosition == position
            binding.clItemReportOptions.setOnClickListener {
                item["title"]?.let { it1 -> onItemClicked.invoke(it1) }
                if (selectedPosition >= 0) notifyItemChanged(selectedPosition)
                selectedPosition = position
                notifyItemChanged(selectedPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportOptionsViewHolder {
        val itemView = ItemReportOptionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReportOptionsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return reportOptionsList.size
    }

    override fun onBindViewHolder(holder: ReportOptionsViewHolder, position: Int) {
        val category = reportOptionsList[position]
        holder.bind(category, position)
    }

}