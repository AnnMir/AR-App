package com.example.ar_app.ui.main.view

import android.content.Intent
import com.example.ar_app.R
import com.example.ar_app.databinding.ActivityMainBinding
import com.example.ar_app.ui.ar.view.ImageArActivity
import com.example.ar_app.ui.common.view.base.BaseActivity
import com.example.ar_app.ui.main.presenter.MainPresenter
import javax.inject.Inject

class MainActivity : BaseActivity<ActivityMainBinding>(), MainView {

    @Inject
    lateinit var presenter: MainPresenter

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun setupUI() {
        super.setupUI()
        binding.buttonStart.setOnClickListener {
            val intent = Intent(this, ImageArActivity::class.java)
            startActivity(intent)
        }
    }
}