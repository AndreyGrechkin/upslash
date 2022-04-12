package com.jdefey.upsplash.ui.profile

import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.jdefey.upsplash.R
import com.jdefey.upsplash.databinding.FragmentProfileBinding
import com.jdefey.upsplash.model.UserProfile
import com.jdefey.upsplash.ui.photo.adapter.PhotoLoadStateAdapter
import com.jdefey.upsplash.ui.profile.adapter.ProfileAdapter
import com.jdefey.upsplash.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val binding by viewBinding(FragmentProfileBinding::bind)
    private val viewModel by viewModels<ProfileViewModel>()
    private var profileAdapter: ProfileAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initList()
        observeUserProfile()
        binding.locationIcon.setOnClickListener {
            locationView()
        }
    }

    private fun initToolbar() {
        binding.toolbar.setTitle(R.string.title_bottom_profile)
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    openDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun initList() {
        profileAdapter =
            ProfileAdapter(onItemClick = { id ->
                val action =
                    ProfileFragmentDirections.actionProfileFragmentToDetailPhoto(id)
                findNavController().navigate(action)
            })
        val headerAdapter = PhotoLoadStateAdapter { profileAdapter.retry() }
        val footerAdapter = PhotoLoadStateAdapter { profileAdapter.retry() }
        val concatAdapter = profileAdapter.withLoadStateHeaderAndFooter(
            header = headerAdapter,
            footer = footerAdapter,
        )
        binding.apply {
            collectionPhotoList.setHasFixedSize(true)
            collectionPhotoList.adapter = concatAdapter
            buttonRetryList.setOnClickListener {
                profileAdapter.retry()
            }
        }
        observeLikePhoto(profileAdapter)
        photosLikeLoadStateListener()
    }

    private fun observeLikePhoto(adapter: ProfileAdapter) {
        lifecycleScope.launchWhenStarted {
            viewModel.photosLikeUserFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun photosLikeLoadStateListener() {
        profileAdapter.addLoadStateListener { combinedLoadStates ->
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

    private fun observeUserProfile() {
        viewModel.setUserProfile()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.profileState.collectLatest {
                when (it) {
                    is ProfileViewModel.ProfileUsers.Success -> {
                        binding.userName.text = it.profileUser.name
                        viewModel.setLikePhotoByUser(it.profileUser.username)
                        binding.userNickName.text = it.profileUser.username
                        binding.userDescription.text = it.profileUser.bio
                        binding.locationCity.text = it.profileUser.location
                        binding.mailUser.text = it.profileUser.email
                        binding.countLikePhotoUser.text = it.profileUser.totalLike.toString()
                        Glide.with(requireContext())
                            .load(it.profileUser.avatar?.large)
                            .placeholder(R.drawable.ic_image)
                            .circleCrop()
                            .error(R.drawable.ic_image)
                            .into(binding.photoProfile)
                    }
                    is ProfileViewModel.ProfileUsers.Empty -> {}
                }
            }
        }
    }

    private fun locationView() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.profileState.collectLatest {
                when (it) {
                    is ProfileViewModel.ProfileUsers.Success -> {
                        openLocation(it.profileUser)
                    }
                    is ProfileViewModel.ProfileUsers.Empty -> {
                    }
                }
            }
        }
    }

    private fun openLocation(loc: UserProfile?) {
        val geocoder = Geocoder(requireContext())
        val location = geocoder.getFromLocationName(loc?.location, 1)
        val latitude = location[0].latitude
        val longitude = location[0].longitude
        val uriLocation = "geo: $latitude, $longitude"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriLocation))
        startActivity(intent)
    }

    private fun openDialog() {
        LogoutDialogFragment()
            .show(childFragmentManager, LOCATION_TAG)
    }

    companion object {
        private const val LOCATION_TAG = "logoutDialogTag"
    }
}