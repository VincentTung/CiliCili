package com.vincent.android.cili.entity

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.JsonClass
import com.vincent.android.cili.annotation.NoArgAllOpen

// @NoArgAllOpen
@JsonClass(generateAdapter = true)
data class VideoEntity(
    val like: Long = 0,
    val tname: String? = "",

    var title: String? = "",

    val url: String? = null,

    val fans: Long = 0,

    val vid: String? = null,

    val cover: String? = null,

    val duration: Int = 0,

    val view: Long = 0,

    val face: String? = null,

    val size: Int = 0,

    val createTime: String? = null,

    val name: String? = null,

    val share: Int = 0,

    val id: Int = 0,

    val reply: Int = 0,

    val favorite: Int = 0,
    val uper: Int = 0,

    val pubdate: Long = 0,
    val isFocus:Boolean=false,

    val desc: String? = null, val coin: Int = 0

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(like)
        parcel.writeString(tname)
        parcel.writeString(title)
        parcel.writeString(url)
        parcel.writeLong(fans)
        parcel.writeString(vid)
        parcel.writeString(cover)
        parcel.writeInt(duration)
        parcel.writeLong(view)
        parcel.writeString(face)
        parcel.writeInt(size)
        parcel.writeString(createTime)
        parcel.writeString(name)
        parcel.writeInt(share)
        parcel.writeInt(id)
        parcel.writeInt(reply)
        parcel.writeInt(favorite)
        parcel.writeInt(uper)
        parcel.writeLong(pubdate)
        parcel.writeByte(if (isFocus) 1 else 0)
        parcel.writeString(desc)
        parcel.writeInt(coin)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VideoEntity> {
        override fun createFromParcel(parcel: Parcel): VideoEntity {
            return VideoEntity(parcel)
        }

        override fun newArray(size: Int): Array<VideoEntity?> {
            return arrayOfNulls(size)
        }
    }


}