package com.example.ar_app.utils

object Utils {

    fun String.fileNameFromPath() =
        when {
            this.contains(".mp4".toRegex()) -> this.replaceFirst(".mp4", "")
            this.contains(".jpg".toRegex()) -> this.replaceFirst(".jpg", "")
            this.contains(".png".toRegex()) -> this.replaceFirst(".png", "")
            else -> this
        }
}