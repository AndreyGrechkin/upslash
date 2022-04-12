package com.jdefey.upsplash.di

import com.jdefey.upsplash.repository.PhotoRepository
import com.jdefey.upsplash.repository.SplashRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun providesPhotoRepository(impl: SplashRepository): PhotoRepository
}