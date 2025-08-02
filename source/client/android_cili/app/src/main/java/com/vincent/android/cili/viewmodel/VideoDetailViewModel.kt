package com.vincent.android.cili.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.vincent.android.cili.entity.Resource
import com.vincent.android.cili.entity.VideoDetailEntity
import com.vincent.android.cili.use_case.coin_video.CoinVideoUseCase
import com.vincent.android.cili.use_case.focus_upper.FocusUpperUseCase
import com.vincent.android.cili.use_case.like_video.LikeVideoUseCase
import com.vincent.android.cili.use_case.star_video.StarVideoUseCase
import com.vincent.android.cili.use_case.video_detail.GetVideoDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.vincent.android.cili.extensions.log
import com.vincent.android.cili.extensions.logE

@HiltViewModel
class VideoDetailViewModel @Inject constructor(
    private val detailUseCase: GetVideoDetailUseCase,
    private val likeVideoUseCase: LikeVideoUseCase,
    private val starVideoUseCase: StarVideoUseCase,
    private val coinVideoUseCase: CoinVideoUseCase,
    private val focusUpperUseCase: FocusUpperUseCase
) : ViewModel() {
    companion object {
        private val TAG = VideoDetailViewModel::class.java.simpleName
    }

    enum class VideoOption {
        DEFAULT, LIKE, UNLIKE, COIN, STAR, SHARE, FOCUS, UNFOCUS
    }


    private var _videoDetailEntity: VideoDetailEntity? = null
    private val _viewState: MutableStateFlow<Resource<VideoDetailEntity>> = MutableStateFlow(Resource.Loading())
    val videoState: StateFlow<Resource<VideoDetailEntity>> get() = _viewState.asStateFlow()

    private val _videoOptionChannel: Channel<VideoOption> = Channel(Channel.BUFFERED)

    fun sendOption(option: VideoOption) {
        viewModelScope.launch {
            _videoOptionChannel.send(option)
        }
    }

    init {

        viewModelScope.launch {
            _videoOptionChannel.consumeAsFlow().collect {

                log(TAG, "receive_option:$it ")
                when (it) {
                    VideoOption.LIKE -> likeVideo()
                    VideoOption.UNLIKE -> hateVideo()
                    VideoOption.COIN -> coinVideo()
                    VideoOption.STAR -> collectVideo()
                    VideoOption.FOCUS -> focusUper(true)
                    VideoOption.UNFOCUS -> focusUper(false)
                    else -> {}
                }
            }
        }
    }

    private fun focusUper(flag: Boolean) {
        _videoDetailEntity?.videoInfo?.let { video ->
            focusUpperUseCase.invoke(video.uper, flag).onStart {
                _viewState.value = Resource.Loading()
            }.onEach { result ->
                if (result.isSuccess()) {
                    _videoDetailEntity?.let {
                        _videoDetailEntity = it.copy(videoInfo = it.videoInfo?.let {
                            it.copy(
                                isFocus = flag, fans = if (flag) it.fans + 1 else it.fans - 1
                            )
                        })
                        _viewState.value = Resource.Success(_videoDetailEntity!!)
                    }
                } else {
                    _viewState.value = Resource.Error(result.msg ?: "关注操作失败")
                }
            }.catch { e ->
                logE(TAG, "Error focusing upper: ${e.message}", e)
                _viewState.value = Resource.Error(e.message ?: "关注操作失败")
            }.launchIn(viewModelScope)
        }
    }

    fun getVideoDetail(id: Int) {
        detailUseCase.invoke(id).onStart {
            _viewState.value = Resource.Loading()
        }.onEach { result ->
            _viewState.value = if (result.isSuccess()) {
                _videoDetailEntity = result.data
                Resource.Success(_videoDetailEntity!!)
            } else {
                Resource.Error(result.msg ?: "获取视频详情失败")
            }
        }.catch { e ->
            logE(TAG, "Error getting video detail: ${e.message}", e)
            _viewState.value = Resource.Error(e.message ?: "获取视频详情失败")
        }.launchIn(viewModelScope)
    }

    private fun collectVideo() {
        val videoInfo = videoState.value.data?.videoInfo

        videoInfo?.let { video ->
            val isCollect = videoState.value.data!!.isFavorite
            video.id.let { id ->
                val isNeedCollect = !isCollect
                starVideoUseCase.invoke(id, isNeedCollect).onStart {
                    _viewState.value = Resource.Loading()
                }.onEach { result ->
                    if (result.isSuccess()) {
                        _videoDetailEntity?.let {
                            _videoDetailEntity =
                                it.copy(isFavorite = !isCollect, videoInfo = it.videoInfo?.let {
                                    it.copy(favorite = if (isNeedCollect) it.favorite + 1 else it.favorite - 1)
                                })
                            _viewState.value = Resource.Success(_videoDetailEntity!!)
                        }
                    } else {
                        _viewState.value = Resource.Error(result.msg ?: "收藏操作失败")
                    }
                }.catch { e ->
                    logE(TAG, "Error collecting video: ${e.message}", e)
                    _viewState.value = Resource.Error(e.message ?: "收藏操作失败")
                }.launchIn(viewModelScope)
            }
        }
    }

    private fun coinVideo() {
        val videoInfo = videoState.value.data?.videoInfo
        videoInfo?.let { video ->
            video.id.let { id ->
                coinVideoUseCase(id).onStart {
                    _viewState.value = Resource.Loading()
                }.onEach { result ->
                    if (result.isSuccess()) {
                        _videoDetailEntity?.let {
                            _videoDetailEntity = it.copy(option = VideoOption.COIN, videoInfo = it.videoInfo?.let {
                                it.copy(coin = it.coin + 1)
                            })
                            _viewState.value = Resource.Success(_videoDetailEntity!!)
                        }
                    } else {
                        _viewState.value = Resource.Error(result.msg ?: "投币操作失败")
                    }
                }.catch { e ->
                    logE(TAG, "Error coining video: ${e.message}", e)
                    _viewState.value = Resource.Error(e.message ?: "投币操作失败")
                }.launchIn(viewModelScope)
            }
        }
    }

    private fun hateVideo() {


    }

    private fun likeVideo() {
        val videoInfo = videoState.value.data?.videoInfo
        videoInfo?.let { video ->
            video.id.let { id ->
                val needLike = _videoDetailEntity?.isLike != true
                likeVideoUseCase.invoke(id, needLike).onStart {
                    _viewState.value = Resource.Loading()
                }.onEach { result ->
                    if (result.isSuccess()) {
                        _videoDetailEntity?.let {
                            _videoDetailEntity =
                                it.copy(isLike = needLike, videoInfo = it.videoInfo?.let {
                                    it.copy(like = if (needLike) it.like + 1L else it.like - 1L)
                                })
                            _viewState.value = Resource.Success(_videoDetailEntity!!)
                            log(TAG, "1___${videoState.value.toString()}")
                        }
                    } else {
                        _viewState.value = Resource.Error(result.msg ?: "点赞操作失败")
                    }
                }.catch { e ->
                    logE(TAG, "Error liking video: ${e.message}", e)
                    _viewState.value = Resource.Error(e.message ?: "点赞操作失败")
                }.launchIn(viewModelScope)
            }
        }
    }
}