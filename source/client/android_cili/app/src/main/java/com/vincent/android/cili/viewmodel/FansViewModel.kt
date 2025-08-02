package com.vincent.android.cili.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.android.cili.entity.Owner
import com.vincent.android.cili.use_case.fans_list.FansListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class FansViewModel @Inject constructor(
    private val fansListUseCase: FansListUseCase
): ViewModel() {

    private val _fanListState = mutableStateOf<List<Owner>>(emptyList())
    val fansList: State<List<Owner>> get() = _fanListState

    fun getFansList(){
        fansListUseCase().onStart {  }.onEach {
            _fanListState.value = it.data?.list ?: emptyList()
        }.onEmpty {  }.launchIn(viewModelScope)

    }
}