package com.example.ar_app.di

import android.content.Context
import com.example.ar_app.ARAppApplication
import com.example.ar_app.data.Image
import com.example.ar_app.data.Video
import com.example.ar_app.easyar.GLView
import com.example.ar_app.repository.Repository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CommonModule {

    @Provides
    @Singleton
    fun provideContext(application: ARAppApplication): Context = application.applicationContext

    @Provides
    @Singleton
    fun provideRepository(context: Context) = Repository(context)

    @Provides
    @Singleton
    fun provideImages(repository: Repository) = repository.getImages()

    @Provides
    @Singleton
    fun provideVideos(repository: Repository) = repository.getVideos()

    @Provides
    @Singleton
    fun provideGlView(context: Context, videos: List<Video>?, images: List<Image>?) =
        GLView(context, videos, images)
}