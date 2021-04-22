package com.example.ar_app.repository

import android.content.Context
import com.example.ar_app.data.Image
import com.example.ar_app.data.Video
import com.example.ar_app.utils.Utils.fileNameFromPath

class Repository(private val context: Context) {

    fun getImages() = context.assets.list("")
        ?.toList()
        ?.filter { it.contains(".jpg|.png".toRegex()) }
        ?.map { Image(it, it.fileNameFromPath()) }

    fun getVideos() = context.assets.list("")
        ?.toList()
        ?.filter { it.contains(".mp4".toRegex()) }
        ?.map { Video(it, it.fileNameFromPath()) }

}