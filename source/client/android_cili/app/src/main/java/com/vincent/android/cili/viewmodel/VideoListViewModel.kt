package com.vincent.android.cili.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.android.cili.config.DEFAULT_PAGE_SIZE
import com.vincent.android.cili.entity.VideoEntity
import com.vincent.android.cili.use_case.video_list.GetVideoListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val getVideoListUseCase: GetVideoListUseCase
) : ViewModel() {
    private val _videoList = MutableLiveData<MutableList<VideoEntity>>(mutableListOf())
    val videoList: LiveData<MutableList<VideoEntity>> get() = _videoList

    private val _newPage = MutableLiveData<List<VideoEntity>>()
    val newPage: LiveData<List<VideoEntity>> get() = _newPage

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isRefreshing = MutableLiveData<Boolean>(false)
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    private val _noMoreData = MutableLiveData<Boolean>(false)
    val noMoreData: LiveData<Boolean> get() = _noMoreData

    private var currentPage = 1
    private var currentCategory: String = ""
    private var pageSize = DEFAULT_PAGE_SIZE

    fun refresh(category: String, pageSize: Int = DEFAULT_PAGE_SIZE) {
        if (_isRefreshing.value == true) return
        _isRefreshing.value = true
        currentPage = 1
        currentCategory = category
        this.pageSize = pageSize
        _noMoreData.value = false
        viewModelScope.launch {
            getVideoListUseCase(category, currentPage, pageSize).onEach {
                _videoList.value = it.toMutableList()
                _isRefreshing.value = false
                _noMoreData.value = it.size < pageSize
                // 刷新时不发增量
            }.launchIn(viewModelScope)
        }
    }

    fun loadMore() {
        if (_isLoading.value == true || _noMoreData.value == true) return
        _isLoading.value = true
        val nextPage = currentPage + 1
        viewModelScope.launch {
            getVideoListUseCase(currentCategory, nextPage, pageSize).onEach { newList ->
                val currentList = _videoList.value ?: mutableListOf()
                if (newList.isNotEmpty()) {
                    currentList.addAll(newList)
                    _videoList.value = currentList
                    _newPage.value = newList // 只发增量
                    currentPage = nextPage
                }
                _isLoading.value = false
                _noMoreData.value = newList.size < pageSize
            }.launchIn(viewModelScope)
        }
    }

    fun initCategory(category: String, pageSize: Int = DEFAULT_PAGE_SIZE) {
        if (currentCategory != category) {
            refresh(category, pageSize)
        }
    }
}