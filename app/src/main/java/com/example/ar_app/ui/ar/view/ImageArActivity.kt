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

class ImageArActivity : BaseActivity<ActivityImageArBinding>(), ImageArView {

    companion object {
        const val KEY =
            "d0NBInNQWT5rNsyJc1e963aduSuTLDLN8t9pWUdxdwlzYXEUR2xmWQggc1Vfa2ACU2x9DVNCcwFdZGZVUW1/WR4gfxpBdncJeWdrMlYgKEoeIH4SUWd8CFdxMEFpeTAZR2x2F1dLdggQOEkmHiBkGkBrcxVGcTBBaSBxFF9vZxVbdmtZby4wC15jZh1dcH8IEDhJWUVrfB9ddWFZHiB/GlEgT1cQb30fR253CBA4SVlBZ3wIVyxbFlNldy9AY3EQW2x1WR4gYR5ccXdVcW59DlZQdxhdZXwSRmt9FRAuMAhXbGEeHFB3GF1wdhJcZTBXEHF3FUFnPDRQaHcYRlZgGlFpexVVID5ZQWd8CFcsQQ5AZHMYV1ZgGlFpexVVID5ZQWd8CFcsQQtTcGEeYXJzD1tjfjZTcjBXEHF3FUFnPDZddnsUXFZgGlFpexVVID5ZQWd8CFcsVh5ccXcoQmNmElNuXxpCID5ZQWd8CFcsUTp2VmAaUWl7FVUgT1cQZ2oLW3B3L1tvdyhGY38LEDh8Dl5uPllbcV4UUWN+WQhkcxdBZ29XSSBwDlxmfh57ZmFZCFkwGF1vPB5KY38LXmc8GkBdcwtCIE9XEHRzCVtjfA9BICggEGF9Fl93fBJGezAmHiBiF1N2dBRAb2FZCFkwGlxmYBRbZjAmHiB/FFZ3fh5BICggEHF3FUFnPDJfY3UeZnBzGFlrfBwQLjAIV2xhHhxBfhRHZkAeUW11FVt2exRcID5ZQWd8CFcsQB5RbWAfW2x1WR4gYR5ccXdVfWB4HlF2RglTYXkSXGUwVxBxdxVBZzwoR3B0GlFnRglTYXkSXGUwVxBxdxVBZzwoQmNgCFdRYhpGa3MXf2NiWR4gYR5ccXdVf21mEl1sRglTYXkSXGUwVxBxdxVBZzw/V2xhHmFycw9bY342U3IwVxBxdxVBZzw4c0ZGCVNheRJcZTAmHiB3A0JrYB5ma38eYXZzFkIgKBVHbn5XEGthN11hcxcQOHQaXnF3Bh55MBlHbHYXV0t2CBA4SVkQXz5ZRGNgElNsZggQOElZUW1/Fkdsew9LIE9XEHJ+GkZkfQlfcTBBaSB7FEEgT1cQb30fR253CBA4SVlBZ3wIVyxbFlNldy9AY3EQW2x1WR4gYR5ccXdVcW59DlZQdxhdZXwSRmt9FRAuMAhXbGEeHFB3GF1wdhJcZTBXEHF3FUFnPDRQaHcYRlZgGlFpexVVID5ZQWd8CFcsQQ5AZHMYV1ZgGlFpexVVID5ZQWd8CFcsQQtTcGEeYXJzD1tjfjZTcjBXEHF3FUFnPDZddnsUXFZgGlFpexVVID5ZQWd8CFcsVh5ccXcoQmNmElNuXxpCID5ZQWd8CFcsUTp2VmAaUWl7FVUgT1cQZ2oLW3B3L1tvdyhGY38LEDh8Dl5uPllbcV4UUWN+WQhkcxdBZ28mTzBzsGCLxGbA+gky2jKFQYS5gTjTE22Sw5r+39JSfgfI+LGQgxRJu3YMbkmpKiyW2M0s0anFWk4KpnfrTSfI4z4eSkd2by4zEG2lxSJrCU7BOsEKacRgkrU8r0zGhxqbK8ji/xgP5h2uUpcRSEqGRRWK7GZ9Df6TQqQsbkprs7FjaDaDMoYq/oAewwTDsXcGlXkIyvlSeNzgoaSd2CTmag4ezbMOJO9kKDtYaRMkBILhQ/oKkkbMC/Ds1lY2tSrqm1r4aQPjm1ovRxdSN48cS4WFrruXMj185gmPHWOyKRg5c/j0lMaLIa8mvuxcCs3o8Y/FIQ95+aOcabXkVTICEns="
    }

    private var glView: GLView? = null
    private lateinit var presenter: ImageArPresenter

    override fun getLayoutId(): Int = R.layout.activity_image_ar

    override fun setupUI() {
        super.setupUI()
        presenter = ImageArPresenter(this)
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!Engine.initialize(this, KEY)) {
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

//    override fun onDestroy() {
//        presenter.dispose()
//        super.onDestroy()
//    }

    override fun getFilesFromAssets(): Array<String>? {
        return assets.list("")
    }
}