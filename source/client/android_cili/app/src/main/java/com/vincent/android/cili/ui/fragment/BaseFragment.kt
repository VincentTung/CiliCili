package com.vincent.android.cili.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.vincent.android.cili.ui.activity.BaseActivity

open class BaseFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun <T : ViewModel> getActivityScopeVM(vmClass: Class<T>): T? {

        val activity = requireActivity()
        return if (activity is BaseActivity) {

            activity.getActivityScopeVM(vmClass)

        } else {
            null
        }
    }
}