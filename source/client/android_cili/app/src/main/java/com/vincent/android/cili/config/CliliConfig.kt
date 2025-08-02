package com.vincent.android.cili.config

const val BASE_URL = "http://192.168.1.1:8080/api/"
const val API_KEY = "your_api_key_here"

// 分页配置
const val DEFAULT_PAGE_SIZE = 10
const val MAX_PAGE_SIZE = 50

// 错误码配置
const val ERROR_CODE_UNKNOWN = 20000
const val ERROR_CODE_NETWORK_UNAVAILABLE = 503
const val ERROR_CODE_TOKEN_EXPIRED = 401

// 网络配置
const val NETWORK_TIMEOUT_SECONDS = 30L
const val NETWORK_RETRY_COUNT = 3

// 缓存配置
const val CACHE_SIZE_MB = 10L
const val CACHE_MAX_AGE_HOURS = 24L

// 动画配置
const val ANIMATION_DURATION_SHORT = 200L
const val ANIMATION_DURATION_MEDIUM = 500L
const val ANIMATION_DURATION_LONG = 1000L

// 视频播放配置
const val VIDEO_BUFFER_SIZE_MS = 5000L
const val VIDEO_SEEK_INCREMENT_MS = 10000L

// 弹幕配置
const val BARRAGE_MAX_LENGTH = 50
const val BARRAGE_DISPLAY_TIME_MS = 6000L
