package com.jdefey.upsplash.ui.detail

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.jdefey.upsplash.R
import com.jdefey.upsplash.databinding.FragmentPhotoBinding
import com.jdefey.upsplash.ui.photo.PhotoListViewModel
import com.jdefey.upsplash.ui.photo.adapter.PhotoAdapter
import com.jdefey.upsplash.ui.photo.adapter.PhotoLoadStateAdapter
import com.jdefey.upsplash.utils.autoCleared
import com.jdefey.upsplash.utils.simpleScan
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TagsFragment : Fragment(R.layout.fragment_photo) {

    private val binding by viewBinding(FragmentPhotoBinding::bind)
    private val viewModel: PhotoListViewModel by viewModels()
    private var photoAdapter: PhotoAdapter by autoCleared()
    private val args: TagsFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()
        initList()
        searchByTags(args.titleId)

    }

    private fun initToolbar() {
        val searchItem = binding.toolbar.menu.findItem(R.id.action_search)
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                return true
            }
        })

        (searchItem.actionView as SearchView).setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.setSearchBy(query!!)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.setSearchBy(newText!!)
                    return true
                }
            })
    }

    private fun initList() {
        photoAdapter =
            PhotoAdapter(onItemClick = { id ->
                val action =
                    TagsFragmentDirections.actionTagsFragmentToDetailPhoto(id)
                findNavController().navigate(action)
            })

        val headerAdapter = PhotoLoadStateAdapter { photoAdapter.retry() }
        val footerAdapter = PhotoLoadStateAdapter { photoAdapter.retry() }
        val concatAdapter = photoAdapter.withLoadStateHeaderAndFooter(
            header = headerAdapter,
            footer = footerAdapter,
        )

        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

        binding.apply {
            photoList.layoutManager = staggeredGridLayoutManager
            photoList.setHasFixedSize(true)
            photoList.adapter = concatAdapter
            buttonRetryList.setOnClickListener {
                photoAdapter.retry()
            }
        }

        observePhoto(photoAdapter)
        setupSwipeToRefresh()
        photosLoadStateListener()
        handleScrollingToTopWhenSearching(photoAdapter)
    }

    private fun searchByTags(title: String) {
        viewModel.setSearchBy(title)
    }

    private fun observePhoto(adapter: PhotoAdapter) {
        lifecycleScope.launchWhenStarted {
            viewModel.photosFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }

        }
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun photosLoadStateListener() {
        photoAdapter.addLoadStateListener { combinedLoadStates ->
            binding.apply {
                photoList.isVisible = combinedLoadStates.source.refresh is LoadState.NotLoading
                progressBarList.isVisible = combinedLoadStates.source.refresh is LoadState.Loading
                swipeRefreshLayout.isRefreshing = false
                buttonRetryList.isVisible = combinedLoadStates.source.refresh is LoadState.Error
                textErrorMessageList.isVisible =
                    combinedLoadStates.source.refresh is LoadState.Error
            }
        }
    }

    private fun handleScrollingToTopWhenSearching(adapter: PhotoAdapter) = lifecycleScope.launch {
        getRefreshLoadStateFlow(adapter)
            .simpleScan(count = 2)
            .collectLatest { (previousState, currentState) ->
                if (previousState is LoadState.Loading && currentState is LoadState.NotLoading) {
                    binding.photoList.scrollToPosition(0)
                }
            }
    }

    private fun getRefreshLoadStateFlow(adapter: PhotoAdapter): Flow<LoadState> {
        return adapter.loadStateFlow
            .map { it.refresh }
    }
}
