package com.jdefey.upsplash.ui.profile.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jdefey.upsplash.R
import com.jdefey.upsplash.databinding.ItemPhotoBinding
import com.jdefey.upsplash.model.PhotoLikeByUser

class ProfileAdapter(
    private val onItemClick: (id: String) -> Unit
) : PagingDataAdapter<PhotoLikeByUser, ProfileAdapter.Holder>(ProfileDiffCallback()) {

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val profile = getItem(position) ?: return
        holder.bind(profile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPhotoBinding.inflate(inflater, parent, false)
        return Holder(binding, onItemClick)
    }

    class Holder(
        private val binding: ItemPhotoBinding,
        private val onItemClick: (id: String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private var currentId: String? = null

        init {
            binding.photoCardId.setOnClickListener {
                onItemClick(
                    currentId.toString()
                )
            }
        }

        fun bind(profile: PhotoLikeByUser) {
            binding.userName.text = profile.user?.name
            binding.countLikePhoto.text = profile.likes.toString()
            if (profile.likeByUser) {
                binding.likePhoto.setImageResource(R.drawable.ic_favorite_on)
            }
            Glide.with(itemView)
                .load(profile.urls.regular)
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .into(binding.photoListId)

            Glide.with(itemView)
                .load(profile.user?.profileImage?.small)
                .placeholder(R.drawable.ic_image)
                .circleCrop()
                .error(R.drawable.ic_image)
                .into(binding.userAvatar)

            currentId = profile.id
        }
    }
}

class ProfileDiffCallback : DiffUtil.ItemCallback<PhotoLikeByUser>() {
    override fun areItemsTheSame(oldItem: PhotoLikeByUser, newItem: PhotoLikeByUser): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PhotoLikeByUser, newItem: PhotoLikeByUser): Boolean {
        return oldItem == newItem
    }
}