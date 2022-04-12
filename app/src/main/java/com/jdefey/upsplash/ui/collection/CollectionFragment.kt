package com.jdefey.upsplash.ui.collection

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import by.kirich1409.viewbindingdelegate.viewBinding
import com.jdefey.upsplash.R
import com.jdefey.upsplash.databinding.FragmentCollectionBinding
import com.jdefey.upsplash.ui.collection.adapter.CollectionAdapter
import com.jdefey.upsplash.ui.photo.adapter.PhotoLoadStateAdapter
import com.jdefey.upsplash.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest


@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CollectionFragment : Fragment(R.layout.fragment_collection) {

    private val binding by viewBinding(FragmentCollectionBinding::bind)
    private val viewModel by viewModels<CollectionViewModel>()
    private var collectionAdapter: CollectionAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initList()
    }

    private fun initList() {
        collectionAdapter =
            CollectionAdapter(onItemClick = { id ->
                val action =
                    CollectionFragmentDirections.actionCollectionFragmentToCollectionPhotoFragment(
                        id
                    )
                findNavController().navigate(action)
            })

        val headerAdapter = PhotoLoadStateAdapter { collectionAdapter.retry() }
        val footerAdapter = PhotoLoadStateAdapter { collectionAdapter.retry() }
        val concatAdapter = collectionAdapter.withLoadStateHeaderAndFooter(
            header = headerAdapter,
            footer = footerAdapter,
        )
        binding.apply {
            collectionPhotoList.setHasFixedSize(true)
            collectionPhotoList.adapter = concatAdapter
            buttonRetryList.setOnClickListener {
                collectionAdapter.retry()
            }
        }
        observeCollection(collectionAdapter)
        collectionLoadStateListener()
    }

    private fun observeCollection(adapter: CollectionAdapter) {
        lifecycleScope.launchWhenStarted {
            viewModel.photosFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun collectionLoadStateListener() {
        collectionAdapter.addLoadStateListener { combinedLoadStates ->
            binding.apply {
                collectionPhotoList.isVisible =
                    combinedLoadStates.source.refresh is LoadState.NotLoading
                progressBarList.isVisible = combinedLoadStates.source.refresh is LoadState.Loading
                buttonRetryList.isVisible = combinedLoadStates.source.refresh is LoadState.Error
                textErrorMessageList.isVisible =
                    combinedLoadStates.source.refresh is LoadState.Error
            }
        }
    }
}

