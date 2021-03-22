package com.example.ar_app.di

import com.example.ar_app.ui.ar.di.ImageArModule
import com.example.ar_app.ui.ar.view.ImageArActivity
import com.example.ar_app.ui.main.di.MainModule
import com.example.ar_app.ui.main.view.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class UIModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun contributeMainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [ImageArModule::class])
    abstract fun contributeImageArActivity(): ImageArActivity
}