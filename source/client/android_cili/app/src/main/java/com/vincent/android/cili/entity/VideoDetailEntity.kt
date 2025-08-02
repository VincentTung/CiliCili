package com.vincent.android.cili.entity

import com.squareup.moshi.JsonClass
// import com.vincent.android.cili.annotation.NoArgAllOpen
import android.os.Parcel
import android.os.Parcelable
import com.vincent.android.cili.viewmodel.VideoDetailViewModel
import com.vincent.android.cili.viewmodel.VideoDetailViewModel.VideoOption

// @NoArgAllOpen
@JsonClass(generateAdapter = true)
data class VideoDetailEntity(

    val isFavorite: Boolean = false,
    val isLike: Boolean = false,
    val isHate: Boolean = false,
    val isCoin: Boolean = false,
    val videoInfo: VideoEntity? = null,
    val videoList: List<VideoEntity>? = null,
    val option: VideoOption = VideoOption.DEFAULT,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readParcelable(VideoEntity::class.java.classLoader),
        parcel.createTypedArrayList(VideoEntity.CREATOR)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (isFavorite) 1 else 0)
        parcel.writeByte(if (isLike) 1 else 0)
        parcel.writeByte(if (isHate) 1 else 0)
        parcel.writeByte(if (isCoin) 1 else 0)
        parcel.writeParcelable(videoInfo, flags)
        parcel.writeTypedList(videoList)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<VideoDetailEntity> {
        override fun createFromParcel(parcel: Parcel): VideoDetailEntity {
            return VideoDetailEntity(parcel)
        }
        override fun newArray(size: Int): Array<VideoDetailEntity?> {
            return arrayOfNulls(size)
        }
    }
}
