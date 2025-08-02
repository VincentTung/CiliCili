package com.vincent.android.cili.di

import com.vincent.android.cili.network.ApiHelper
import com.vincent.android.cili.data.api.VideoApi
import com.vincent.android.cili.data.repository.LoginRepository
import com.vincent.android.cili.data.repository.VideoRepository
import com.vincent.android.cili.data.repository.CommentRepository
import com.vincent.android.cili.data.repository.impl.LoginRepositoryImpl
import com.vincent.android.cili.data.repository.impl.VideoRepositoryImpl
import com.vincent.android.cili.data.repository.impl.CommentRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideVideoApi(): VideoApi {
        return ApiHelper.instance.videoService
    }

    @Provides
    @Singleton
    fun provideLoginRepository(api: VideoApi): LoginRepository {
        return LoginRepositoryImpl(api)
    }


    @Provides
    @Singleton
    fun provideVideoRepository(api: VideoApi): VideoRepository {
        return VideoRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideCommentRepository(api: VideoApi): CommentRepository {
        return CommentRepositoryImpl(api)
    }
}