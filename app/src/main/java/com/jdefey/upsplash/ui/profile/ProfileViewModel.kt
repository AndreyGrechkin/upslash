package com.jdefey.upsplash.ui.profile

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jdefey.upsplash.model.PhotoLikeByUser
import com.jdefey.upsplash.model.UserProfile
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
class ProfileViewModel @Inject constructor(
    private val repository: PhotoRepository
) : ViewModel() {

    private val profileStateFlow = MutableStateFlow<ProfileUsers>(ProfileUsers.Empty)

    val profileState: StateFlow<ProfileUsers>
        get() = profileStateFlow
    val photosLikeUserFlow: Flow<PagingData<PhotoLikeByUser>>

    private val searchBy = MutableLiveData("")

    init {
        photosLikeUserFlow = searchBy.asFlow()
            .debounce(500)
            .flatMapLatest {
                repository.getPhotoLikeByUser(it)
            }
            .cachedIn(viewModelScope)
    }

    fun setLikePhotoByUser(value: String) {
        if (this.searchBy.value == value) return
        this.searchBy.value = value
    }

    fun setUserProfile() =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val profile = repository.getUserProfile()
                profileStateFlow.value = ProfileUsers.Success(profile)
            } catch (t: Throwable) {
                profileStateFlow.value = ProfileUsers.Empty
            }
        }


    sealed class ProfileUsers {
        data class Success(val profileUser: UserProfile) : ProfileUsers()
        object Empty : ProfileUsers()
    }
}