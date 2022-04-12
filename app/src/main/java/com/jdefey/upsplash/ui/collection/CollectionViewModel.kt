package com.jdefey.upsplash.ui.collection

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jdefey.upsplash.model.CollectionPhoto
import com.jdefey.upsplash.model.Collections
import com.jdefey.upsplash.model.Photo
import com.jdefey.upsplash.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val repository: PhotoRepository
) : ViewModel() {

    val photosCollectionFlow: Flow<PagingData<Photo>>
    private val collectionStateFlow = MutableStateFlow<PhotoCollections>(PhotoCollections.Empty)
    private val searchBy = MutableLiveData("")
    val collectionState: StateFlow<PhotoCollections>
        get() = collectionStateFlow
    val photosFlow: Flow<PagingData<Collections>> = repository.getPagedCollection()
        .cachedIn(viewModelScope)

    init {
        photosCollectionFlow = searchBy.asFlow()
            .debounce(500)
            .flatMapLatest {
                repository.getCollectionPhoto(it)
            }
            .cachedIn(viewModelScope)
    }

    fun setCollectionId(collectionId: String) =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val collection = repository.setCollectionId(collectionId)
                collectionStateFlow.value = PhotoCollections.Success(collection)
            } catch (t: Throwable) {
                collectionStateFlow.value = PhotoCollections.Empty
            }
        }

    fun setSearchBy(value: String) {
        if (this.searchBy.value == value) return
        this.searchBy.value = value
    }

    sealed class PhotoCollections {
        data class Success(val photoCollections: CollectionPhoto) : PhotoCollections()
        object Empty : PhotoCollections()
    }
}

