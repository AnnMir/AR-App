//================================================================================================================================
//
// Copyright (c) 2015-2020 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
// EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
// and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
//
//================================================================================================================================
package com.example.ar_app.easyar

import android.opengl.GLES20
import android.util.Log
import cn.easyar.*
import com.example.ar_app.data.Video
import com.example.ar_app.data.Image
import java.nio.ByteBuffer
import java.util.*

class HelloAR {
    private var scheduler: DelayedCallbackScheduler?
    private var camera: CameraDevice? = null
    private val trackers: ArrayList<ImageTracker>
    private var bgRenderer: BGRenderer? = null
    private var video_renderers: ArrayList<VideoRenderer>? = null
    private var current_video_renderer: VideoRenderer? = null
    private var tracked_target = 0
    private var active_target = 0
    private var video: ARVideo? = null
    private var throttler: InputFrameThrottler? = null
    private var feedbackFrameFork: FeedbackFrameFork? = null
    private var i2OAdapter: InputFrameToOutputFrameAdapter? = null
    private var inputFrameFork: InputFrameFork? = null
    private var join: OutputFrameJoin? = null
    private var oFrameBuffer: OutputFrameBuffer? = null
    private var i2FAdapter: InputFrameToFeedbackFrameAdapter? = null
    private var outputFrameFork: OutputFrameFork? = null
    private var previousInputFrameIndex = -1
    private var imageBytes: ByteArray? = null

    private var videos: List<Video>? = null
    private var images: List<Image>? = null

    private fun loadFromImage(tracker: ImageTracker, image: Image) {
        val target = ImageTarget.createFromImageFile(image.path, 1, image.name, "", "", 1.0f)
        if (target == null) {
            Log.e("HelloAR", "target create failed or key is not correct")
            return
        }
        tracker.loadTarget(
            target, scheduler!!
        ) { target, status ->
            Log.i(
                "HelloAR",
                String.format(
                    "load target (%b): %s (%d)",
                    status,
                    target.name(),
                    target.runtimeID()
                )
            )
        }
        target.dispose()
    }

    fun recreate_context() {
        if (active_target != 0) {
            video?.onLost()
            video?.dispose()
            video = null
            tracked_target = 0
            active_target = 0
        }
        if (bgRenderer != null) {
            bgRenderer?.dispose()
            bgRenderer = null
        }
        if (video_renderers != null) {
            for (video_renderer in video_renderers!!) {
                video_renderer.dispose()
            }
            video_renderers = null
        }
        current_video_renderer = null
        previousInputFrameIndex = -1
        bgRenderer = BGRenderer()
        video_renderers = ArrayList<VideoRenderer>()
        var k = 0
        while (k < 3) {
            val video_renderer = VideoRenderer()
            video_renderers!!.add(video_renderer)
            k += 1
        }
    }

    fun initialize(videoList: List<Video>?, imageList: List<Image>?) {
        recreate_context()
        camera = CameraDeviceSelector.createCameraDevice(CameraDevicePreference.PreferObjectSensing)
        throttler = InputFrameThrottler.create()
        inputFrameFork = InputFrameFork.create(2)
        join = OutputFrameJoin.create(2)
        oFrameBuffer = OutputFrameBuffer.create()
        i2OAdapter = InputFrameToOutputFrameAdapter.create()
        i2FAdapter = InputFrameToFeedbackFrameAdapter.create()
        outputFrameFork = OutputFrameFork.create(2)
        var status = true
        status = status and camera!!.openWithPreferredType(CameraDeviceType.Back)
        camera!!.setSize(Vec2I(1280, 960))
        camera!!.setFocusMode(CameraDeviceFocusMode.Continousauto)
        if (!status) {
            return
        }
        val tracker = ImageTracker.create()
        images = imageList
        images?.forEach { loadFromImage(tracker, it) }
        trackers.add(tracker)
        feedbackFrameFork = FeedbackFrameFork.create(trackers.size)
        camera!!.inputFrameSource().connect(throttler!!.input())
        throttler!!.output().connect(inputFrameFork!!.input())
        inputFrameFork!!.output(0).connect(i2OAdapter!!.input())
        i2OAdapter!!.output().connect(join!!.input(0))
        inputFrameFork!!.output(1).connect(i2FAdapter!!.input())
        i2FAdapter!!.output().connect(feedbackFrameFork!!.input())
        var k = 0
        var trackerBufferRequirement = 0
        for (_tracker in trackers) {
            feedbackFrameFork!!.output(k).connect(_tracker.feedbackFrameSink())
            _tracker.outputFrameSource().connect(join!!.input(k + 1))
            trackerBufferRequirement += _tracker.bufferRequirement()
            k++
        }
        join!!.output().connect(outputFrameFork!!.input())
        outputFrameFork!!.output(0).connect(oFrameBuffer!!.input())
        outputFrameFork!!.output(1).connect(i2FAdapter!!.sideInput())
        oFrameBuffer!!.signalOutput().connect(throttler!!.signalInput())

        videos = videoList

        //CameraDevice and rendering each require an additional buffer
        camera!!.setBufferCapacity(throttler!!.bufferRequirement() + i2FAdapter!!.bufferRequirement() + oFrameBuffer!!.bufferRequirement() + trackerBufferRequirement + 2)
    }

    fun dispose() {
        if (video != null) {
            video?.dispose()
            video = null
        }
        tracked_target = 0
        active_target = 0
        for (tracker in trackers) {
            tracker.dispose()
        }
        trackers.clear()
        if (video_renderers != null) {
            for (video_renderer in video_renderers!!) {
                video_renderer.dispose()
            }
            video_renderers = null
        }
        current_video_renderer = null
        if (bgRenderer != null) {
            bgRenderer = null
        }
        if (camera != null) {
            camera!!.dispose()
            camera = null
        }
        if (scheduler != null) {
            scheduler!!.dispose()
            scheduler = null
        }
    }

    fun start(): Boolean {
        var status = true
        status = if (camera != null) {
            status and camera!!.start()
        } else {
            false
        }
        for (tracker in trackers) {
            status = status and tracker.start()
        }
        return status
    }

    fun stop() {
        if (camera != null) {
            camera!!.stop()
        }
        for (tracker in trackers) {
            tracker.stop()
        }
    }

    fun render(width: Int, height: Int, screenRotation: Int) {
        while (scheduler!!.runOne()) {
        }
        GLES20.glViewport(0, 0, width, height)
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        val oframe = oFrameBuffer!!.peek() ?: return
        val iframe = oframe.inputFrame()
        val cameraParameters = iframe.cameraParameters()
        val viewport_aspect_ratio = width.toFloat() / height.toFloat()
        val imageProjection =
            cameraParameters.imageProjection(viewport_aspect_ratio, screenRotation, true, false)
        val image = iframe.image()
        try {
            if (iframe.index() != previousInputFrameIndex) {
                val buffer = image.buffer()
                try {
                    if (imageBytes == null || imageBytes!!.size != buffer.size()) {
                        imageBytes = ByteArray(buffer.size())
                    }
                    buffer.copyToByteArray(imageBytes!!)
                    bgRenderer?.upload(
                        image.format(),
                        image.width(),
                        image.height(),
                        ByteBuffer.wrap(imageBytes!!)
                    )
                } finally {
                    buffer.dispose()
                }
                previousInputFrameIndex = iframe.index()
            }
            bgRenderer?.render(imageProjection)
            val projectionMatrix = cameraParameters.projection(
                0.01f,
                1000f,
                viewport_aspect_ratio,
                screenRotation,
                true,
                false
            )
            for (oResult in oframe.results()) {
                if (oResult is ImageTrackerResult) {
                    val targetInstances = oResult.targetInstances()
                    for (targetInstance in targetInstances) {
                        if (targetInstance.status() == TargetStatus.Tracked) {
                            val target = targetInstance.target()
                            val id = target!!.runtimeID()
                            if (active_target != 0 && active_target != id) {
                                video?.onLost()
                                video?.dispose()
                                video = null
                                tracked_target = 0
                                active_target = 0
                            }
                            if (tracked_target == 0) {
                                if (video == null && video_renderers!!.size > 0) {
                                    val target_name = target.name()
                                    images?.forEachIndexed { index, img ->
                                        if(img.name == target_name && video_renderers!![index].texId() != 0) {
                                            video = videos?.let { ARVideo(it) }
                                            video?.openVideoFile(
                                                video_renderers!![index].texId(),
                                                scheduler
                                            )
                                            current_video_renderer = video_renderers!![index]
                                        }
                                    }
                                }
                                if (video != null) {
                                    video?.onFound()
                                    tracked_target = id
                                    active_target = id
                                }
                            }
                            val imagetarget = if (target is ImageTarget) target else null
                            if (imagetarget != null) {
                                val scale = Vec2F(
                                    imagetarget.scale(),
                                    imagetarget.scale() / imagetarget.aspectRatio()
                                )
                                if (current_video_renderer != null) {
                                    video?.update()
                                    if (video?.isRenderTextureAvailable == true) {
                                        current_video_renderer?.render(
                                            projectionMatrix,
                                            targetInstance.pose(),
                                            scale
                                        )
                                    }
                                }
                            }
                            target.dispose()
                        }
                        targetInstance.dispose()
                    }
                    if (targetInstances.size == 0) {
                        if (tracked_target != 0) {
                            video?.onLost()
                            tracked_target = 0
                        }
                    }
                }
                oResult?.dispose()
            }
        } finally {
            iframe.dispose()
            oframe.dispose()
            cameraParameters.dispose()
            image.dispose()
        }
    }

    init {
        scheduler = DelayedCallbackScheduler()
        trackers = ArrayList()
    }
}