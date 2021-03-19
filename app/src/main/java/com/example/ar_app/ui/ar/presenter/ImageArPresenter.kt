package com.example.ar_app.ui.ar.presenter

import com.example.ar_app.ui.ar.model.Video
import com.example.ar_app.ui.ar.view.ImageArView
import com.example.ar_app.ui.common.presenter.BasePresenter

class ImageArPresenter(
    private val imageArView: ImageArView
    ) : BasePresenter() {

    private val videos = listOf<Video>()

    companion object {
        const val DEFAULT_IMAGE_PATH = "azoft_logo.png"
        const val DEFAULT_IMAGE_NAME = "azoft"
    }

    fun getVideos(): List<Video>? {
        return imageArView.getFilesFromAssets()
            ?.toList()
            ?.filter { it.contains(".mp4".toRegex()) }
            ?.map { Video(it, it.nameFromPath()) }
    }

    private fun String.nameFromPath() = this.replaceFirst(".mp4","")
}