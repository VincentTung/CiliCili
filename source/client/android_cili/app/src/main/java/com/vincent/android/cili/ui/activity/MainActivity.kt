package com.vincent.android.cili.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vincent.android.cili.R
import com.vincent.android.cili.extensions.log
import com.vincent.android.cili.ui.fragment.CollectFragment
import com.vincent.android.cili.ui.fragment.FirstFragment
import com.vincent.android.cili.ui.fragment.MineFragment
import com.vincent.android.cili.ui.fragment.RankFragment
import com.vincent.android.cili.util.AccountManager
import com.vincent.android.cili.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    companion object {
        private const val TAG = "MainActivity"
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, MainActivity::class.java)
            context.startActivity(starter)
        }
    }

    private var currentTabIndex = 0
    private lateinit var bottomNav: BottomNavigationView

    private val firstFragment: FirstFragment by lazy { FirstFragment.Companion.newInstance() }
    private val rankFragment: RankFragment by lazy { RankFragment.Companion.newInstance() }
    private val collectFragment: CollectFragment by lazy { CollectFragment.Companion.newInstance() }
    private val mineFragment: MineFragment by lazy { MineFragment.Companion.newInstance() }

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log(TAG, "onCreate")
        setContentView(R.layout.activity_main)
        if (savedInstanceState != null) {
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            fragmentManager.fragments.forEach { fragment ->
                transaction.remove(fragment)
            }
            transaction.commitNowAllowingStateLoss()
            currentTabIndex = savedInstanceState.getInt("currentTabIndex", 0)
            changeFragment(currentTabIndex, force = true)
        } else {
            supportFragmentManager.beginTransaction()
                .add(R.id.nav_host_fragment, getFragment(0)!!)
                .commit()
            currentTabIndex = 0
        }
        setUpUI()
        if (AccountManager.userToken.isNotEmpty()) {
            lifecycleScope.launch {
                loginViewModel.userInfo.collect { info ->
                    info?.let { AccountManager.setUserInfo(it) }
                }
            }
            loginViewModel.getUserInfo()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentTabIndex", currentTabIndex)
    }

    private fun setUpUI() {
        bottomNav = findViewById(R.id.bottom_nav)
        bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_first -> changeFragment(0)
                R.id.menu_rank -> changeFragment(1)
                R.id.menu_collect -> changeFragment(2)
                R.id.menu_mine -> changeFragment(3)
            }
            true
        }
        bottomNav.selectedItemId = when (currentTabIndex) {
            1 -> R.id.menu_rank
            2 -> R.id.menu_collect
            3 -> R.id.menu_mine
            else -> R.id.menu_first
        }
    }

    private fun changeFragment(position: Int, force: Boolean = false) {
        if (!force && position == currentTabIndex) return
        val transaction = supportFragmentManager.beginTransaction()
        val newFragment = getFragment(position)
        val oldFragment = getFragment(currentTabIndex)
        if (newFragment == null) return
        if (!newFragment.isAdded) {
            transaction.add(R.id.nav_host_fragment, newFragment)
        }
        oldFragment?.let { transaction.hide(it) }
        transaction.show(newFragment)
        transaction.commit()
        currentTabIndex = position
    }

    private fun getFragment(position: Int): Fragment? {
        return when (position) {
            0 -> firstFragment
            1 -> rankFragment
            2 -> collectFragment
            3 -> mineFragment
            else -> null
        }
    }

    fun showCollectTab() {
        bottomNav.selectedItemId = R.id.menu_collect
    }
}