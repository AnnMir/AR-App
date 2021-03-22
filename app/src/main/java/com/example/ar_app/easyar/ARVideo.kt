package com.example.ar_app.easyar

import android.util.Log
import cn.easyar.*
import com.example.ar_app.data.Video

class ARVideo(private val videos: List<Video>?) {

    private val player: VideoPlayer
    private var prepared: Boolean
    private var found: Boolean
    private var path: String? = null
    private var scheduler: DelayedCallbackScheduler? = null

    fun dispose() {
        player.close()
    }

    fun openVideoFile(texid: Int, scheduler: DelayedCallbackScheduler?) {
        path = getRandomVideo()
        this.scheduler = scheduler
        player.setRenderTexture(TextureId.fromInt(texid))
        player.setVideoType(VideoType.Normal)
        player.open(
            path!!, StorageType.Assets, scheduler!!
        ) { status -> setVideoStatus(status) }
    }

    fun openTransparentVideoFile(texid: Int, scheduler: DelayedCallbackScheduler?) {
        path = getRandomVideo()
        player.setRenderTexture(TextureId.fromInt(texid))
        player.setVideoType(VideoType.TransparentSideBySide)
        player.open(
            path!!, StorageType.Assets, scheduler!!
        ) { status -> setVideoStatus(status) }
    }

    fun openStreamingVideo(url: String?, texid: Int, scheduler: DelayedCallbackScheduler?) {
        path = url
        player.setRenderTexture(TextureId.fromInt(texid))
        player.setVideoType(VideoType.Normal)
        player.open(
            url!!, StorageType.Absolute, scheduler!!
        ) { status -> setVideoStatus(status) }
    }

    fun setVideoStatus(status: Int) {
        Log.i("HelloAR", "video: " + path + " (" + Integer.toString(status) + ")")
        if (status == VideoStatus.Ready) {
            prepared = true
            if (found) {
                player.play()
            }
        } else if (status == VideoStatus.Completed) {
            if (found) {
                player.play()
            }
        }
    }

    fun onFound() {
        found = true
        if (prepared) {
            player.play()
        }
    }

    fun onLost() {
        found = false
        if (prepared) {
            player.close()
            changeVideo()
        }
    }

    val isRenderTextureAvailable: Boolean
        get() = player.isRenderTextureAvailable

    fun update() {
        player.updateFrame()
    }

    fun changeVideo() {
        path = getRandomVideo()
        player.open(
            path!!, StorageType.Assets, scheduler!!
        ) { status -> setVideoStatus(status) }
    }

    private fun getRandomVideo(): String {
        return videos?.random()?.path ?: DEFAULT_VIDEO
    }

    init {
        player = VideoPlayer()
        prepared = false
        found = false
    }

    companion object {
        const val DEFAULT_VIDEO = "Sea.mp4"
    }
}