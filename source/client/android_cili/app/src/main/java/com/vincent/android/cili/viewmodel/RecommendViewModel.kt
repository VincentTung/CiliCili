package com.vincent.android.cili.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.android.cili.config.DEFAULT_PAGE_SIZE
import com.vincent.android.cili.entity.BannerData
import com.vincent.android.cili.entity.Type
import com.vincent.android.cili.entity.VideoEntity
import com.vincent.android.cili.use_case.video_list.GetRecommendDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@HiltViewModel
class RecommendViewModel @Inject constructor(
    private val getRecommendDataUseCase: GetRecommendDataUseCase
) : ViewModel() {
    
    private val _videoList = MutableLiveData<MutableList<VideoEntity>>(mutableListOf())
    val videoList: LiveData<MutableList<VideoEntity>> get() = _videoList

    private val _bannerList = MutableLiveData<List<BannerData>>(emptyList())
    val bannerList: LiveData<List<BannerData>> get() = _bannerList

    private val _categoryList = MutableLiveData<List<Type>>(emptyList())
    val categoryList: LiveData<List<Type>> get() = _categoryList

    private val _newPage = MutableLiveData<List<VideoEntity>>()
    val newPage: LiveData<List<VideoEntity>> get() = _newPage

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isRefreshing = MutableLiveData<Boolean>(false)
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    private val _noMoreData = MutableLiveData<Boolean>(false)
    val noMoreData: LiveData<Boolean> get() = _noMoreData

    private var currentPage = 1
    private var pageSize = DEFAULT_PAGE_SIZE

    fun refresh(pageSize: Int = DEFAULT_PAGE_SIZE) {
        if (_isRefreshing.value == true) return
        _isRefreshing.value = true
        currentPage = 1
        this.pageSize = pageSize
        _noMoreData.value = false
        getRecommendDataUseCase(currentPage, pageSize).onEach { recommendData ->
            _videoList.value = recommendData.videoList.toMutableList()
            _bannerList.value = recommendData.bannerList
            _categoryList.value = recommendData.categoryList
            _isRefreshing.value = false
            _noMoreData.value = recommendData.videoList.size < pageSize
        }.catch { e ->
            _isRefreshing.value = false
            e.printStackTrace()
        }.launchIn(viewModelScope)
    }

    fun loadMore() {
        if (_isLoading.value == true || _noMoreData.value == true) return
        _isLoading.value = true
        val nextPage = currentPage + 1
        getRecommendDataUseCase(nextPage, pageSize).onEach { recommendData ->
            val currentList = _videoList.value ?: mutableListOf()
            if (recommendData.videoList.isNotEmpty()) {
                currentList.addAll(recommendData.videoList)
                _videoList.value = currentList
                _newPage.value = recommendData.videoList
                currentPage = nextPage
            }
            _isLoading.value = false
            _noMoreData.value = recommendData.videoList.size < pageSize
        }.catch { e ->
            _isLoading.value = false
            e.printStackTrace()
        }.launchIn(viewModelScope)
    }

    fun initRecommend(pageSize: Int = DEFAULT_PAGE_SIZE) {
        refresh(pageSize)
    }
} 