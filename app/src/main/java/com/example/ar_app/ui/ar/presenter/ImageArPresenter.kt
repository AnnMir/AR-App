package com.example.ar_app.ui.ar.presenter

import com.example.ar_app.data.Image
import com.example.ar_app.data.Video
import com.example.ar_app.ui.ar.view.ImageArView
import com.example.ar_app.ui.common.presenter.BasePresenter

class ImageArPresenter(
    private val imageArView: ImageArView
) : BasePresenter() {

    fun getImages(): List<Image>? = imageArView.getFilesFromAssets()
        ?.toList()
        ?.filter { it.contains(".jpg|.png".toRegex()) }
        ?.map { Image(it, it.nameFromPath()) }

    fun getVideos(): List<Video>? = imageArView.getFilesFromAssets()
        ?.toList()
        ?.filter { it.contains(".mp4".toRegex()) }
        ?.map { Video(it, it.nameFromPath()) }

    private fun String.nameFromPath() =
        when {
            this.contains(".mp4".toRegex()) -> this.replaceFirst(".mp4", "")
            this.contains(".jpg".toRegex()) -> this.replaceFirst(".jpg", "")
            this.contains(".png".toRegex()) -> this.replaceFirst(".png", "")
            else -> this
        }
}