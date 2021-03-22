package com.example.ar_app.ui.main.presenter

import com.example.ar_app.ui.common.presenter.BasePresenter
import com.example.ar_app.ui.main.view.MainView
import javax.inject.Inject

class MainPresenterImpl @Inject constructor(
    private val mainView: MainView
) : BasePresenter(), MainPresenter {

}