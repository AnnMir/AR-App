package com.example.ar_app.ui.ar.presenter

import com.example.ar_app.ui.ar.view.ImageArView
import com.example.ar_app.ui.common.presenter.BasePresenter
import javax.inject.Inject

class ImageArPresenterImpl @Inject constructor(
    private val imageArView: ImageArView
) : BasePresenter(), ImageArPresenter {

}