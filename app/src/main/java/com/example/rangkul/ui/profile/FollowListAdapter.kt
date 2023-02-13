package com.example.rangkul.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rangkul.R
import com.example.rangkul.data.model.FollowData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.databinding.ItemFollowListBinding
import com.example.rangkul.utils.hide
import com.example.rangkul.utils.limitTextLength
import com.example.rangkul.utils.show

class FollowListAdapter (
    val onItemClicked: (Int, String) -> Unit,
    val onFollowClicked: (Int, String) -> Unit,
    val onUnfollowClicked: (Int, String) -> Unit
): RecyclerView.Adapter<FollowListAdapter.PostViewHolder>(){

    private var list: MutableList<UserData> = arrayListOf()
    private var followingList: MutableList<FollowData> = arrayListOf()
    private lateinit var currentUserId: String

    inner class PostViewHolder (val binding: ItemFollowListBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserData) {
            // Set User name
            binding.tvUserName.text = item.userName.limitTextLength()

            // Set User Badge
            binding.ivUserBadge.apply {
                when (item.badge) {
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

            // Set Profile Picture
            binding.civProfilePicture.apply {
                if (item.profilePicture.isNullOrEmpty()) setImageResource(R.drawable.ic_profile_picture_default)
                else {
                    Glide
                        .with(context)
                        .load(item.profilePicture)
                        .placeholder(R.drawable.ic_profile_picture_default)
                        .error(R.drawable.ic_baseline_error_24)
                        .into(binding.civProfilePicture)
                }
            }

            // Set Follow button
            if (currentUserId == item.userId) {
                binding.btFollow.hide()
                binding.btUnfollow.hide()
            } else if (followingList.any {it.userId == item.userId}) {
                binding.btFollow.hide()
                binding.btUnfollow.show()
            } else {
                binding.btFollow.show()
                binding.btUnfollow.hide()
            }

            binding.clItemFollowList.setOnClickListener {
                onItemClicked.invoke(adapterPosition, item.userId)
            }

            binding.btFollow.setOnClickListener {
                onFollowClicked.invoke(adapterPosition, item.userId)
            }

            binding.btUnfollow.setOnClickListener {
                onUnfollowClicked.invoke(adapterPosition, item.userId)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = ItemFollowListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(list: MutableList<UserData>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun updateFollowingList(list: MutableList<FollowData>) {
        this.followingList = list
        notifyDataSetChanged()
    }

    fun updateCurrentUser(userId: String) {
        this.currentUserId = userId
    }

}