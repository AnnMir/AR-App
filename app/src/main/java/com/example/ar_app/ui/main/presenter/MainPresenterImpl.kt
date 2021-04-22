package com.example.ar_app.ui.main.presenter

import com.example.ar_app.R
import com.example.ar_app.data.Image
import com.example.ar_app.data.Video
import com.example.ar_app.ui.common.presenter.BasePresenter
import com.example.ar_app.ui.main.view.MainView
import javax.inject.Inject

class MainPresenterImpl @Inject constructor(
    private val mainView: MainView,
    private val images: List<Image>?,
    private val videos: List<Video>?
) : BasePresenter(), MainPresenter {

    override fun onStartClick() {
        checkResources()
    }

    private fun checkResources() {
        if (videos.isNullOrEmpty() || images.isNullOrEmpty()) {
            mainView.showErrorMessage(R.string.error_empty_resources)
        } else {
            mainView.toImageArActivity()
        }
    }
}