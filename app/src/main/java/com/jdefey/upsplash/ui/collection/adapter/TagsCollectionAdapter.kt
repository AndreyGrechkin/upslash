package com.jdefey.upsplash.ui.collection.adapter

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.jdefey.upsplash.model.TagsCollection

class TagsCollectionAdapter(
    onItemClick: (
        title: String,
    ) -> Unit
) : AsyncListDifferDelegationAdapter<TagsCollection>(TagsCollectionDiffUtilCallback()) {

    init {
        delegatesManager.addDelegate(TagsCollectionAdapterDelegate(onItemClick))
    }

    class TagsCollectionDiffUtilCallback : DiffUtil.ItemCallback<TagsCollection>() {
        override fun areItemsTheSame(oldItem: TagsCollection, newItem: TagsCollection): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: TagsCollection, newItem: TagsCollection): Boolean {
            return oldItem == newItem
        }
    }
}