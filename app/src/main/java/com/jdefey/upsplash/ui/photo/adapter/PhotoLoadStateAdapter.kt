package com.jdefey.upsplash.ui.photo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jdefey.upsplash.databinding.PhotoLoadStateHeaderFooterBinding


class PhotoLoadStateAdapter(
    private val retry: () -> Unit
) :
    LoadStateAdapter<PhotoLoadStateAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): Holder {
        return Holder(
            PhotoLoadStateHeaderFooterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), null, retry
        )
    }

    override fun onBindViewHolder(holder: Holder, loadState: LoadState) {
        holder.bind(loadState)
        val layoutParams = holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
        layoutParams.isFullSpan = true
    }

    class Holder(
        private val binding: PhotoLoadStateHeaderFooterBinding,
        private val swipeRefreshLayout: SwipeRefreshLayout?,
        private val retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonRetry.setOnClickListener { retry() }
        }

        fun bind(loadState: LoadState) = with(binding) {
            textErrorMessage.isVisible = loadState is LoadState.Error
            buttonRetry.isVisible = loadState is LoadState.Error
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.isRefreshing = loadState is LoadState.Loading
                progressBar.isVisible = false
            } else {
                progressBar.isVisible = loadState is LoadState.Loading
            }
        }
    }
}

