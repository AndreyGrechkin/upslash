package com.jdefey.upsplash.ui.detail

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.*
import android.content.Context.DOWNLOAD_SERVICE
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.jdefey.upsplash.BuildConfig
import com.jdefey.upsplash.R
import com.jdefey.upsplash.auth.AuthStateManager
import com.jdefey.upsplash.data.Networking
import com.jdefey.upsplash.databinding.DetailPhotoBinding
import com.jdefey.upsplash.model.LikePhoto
import com.jdefey.upsplash.model.LocationPhoto
import com.jdefey.upsplash.model.PhotoDetail
import com.jdefey.upsplash.ui.detail.adapter.TagsAdapter
import com.jdefey.upsplash.utils.autoCleared
import com.jdefey.upsplash.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.constructPermissionsRequest
import java.io.File


@AndroidEntryPoint
class DetailPhoto : Fragment(R.layout.detail_photo) {

    private val binding by viewBinding(DetailPhotoBinding::bind)
    private val args: DetailPhotoArgs by navArgs()
    private val viewModel by viewModels<DetailPhotoViewModel>()
    private var tagAdapter: TagsAdapter by autoCleared()
    private var dm: DownloadManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dm = activity?.baseContext?.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        if (Networking.token.length < 5) {
            openDeepLink()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initList()
        getPhotoDetails(args.photoId)
        observeViewModel()
        observeCountLike()

        binding.likePhotoDetail.setOnClickListener {
            setLikePhoto()
        }

        binding.iconLocation.setOnClickListener {
            viewLocation()
        }

        binding.downloadText.setOnClickListener {
            downloading()
        }
    }

    private fun initToolbar() {
        binding.toolbar.setTitle(R.string.title_detail)
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_share -> {
                    val urlShare = "https://unsplash.com/photos/${args.photoId}"
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, urlShare)
                    val chooser = Intent.createChooser(intent, getText(R.string.title_share))
                    startActivity(chooser)
                    true
                }
                else -> false
            }
        }
    }

    private fun initList() {
        tagAdapter =
            TagsAdapter(onItemClick = { title ->
                val action =
                    DetailPhotoDirections.actionDetailPhotoToTagsFragment(title)
                findNavController().navigate(action)
            })
        with(binding.hashTagList) {
            adapter = tagAdapter
            setHasFixedSize(true)
        }
    }

    private fun getPhotoDetails(id: String) {
        viewModel.loadPhotoById(id)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.loadPhoto.collectLatest {
                when (it) {
                    is DetailPhotoViewModel.PhotoDetails.Success -> {
                        bindDetails(it.photoDetails)
                        setDetailsVisibility(true)
                    }
                    is DetailPhotoViewModel.PhotoDetails.Empty -> {
                        setDetailsVisibility(false)
                    }
                }
            }
        }
    }

    private fun observeCountLike() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.likePhoto.collectLatest {
                when (it) {
                    is DetailPhotoViewModel.PhotoLikes.Success -> {
                        binding.countLikePhotoDetail.text = it.photoLike.photo.likes.toString()
                        setDetailsVisibility(true)
                    }
                    is DetailPhotoViewModel.PhotoLikes.Empty -> {
                        setDetailsVisibility(false)
                    }
                }
            }
        }
    }

    private fun bindDetails(photo: PhotoDetail) = with(binding) {
        tagAdapter.items = photo.tags
        binding.makeName.text = photo.exif?.make ?: getText(R.string.unknown)
        binding.modelName.text = photo.exif?.model ?: getText(R.string.unknown)
        binding.exposure.text = photo.exif?.exposure ?: getText(R.string.unknown)
        binding.aperture.text = photo.exif?.aperture ?: getText(R.string.unknown)
        binding.focalLength.text = photo.exif?.focalLength ?: getText(R.string.unknown)
        binding.iso.text = photo.exif?.iso.toString()
        binding.countLikePhotoDetail.text = photo.likes.toString()
        binding.countDownload.text = photo.downloads.toString()
        binding.textLocation.text = photo.location?.city ?: getText(R.string.unknown)
        binding.userNameDetail.text = photo.user?.name ?: getText(R.string.unknown)
        binding.aboutUser.text = photo.user?.bio ?: ""
        if (photo.likeByUser) {
            binding.likePhotoDetail.setImageResource(R.drawable.ic_favorite_on)
            viewModel.setPhotoLikeId(args.photoId)
        } else {
            binding.likePhotoDetail.setImageResource(R.drawable.ic_favorite)
            viewModel.deleteLikePhotoId(args.photoId)
        }
        Glide.with(requireContext())
            .load(photo.user?.profileImage?.small)
            .placeholder(R.drawable.ic_image)
            .circleCrop()
            .error(R.drawable.ic_image)
            .into(binding.userAvatarDetail)

        Glide.with(requireContext())
            .load(photo.urls.regular)
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
            .into(binding.photoDetailId)
    }

    private fun setLikePhoto() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.likePhoto.collect {
                when (it) {
                    is DetailPhotoViewModel.PhotoLikes.Success -> {
                        bindLike(it.photoLike)
                        cancel()
                    }
                    is DetailPhotoViewModel.PhotoLikes.Empty -> {
                        setDetailsVisibility(false)
                    }
                }
            }
        }
    }

    private fun bindLike(like: LikePhoto) {
        if (like.photo.liked) {
            binding.likePhotoDetail.setImageResource(R.drawable.ic_favorite)
            viewModel.deleteLikePhotoId(args.photoId)
        } else {
            binding.likePhotoDetail.setImageResource(R.drawable.ic_favorite_on)
            viewModel.setPhotoLikeId(args.photoId)
        }
    }

    private fun viewLocation() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.loadPhoto.collectLatest {
                when (it) {
                    is DetailPhotoViewModel.PhotoDetails.Success -> {
                        openLocation(it.photoDetails.location)
                    }
                    is DetailPhotoViewModel.PhotoDetails.Empty -> {
                    }
                }
            }
        }
    }

    private fun openLocation(loc: LocationPhoto?) {
        val uriLocation = "geo: ${loc?.position?.latitude} ${loc?.position?.longitude}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriLocation))
        startActivity(intent)
    }

    private fun downloading() {
        constructPermissionsRequest(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            onShowRationale = ::onContactPermissionShowRationale,
            onPermissionDenied = ::onContactPermissionDenied,
            onNeverAskAgain = ::onContactPermissionNeverAskAgain,
            requiresPermission = {
                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    viewModel.loadPhoto.collectLatest {
                        when (it) {
                            is DetailPhotoViewModel.PhotoDetails.Success -> {
                                downloadLink(it.photoDetails.id)
                                downloadPhoto(
                                    name = "${it.photoDetails.user?.name} ${it.photoDetails.id}",
                                    url = it.photoDetails.urls.raw
                                )
                            }
                            is DetailPhotoViewModel.PhotoDetails.Empty -> {}
                        }
                    }
                }
            }
        ).launch()
    }

    private fun downloadLink(photoId: String) {
        viewModel.downloadPhotoId(photoId)
    }

    private fun downloadPhoto(name: String, url: String) {
        val download = DownloadManager.Request(Uri.parse(url))
            .setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_MOBILE or
                        DownloadManager.Request.NETWORK_WIFI
            )
            .setTitle(name)
            .setDescription(getString(R.string.download_description))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_PICTURES,
                "/Unsplash/" + "/" + File.separator + name + ".jpg"
            )
        val downloadId = dm?.enqueue(download)
        val br = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                val id: Long? = p1?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    val snackBar = binding.snakeBarLayout
                    Snackbar.make(snackBar, R.string.download_complete, Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.open_file)) {
                            downloadId?.let { it1 -> openDownloadedAttachment(it1) }
                        }
                        .show()
                }
            }
        }
        requireContext().registerReceiver(
            br,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    @SuppressLint("Range")
    private fun openDownloadedAttachment(downloadId: Long) {
        val downloadManager = context?.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query()
        query.setFilterById(downloadId)
        val cursor: Cursor = downloadManager.query(query)
        if (cursor.moveToFirst()) {
            val downloadStatus: Int =
                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            val downloadLocalUri: String? =
                cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
            val downloadMimeType: String =
                cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE))
            if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL && downloadLocalUri != null) {
                openDownloadedAttachment(Uri.parse(downloadLocalUri), downloadMimeType)
            }
        }
        cursor.close()
    }

    private fun openDownloadedAttachment(
        attachmentUri: Uri?,
        attachmentMimeType: String
    ) {
        var attachment: Uri? = attachmentUri
        if (attachment != null) {
            if (ContentResolver.SCHEME_FILE == attachment.scheme) {
                val file = File(attachment.path)
                attachment =
                    FileProvider.getUriForFile(
                        requireContext(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        file
                    )
            }
            val openAttachmentIntent = Intent(Intent.ACTION_VIEW)
            openAttachmentIntent.setDataAndType(attachment, attachmentMimeType)
            openAttachmentIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            try {
                context?.startActivity(openAttachmentIntent)
            } catch (e: ActivityNotFoundException) {
                toast(R.string.unable_to_open_filed)
            }
        }
    }

    private fun openDeepLink() {
        val authFrag = AuthStateManager(requireContext())
        val auth = authFrag.readState()
        Networking.token = auth.accessToken.toString()
        if (Networking.token.length < 5) {
            findNavController().navigate(DetailPhotoDirections.actionDetailPhotoToAuthFragment())
        }
    }

    private fun setDetailsVisibility(boolean: Boolean) = with(binding) {
        photoDetailCard.isVisible = boolean
        locationView.isVisible = boolean
        tagsView.isVisible = boolean
        exif.isVisible = boolean
        aboutUserView.isVisible = boolean
        downloadView.isVisible = boolean
    }

    private fun onContactPermissionDenied() {
        toast(R.string.file_add_permission_denied)
    }

    private fun onContactPermissionShowRationale(request: PermissionRequest) {
        request.proceed()
    }

    private fun onContactPermissionNeverAskAgain() {
        toast(R.string.file_add_permission_never_ask_again)
    }

}