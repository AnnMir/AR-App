package com.example.ar_app.ui.main.di

import com.example.ar_app.di.ActivityScope
import com.example.ar_app.ui.main.presenter.MainPresenter
import com.example.ar_app.ui.main.presenter.MainPresenterImpl
import com.example.ar_app.ui.main.view.MainActivity
import com.example.ar_app.ui.main.view.MainView
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class MainModule {

    @Binds
    @ActivityScope
    abstract fun provideMainActivityView(activity: MainActivity): MainView

    @Module
    companion object {

        @JvmStatic
        @Provides
        @ActivityScope
        fun provideMainPresenter(mainView: MainView): MainPresenter = MainPresenterImpl(mainView)
    }
}