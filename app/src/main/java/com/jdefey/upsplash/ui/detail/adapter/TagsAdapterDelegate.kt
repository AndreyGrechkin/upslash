package com.jdefey.upsplash.ui.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.jdefey.upsplash.databinding.ItemTagsBinding
import com.jdefey.upsplash.model.Tags

class TagsAdapterDelegate(
    private val onItemClick: (
        title: String,

        ) -> Unit
) : AbsListItemAdapterDelegate<Tags, Tags, TagsAdapterDelegate.Holder>() {

    override fun isForViewType(item: Tags, items: MutableList<Tags>, position: Int): Boolean {
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup): Holder {
        val itemBinding =
            ItemTagsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(itemBinding, onItemClick)
    }

    override fun onBindViewHolder(
        item: Tags,
        holder: Holder,
        payloads: MutableList<Any>
    ) {
        holder.bind(item)
    }

    class Holder(
        private val itemBinding: ItemTagsBinding,
        private val onItemClick: (
            title: String,
        ) -> Unit
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        private var currentTitle: String? = null
        init {
            itemBinding.hashTag.setOnClickListener {
                onItemClick(
                    currentTitle.toString()

                )
            }
        }

        fun bind(tag: Tags) {
            itemBinding.hashTag.text = tag.title
            currentTitle = tag.title
        }
    }
}