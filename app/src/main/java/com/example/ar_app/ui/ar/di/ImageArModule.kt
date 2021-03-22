package com.example.ar_app.ui.ar.di

import com.example.ar_app.di.ActivityScope
import com.example.ar_app.ui.ar.presenter.ImageArPresenter
import com.example.ar_app.ui.ar.presenter.ImageArPresenterImpl
import com.example.ar_app.ui.ar.view.ImageArActivity
import com.example.ar_app.ui.ar.view.ImageArView
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class ImageArModule {

    @Binds
    @ActivityScope
    abstract fun provideImageArActivityView(activity: ImageArActivity): ImageArView

    @Module
    companion object {

        @JvmStatic
        @Provides
        @ActivityScope
        fun provideImageArPresenter(imageArView: ImageArView): ImageArPresenter = ImageArPresenterImpl(imageArView)
    }
}