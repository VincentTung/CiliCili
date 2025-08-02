package com.vincent.android.cili.ui.activity

import android.app.Activity
import android.app.Application
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 *  BaseActivity
 */
open class BaseActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = Color.WHITE
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        //        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

    }
    private val mActivityVMProvider: ViewModelProvider by lazy {
        ViewModelProvider(this)
    }

    private val mApplicationVMProvider: ViewModelProvider by lazy {
        ViewModelProvider(this, getAppFactory(this))
    }

     fun <T : ViewModel> getActivityScopeVM(vmClass: Class<T>): T {

        return mActivityVMProvider[vmClass]
    }


     fun <T : ViewModel> getApplicationScopeVM(vmClass: Class<T>): T {
        return mApplicationVMProvider[vmClass]
    }

    private fun getAppFactory(activity: Activity): ViewModelProvider.Factory {
        return ViewModelProvider.AndroidViewModelFactory.getInstance(checkApplication(activity))
    }

    private fun checkApplication(activity: Activity): Application {

        return activity.application
            ?: throw IllegalStateException(
                "Your activity/fragment is not yet attached to Application. You can't request ViewModel before onCreate call."
            )
    }

}