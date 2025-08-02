package com.vincent.android.cili.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.android.cili.entity.VideoEntity
import com.vincent.android.cili.use_case.collect_list.CollectListUseCase
import com.vincent.android.cili.extensions.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CollectViewModel @Inject constructor(
    private val collectListUseCase: CollectListUseCase
): ViewModel() {


    companion object {
        private val TAG: String = CollectViewModel::class.java.simpleName
    }

    private var _videoListState: MutableLiveData<List<VideoEntity>> = MutableLiveData()
    val videoList: LiveData<List<VideoEntity>> get() = _videoListState


    fun getCollectList() {
        collectListUseCase.invoke().onEach {
            log(TAG, "getCollectList:${it.data} ")
            _videoListState.value = it.data?.list
        }.launchIn(viewModelScope)
    }
}