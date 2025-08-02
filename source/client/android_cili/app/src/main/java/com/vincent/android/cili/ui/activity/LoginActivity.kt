package com.vincent.android.cili.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.vincent.android.cili.R
import com.vincent.android.cili.entity.LoginResult
import com.vincent.android.cili.extensions.showToast
import com.vincent.android.cili.util.AccountManager
import com.vincent.android.cili.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import com.vincent.android.cili.extensions.log

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    private lateinit var loadingView: ProgressBar
    private lateinit var nameView: EditText
    private lateinit var pwdView: EditText


    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, LoginActivity::class.java)
            context.startActivity(starter)
        }
    }

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setUpUI()
        observeData()
    }

    private fun observeData() {


        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    loginViewModel.loginChannel.consumeAsFlow().collect {
                        if (it.isSuccess()) {
                            log("observeData: isSuccess:${it.toString()}")
                            //login success
                            storeUserInfo(it)
                            loginViewModel.getUserInfo()

                        } else {
                            it.msg?.let { msg -> showToast(msg) }
                        }
                    }

                }

                launch {
                    loginViewModel.loadingState.collect { isLoading ->
                        loadingView.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
                    }
                }
                launch {
                    loginViewModel.userInfo.collect {

                        it?.let {
                            AccountManager.setUserInfo(it)
                            MainActivity.start(this@LoginActivity)
                            finish()
                        }

                    }
                }

            }
        }

    }

    private fun storeUserInfo(data: LoginResult?) {
        data?.let {
            log("storeUserInfo: ${it.token.toString()}")
            AccountManager.userToken = it.token.toString()
        }
    }

    private fun setUpUI() {
        nameView = findViewById(R.id.et_name)
        pwdView = findViewById(R.id.et_pwd)
        loadingView = findViewById(R.id.loading)
        findViewById<Button>(R.id.btn_login).setOnClickListener {
            val name = nameView.text.toString()
            val pwd = pwdView.text.toString()

            if (name.isNotEmpty() && pwd.isNotEmpty()) {
                doLogin(name, pwd)
            } else {
                showToast(R.string.error)
            }

        }

    }

    private fun doLogin(name: String, pwd: String) {
        loginViewModel.login(name, pwd)
    }
}