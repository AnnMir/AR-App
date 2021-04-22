package com.example.ar_app.ui.common.view.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.ar_app.R
import dagger.android.support.DaggerAppCompatActivity

abstract class BaseActivity<DataBinding : ViewDataBinding> : DaggerAppCompatActivity() {

    protected lateinit var binding: DataBinding

    @LayoutRes
    protected abstract fun getLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    protected open fun setupUI() {
        binding = DataBindingUtil.setContentView(this, getLayoutId())
    }

    fun showMessageDialog(messageId: Int) {
        showMessageDialog(getString(messageId))
    }

    fun showMessageDialog(message: String) {
        showMessageDialog(null, message)
    }

    fun showMessageDialog(
        title: String?,
        message: String,
        @StringRes positiveButtonRes: Int = R.string.text_dialog_ok,
        positiveAction: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(this)
            .apply {
                setTitle(title)
                setMessage(message)
                setPositiveButton(positiveButtonRes) { _, _ -> positiveAction?.invoke() }
                setCancelable(false)
            }.show()
    }
}