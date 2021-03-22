package com.example.ar_app.di

import com.example.ar_app.ARAppApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        CommonModule::class,
        UIModule::class
    ]
)

interface AppComponent: AndroidInjector<DaggerApplication> {

    fun inject(application: ARAppApplication)

    override fun inject(instance: DaggerApplication)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: ARAppApplication): Builder

        fun build(): AppComponent
    }
}