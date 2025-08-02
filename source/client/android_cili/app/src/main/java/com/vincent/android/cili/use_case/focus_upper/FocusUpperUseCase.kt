package com.vincent.android.cili.use_case.focus_upper

import com.vincent.android.cili.data.repository.VideoRepository
import com.vincent.android.cili.entity.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FocusUpperUseCase @Inject constructor(private val videoRepository: VideoRepository) {

    operator fun invoke(upperId: Int, isFocus: Boolean): Flow<ApiResult<String>> = flow {
        if (isFocus) {
            emit(videoRepository.focusUpper(upperId))
        } else {
            emit(videoRepository.cancelFocusUpper(upperId))
        }
    }
}