package com.example.ar_app.ui.common.view.base

import android.os.Bundle
import androidx.annotation.LayoutRes
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

    fun showMessageDialog(titleId: Int? = null, messageId: Int) {
        showMessageDialog(titleId?.let { getString(it) }, getString(messageId))
    }

    fun showMessageDialog(title: String? = null, message: String) {
        showMessageDialog(title, message)
    }

    inline fun showMessageDialog(
        title: String?,
        message: String,
        crossinline positiveAction: () -> Unit = {},
        crossinline negativeAction: () -> Unit = {}
    ) {
        AlertDialog.Builder(this)
            .apply {
                setTitle(title)
                setMessage(message)
                setPositiveButton(R.string.text_dialog_ok) { _, _ -> positiveAction.invoke() }
                setNegativeButton(R.string.text_dialog_cancel) { _, _ -> negativeAction.invoke() }
            }.show()
    }
}