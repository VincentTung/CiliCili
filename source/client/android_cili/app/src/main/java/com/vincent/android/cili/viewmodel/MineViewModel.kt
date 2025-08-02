package com.vincent.android.cili.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.android.cili.entity.UserInfo
import com.vincent.android.cili.use_case.user_info.UserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class MineViewModel @Inject constructor(
    private val userInfoUseCase: UserInfoUseCase
) : ViewModel() {

    private var _userInfo: MutableState<UserInfo> = mutableStateOf(UserInfo())
    val userInfo: State<UserInfo> get() = _userInfo
    
    private var _isRefreshing: MutableState<Boolean> = mutableStateOf(false)
    val isRefreshing: State<Boolean> get() = _isRefreshing
    
    fun getUserInfo() {
        userInfoUseCase.invoke().onStart { 
            _isRefreshing.value = true
        }.onEach {
            it.data?.let {
                _userInfo.value = it
            }
            _isRefreshing.value = false
        }.catch { 
            _isRefreshing.value = false
        }.launchIn(viewModelScope)
    }
    
    fun refreshUserInfo() {
        getUserInfo()
    }
}