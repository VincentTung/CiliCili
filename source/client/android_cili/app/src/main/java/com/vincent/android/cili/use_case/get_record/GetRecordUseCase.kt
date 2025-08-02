package com.vincent.android.cili.use_case.get_record

import com.vincent.android.cili.data.repository.VideoRepository
import com.vincent.android.cili.entity.RankListEntity
import com.vincent.android.cili.entity.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

enum class RecordType {
    COIN, LIKE, VIEW
}

class GetRecordUseCase @Inject constructor(private val mVideoRepository: VideoRepository) {
    operator fun invoke(type: RecordType): Flow<ApiResult<RankListEntity>> = flow {
        when (type) {
            RecordType.COIN -> emit(mVideoRepository.coinRecord())
            RecordType.LIKE -> emit(mVideoRepository.likeRecord())
            RecordType.VIEW -> emit(mVideoRepository.viewRecord())
        }
    }
}