package com.jdefey.upsplash.ui.photo

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jdefey.upsplash.model.Photo
import com.jdefey.upsplash.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@FlowPreview
@HiltViewModel
class PhotoListViewModel @Inject constructor(
    private val repository: PhotoRepository
) : ViewModel() {

    val photosFlow: Flow<PagingData<Photo>>
    private val searchBy = MutableLiveData("")

    init {
        photosFlow = searchBy.asFlow()
            .debounce(500)
            .flatMapLatest {
                repository.getPagedPhotos(it)
            }
            .cachedIn(viewModelScope)
    }

    fun setSearchBy(value: String) {
        if (this.searchBy.value == value) return
        this.searchBy.value = value
    }

    fun refresh() {
        this.searchBy.postValue(this.searchBy.value)
    }

    fun deletePhotoFromDataBase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePhotoDb()
        }
    }
}