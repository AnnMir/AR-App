package com.example.ar_app.ui.main.view

import androidx.annotation.StringRes

interface MainView {

    fun toImageArActivity()

    fun showErrorMessage(@StringRes msg: Int)
}