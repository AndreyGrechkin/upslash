package com.jdefey.upsplash.ui.photo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jdefey.upsplash.R
import com.jdefey.upsplash.databinding.ItemPhotoBinding
import com.jdefey.upsplash.model.Photo


class PhotoAdapter(
    private val onItemClick: (
        id: String
    ) -> Unit
) : PagingDataAdapter<Photo, PhotoAdapter.Holder>(PhotoDiffCallback()) {


    override fun onBindViewHolder(holder: Holder, position: Int) {
        val photo = getItem(position) ?: return
        holder.bind(photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPhotoBinding.inflate(inflater, parent, false)
        return Holder(binding, onItemClick)
    }

    class Holder(
        private val binding: ItemPhotoBinding,
        private val onItemClick: (
            id: String
        ) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentId: String? = null

        init {
            binding.photoCardId.setOnClickListener {
                onItemClick(
                    currentId.toString()
                )
            }
        }

        fun bind(photo: Photo) {
            binding.countLikePhoto.text = photo.likes
            binding.userName.text = photo.user.name
            if (photo.likeByUser) {
                binding.likePhoto.setImageResource(R.drawable.ic_favorite_on)
            }
            Glide.with(itemView)
                .load(photo.urls.small)
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .into(binding.photoListId)

            Glide.with(itemView)
                .load(photo.user.profileImage.small)
                .placeholder(R.drawable.ic_image)
                .circleCrop()
                .error(R.drawable.ic_image)
                .into(binding.userAvatar)

            currentId = photo.id

        }
    }
}

class PhotoDiffCallback : DiffUtil.ItemCallback<Photo>() {
    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem == newItem
    }
}