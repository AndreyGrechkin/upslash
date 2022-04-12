package com.jdefey.upsplash.ui.collection.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jdefey.upsplash.R
import com.jdefey.upsplash.databinding.ItemCollectionBinding
import com.jdefey.upsplash.model.Collections

class CollectionAdapter(
    private val onItemClick: (
        id: String
    ) -> Unit
) : PagingDataAdapter<Collections, CollectionAdapter.Holder>(CollectionDiffCallback()) {

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val collection = getItem(position) ?: return
        holder.bind(collection)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCollectionBinding.inflate(inflater, parent, false)
        return Holder(binding, onItemClick)
    }

    class Holder(
        private val binding: ItemCollectionBinding,
        private val onItemClick: (
            id: String
        ) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private var currentId: String? = null

        init {
            binding.collectionCardId.setOnClickListener {
                onItemClick(
                    currentId.toString()
                )
            }
        }

        fun bind(collection: Collections) {
            binding.countCollectionPhoto.text = collection.totalPhoto.toString()
            binding.userName.text = collection.user.name
            binding.titleCollection.text = collection.title

            Glide.with(itemView)
                .load(collection.coverPhoto.urls.regular)
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .into(binding.collectionPhotoListId)

            Glide.with(itemView)
                .load(collection.user.profileImage.small)
                .placeholder(R.drawable.ic_image)
                .circleCrop()
                .error(R.drawable.ic_image)
                .into(binding.userCollectionAvatar)

            currentId = collection.id

        }
    }
}

class CollectionDiffCallback : DiffUtil.ItemCallback<Collections>() {
    override fun areItemsTheSame(oldItem: Collections, newItem: Collections): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Collections, newItem: Collections): Boolean {
        return oldItem == newItem
    }
}