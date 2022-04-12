package com.jdefey.upsplash.ui.collection

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.jdefey.upsplash.R
import com.jdefey.upsplash.databinding.FragmentCollectionPhotoBinding
import com.jdefey.upsplash.ui.collection.adapter.CollectionPhotoAdapter
import com.jdefey.upsplash.ui.collection.adapter.TagsCollectionAdapter
import com.jdefey.upsplash.ui.photo.adapter.PhotoLoadStateAdapter
import com.jdefey.upsplash.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest


@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CollectionPhotoFragment : Fragment(R.layout.fragment_collection_photo) {

    private val binding: FragmentCollectionPhotoBinding by viewBinding(
        FragmentCollectionPhotoBinding::bind
    )
    private val viewModel by viewModels<CollectionViewModel>()
    private var collectionPhotoAdapter: CollectionPhotoAdapter by autoCleared()
    private var tagAdapter: TagsCollectionAdapter by autoCleared()
    private val args: CollectionPhotoFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        tagsInit()
        initList()
        viewModel.setSearchBy(args.collectionId)
        viewModel.setCollectionId(args.collectionId)
        bindCollectionPhoto()

    }

    private fun initToolbar() {
        binding.toolbar.setTitle(R.string.title_collection_photo)
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun tagsInit() {
        tagAdapter =
            TagsCollectionAdapter(onItemClick = { title ->
                val action =
                    CollectionPhotoFragmentDirections.actionCollectionPhotoFragmentToTagsFragment(
                        title
                    )
                findNavController().navigate(action)
            })
        with(binding.hashTagCollectionList) {
            adapter = tagAdapter
            setHasFixedSize(true)
        }
    }

    private fun initList() {
        collectionPhotoAdapter =
            CollectionPhotoAdapter(onItemClick = { id ->
                val action =
                    CollectionPhotoFragmentDirections.actionCollectionPhotoFragmentToDetailPhoto(id)
                findNavController().navigate(action)
            })

        val headerAdapter = PhotoLoadStateAdapter { collectionPhotoAdapter.retry() }
        val footerAdapter = PhotoLoadStateAdapter { collectionPhotoAdapter.retry() }
        val concatAdapter = collectionPhotoAdapter.withLoadStateHeaderAndFooter(
            header = headerAdapter,
            footer = footerAdapter,
        )
        binding.apply {
            collectionPhotoList.setHasFixedSize(true)
            collectionPhotoList.adapter = concatAdapter
            buttonRetryList.setOnClickListener {
                collectionPhotoAdapter.retry()
            }
        }

        observePhotoCollection(collectionPhotoAdapter)
        photosCollectionLoadStateListener()
    }

    private fun bindCollectionPhoto() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.collectionState.collectLatest {
                when (it) {
                    is CollectionViewModel.PhotoCollections.Success -> {
                        tagAdapter.items = it.photoCollections.tags
                        binding.nameUserCollection.text = it.photoCollections.user?.name
                        binding.titleCollection.text = it.photoCollections.title
                        binding.countPhoto.text = it.photoCollections.totalPhoto.toString()
                        binding.descriptionCollection.text = it.photoCollections.description
                        Glide.with(requireContext())
                            .load(it.photoCollections.coverPhoto?.urls?.regular)
                            .placeholder(R.drawable.ic_image)
                            .error(R.drawable.ic_image)
                            .into(binding.photoTitleBackground)
                    }
                    is CollectionViewModel.PhotoCollections.Empty -> {}
                }
            }
        }
    }

    private fun observePhotoCollection(adapter: CollectionPhotoAdapter) {
        lifecycleScope.launchWhenStarted {
            viewModel.photosCollectionFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun photosCollectionLoadStateListener() {
        collectionPhotoAdapter.addLoadStateListener { combinedLoadStates ->
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