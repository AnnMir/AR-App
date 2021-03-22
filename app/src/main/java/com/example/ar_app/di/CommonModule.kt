package com.example.ar_app.di

import android.content.Context
import com.example.ar_app.ARAppApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CommonModule {

    @Provides
    @Singleton
    fun provideContext(application: ARAppApplication) : Context = application.applicationContext
}