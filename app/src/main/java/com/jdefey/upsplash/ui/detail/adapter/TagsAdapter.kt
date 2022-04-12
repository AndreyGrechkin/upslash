package com.jdefey.upsplash.ui.detail.adapter

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.jdefey.upsplash.model.Tags

class TagsAdapter(
    onItemClick: (
        title: String,
    ) -> Unit
) : AsyncListDifferDelegationAdapter<Tags>(TagsDiffUtilCallback()) {

    init {
        delegatesManager.addDelegate(TagsAdapterDelegate(onItemClick))
    }

    class TagsDiffUtilCallback : DiffUtil.ItemCallback<Tags>() {
        override fun areItemsTheSame(oldItem: Tags, newItem: Tags): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Tags, newItem: Tags): Boolean {
            return oldItem == newItem
        }
    }
}