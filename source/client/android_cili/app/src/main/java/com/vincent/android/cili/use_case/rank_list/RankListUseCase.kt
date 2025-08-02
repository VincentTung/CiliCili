package com.vincent.android.cili.use_case.rank_list

import com.vincent.android.cili.config.DEFAULT_PAGE_SIZE
import com.vincent.android.cili.data.repository.VideoRepository
import com.vincent.android.cili.entity.VideoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RankListUseCase @Inject constructor(private val mVideoRepository: VideoRepository) {

    operator fun invoke(
        sort: String,
        pageIndex: Int = 1,
        pageSize: Int = DEFAULT_PAGE_SIZE
    ): Flow<List<VideoEntity>> = flow {
        emit(mVideoRepository.getRankList(sort,pageIndex,pageSize))
    }
}