package com.jdefey.upsplash.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jdefey.upsplash.model.LikePhoto
import com.jdefey.upsplash.model.PhotoDetail
import com.jdefey.upsplash.model.PhotoLink
import com.jdefey.upsplash.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailPhotoViewModel @Inject constructor(
    private val repository: PhotoRepository
) : ViewModel() {

    private val photoMutableLiveData = MutableStateFlow<PhotoDetails>(PhotoDetails.Empty)
    val loadPhoto: StateFlow<PhotoDetails> = photoMutableLiveData

    private val photoLikeMutableLiveData = MutableStateFlow<PhotoLikes>(PhotoLikes.Empty)
    val likePhoto: StateFlow<PhotoLikes> = photoLikeMutableLiveData

    private val downloadPhotoMutableLiveData = MutableStateFlow<PhotoLinks>(PhotoLinks.Empty)


    fun loadPhotoById(photoId: String) =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getPhotoById(photoId)
                photoMutableLiveData.value = PhotoDetails.Success(response)
            } catch (t: Throwable) {
                photoMutableLiveData.value = PhotoDetails.Empty
            }
        }


    fun setPhotoLikeId(photoId: String) =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val liked = repository.setPhotoLike(photoId)
                photoLikeMutableLiveData.value = PhotoLikes.Success(liked)
            } catch (t: Throwable) {
                photoMutableLiveData.value = PhotoDetails.Empty
            }
        }


    fun deleteLikePhotoId(photoId: String) =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val deleteLike = repository.setPhotoLikeDelete(photoId)
                photoLikeMutableLiveData.value = PhotoLikes.Success(deleteLike)
            } catch (t: Throwable) {
                photoMutableLiveData.value = PhotoDetails.Empty
            }
        }

    fun downloadPhotoId(photoId: String) =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val link = repository.downloadPhoto(photoId)
                downloadPhotoMutableLiveData.value = PhotoLinks.Success(link)
            } catch (t: Throwable) {
                downloadPhotoMutableLiveData.value = PhotoLinks.Empty
            }
        }

    sealed class PhotoDetails {
        data class Success(val photoDetails: PhotoDetail) : PhotoDetails()
        object Empty : PhotoDetails()
    }

    sealed class PhotoLikes {
        data class Success(val photoLike: LikePhoto) : PhotoLikes()
        object Empty : PhotoLikes()
    }

    sealed class PhotoLinks {
        data class Success(val photoLinks: PhotoLink) : PhotoLinks()
        object Empty : PhotoLinks()
    }
}