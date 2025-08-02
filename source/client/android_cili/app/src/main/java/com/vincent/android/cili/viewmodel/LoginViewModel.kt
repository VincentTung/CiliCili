package com.vincent.android.cili.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.android.cili.entity.LoginResult
import com.vincent.android.cili.entity.UserInfo
import com.vincent.android.cili.use_case.user_info.UserInfoUseCase
import com.vincent.android.cili.use_case.user_login.UserLoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val userLoginUseCase: UserLoginUseCase,
    val userInfoUseCase: UserInfoUseCase
) : ViewModel() {
    private val _loginState = MutableStateFlow(LoginResult())
    val loginState = _loginState.asStateFlow()
    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()
    private var _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo = _userInfo.asStateFlow()

    fun getUserInfo() {
        userInfoUseCase.invoke().onStart { }.onEach {
            it?.let {
                _userInfo.value = it.data
            }
        }.launchIn(viewModelScope)
    }
    // ?? get()导致无法收到channel数据  why
    private val loginStateChannel: Channel<LoginResult> = Channel(CONFLATED)

    val loginChannel get() = loginStateChannel
    fun login(username: String, pwd: String) {
        _loadingState.value = true
        userLoginUseCase.invoke(username, pwd).onEach {
            _loginState.value = it
            loginStateChannel.send(it)
            _loadingState.value = false
        }.launchIn(viewModelScope)
    }
}