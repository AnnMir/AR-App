package com.example.ar_app.ui.ar.view

import android.Manifest
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.MenuItem
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import cn.easyar.*
import com.example.ar_app.R
import com.example.ar_app.databinding.ActivityImageArBinding
import com.example.ar_app.easyar.GLView
import com.example.ar_app.ui.ar.presenter.ImageArPresenter
import com.example.ar_app.ui.common.view.base.BaseActivity
import java.util.*
import javax.inject.Inject

class ImageArActivity : BaseActivity<ActivityImageArBinding>(), ImageArView {

    private var glView: GLView? = null

    @Inject
    lateinit var presenter: ImageArPresenter

    override fun getLayoutId(): Int = R.layout.activity_image_ar

    override fun setupUI() {
        super.setupUI()
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!Engine.initialize(this, getString(R.string.key))) {
            Log.e("HelloAR", "Initialization Failed.")
            Toast.makeText(this@ImageArActivity, Engine.errorMessage(), Toast.LENGTH_LONG).show()
            return
        }
        if (!CameraDevice.isAvailable()) {
            Toast.makeText(this@ImageArActivity, "CameraDevice not available.", Toast.LENGTH_LONG).show()
            return
        }
        if (!ImageTracker.isAvailable()) {
            Toast.makeText(this@ImageArActivity, "ImageTracker not available.", Toast.LENGTH_LONG).show()
            return
        }
        if (!VideoPlayer.isAvailable()) {
            Toast.makeText(this@ImageArActivity, "VideoPlayer not available.", Toast.LENGTH_LONG).show()
            return
        }

        glView = GLView(this, presenter.getVideos(), presenter.getImages())

        requestCameraPermission(object : PermissionCallback {
            override fun onSuccess() {
                findViewById<ViewGroup>(R.id.preview).addView(glView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            }

            override fun onFailure() {}
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
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
        if (glView != null) {
            glView!!.onResume()
        }
    }

    override fun onPause() {
        if (glView != null) {
            glView!!.onPause()
        }
        super.onPause()
    }

    override fun getFilesFromAssets(): Array<String>? {
        return assets.list("")
    }
}