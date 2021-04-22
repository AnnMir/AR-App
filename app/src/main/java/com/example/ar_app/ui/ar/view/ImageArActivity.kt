package com.example.ar_app.ui.ar.view

import android.Manifest
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.MenuItem
import android.view.ViewGroup
import android.view.WindowManager
import cn.easyar.*
import com.example.ar_app.R
import com.example.ar_app.databinding.ActivityImageArBinding
import com.example.ar_app.easyar.GLView
import com.example.ar_app.ui.ar.presenter.ImageArPresenter
import com.example.ar_app.ui.common.view.base.BaseActivity
import java.util.*
import javax.inject.Inject

class ImageArActivity : BaseActivity<ActivityImageArBinding>(), ImageArView {

    @Inject
    lateinit var presenter: ImageArPresenter

    @Inject
    lateinit var glView: GLView

    override fun getLayoutId(): Int = R.layout.activity_image_ar

    override fun setupUI() {
        super.setupUI()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!Engine.initialize(this, getString(R.string.key))) {
            Log.e("HelloAR", "Initialization Failed.")
            showMessageDialog(message = Engine.errorMessage())
            return
        }
        if (!CameraDevice.isAvailable()) {
            showMessageDialog(R.string.error_camera_title, R.string.error_camera)
            return
        }
        if (!ImageTracker.isAvailable()) {
            showMessageDialog(R.string.error_image_tracker_title, R.string.error_image_tracker)
            return
        }
        if (!VideoPlayer.isAvailable()) {
            showMessageDialog(R.string.error_video_player_title, R.string.error_video_player)
            return
        }

        requestCameraPermission(object : PermissionCallback {
            override fun onSuccess() {
                findViewById<ViewGroup>(R.id.preview).addView(
                    glView,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
            }

            override fun onFailure() {}
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private interface PermissionCallback {
        fun onSuccess()
        fun onFailure()
    }

    private val permissionCallbacks = HashMap<Int, PermissionCallback>()
    private var permissionRequestCodeSerial = 0

    @TargetApi(23)
    private fun requestCameraPermission(callback: PermissionCallback) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                val requestCode = permissionRequestCodeSerial
                permissionRequestCodeSerial += 1
                permissionCallbacks[requestCode] = callback
                requestPermissions(arrayOf(Manifest.permission.CAMERA), requestCode)
            } else {
                callback.onSuccess()
            }
        } else {
            callback.onSuccess()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (permissionCallbacks.containsKey(requestCode)) {
            val callback = permissionCallbacks[requestCode]!!
            permissionCallbacks.remove(requestCode)
            var executed = false
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    executed = true
                    callback.onFailure()
                }
            }
            if (!executed) {
                callback.onSuccess()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onResume() {
        super.onResume()
        glView.onResume()
    }

    override fun onPause() {
        glView.onPause()
        super.onPause()
    }

    override fun onStop() {
        findViewById<ViewGroup>(R.id.preview).removeView(glView)
        super.onStop()
    }
}