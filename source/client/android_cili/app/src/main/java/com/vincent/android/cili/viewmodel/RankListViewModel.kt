package com.vincent.android.cili.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.android.cili.entity.VideoEntity
import com.vincent.android.cili.use_case.rank_list.RankListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.ArrayList
import javax.inject.Inject


@HiltViewModel
class RankListViewModel @Inject constructor(
    private val rankListUseCase: RankListUseCase
) : ViewModel() {


    companion object {
        private val TAG: String = RankListViewModel::class.java.simpleName
    }

    private var _videoList: MutableLiveData<MutableList<VideoEntity>> = MutableLiveData()
    val videoList: LiveData<MutableList<VideoEntity>> get() = _videoList


    fun getRankList(sort: String, pageNum: Int) {
        rankListUseCase.invoke(sort, pageNum).onEach {
            val list = videoList.value ?: mutableListOf()
            list.addAll(it)
            _videoList.value = list

        }.launchIn(viewModelScope)
    }

    fun refreshData(sort: String) {
        rankListUseCase.invoke(sort).onEach {
            val list = videoList.value ?: mutableListOf()
            list.addAll(it)
            _videoList.value = list

        }.launchIn(viewModelScope)
    }

}