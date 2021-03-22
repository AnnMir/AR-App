package com.example.ar_app.ui.ar.presenter

import com.example.ar_app.data.Image
import com.example.ar_app.data.Video

interface ImageArPresenter {
    fun getVideos(): List<Video>?

    fun getImages(): List<Image>?
}