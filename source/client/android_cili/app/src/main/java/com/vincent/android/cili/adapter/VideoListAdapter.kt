package com.vincent.android.cili.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vincent.android.cili.R
import com.vincent.android.cili.databinding.VideolistItemBinding
import com.vincent.android.cili.databinding.VideolistItemHorizontalBinding
import com.vincent.android.cili.entity.VideoEntity
import com.vincent.android.cili.extensions.loadImage
import com.vincent.android.cili.extensions.loadRoundImage
import com.vincent.android.cili.ui.activity.VideoDetailActivity

class VideoListAdapter(private val isVertical: Boolean = true) :
    ListAdapter<VideoEntity, VideoListAdapter.ViewHolder>(VideoDiffCallback()) {

    companion object {
        @JvmStatic
        @BindingAdapter("load_image")
        fun loadImage(view: ImageView, cover: String) {
            view.loadRoundImage(cover, 6)
        }

        @JvmStatic
        @BindingAdapter("load_circle_image")
        fun loadCircleImage(view: ImageView, cover: String) {
            view.loadImage(cover, isCircle = true)
        }
    }
    var onItemClick: ((VideoEntity, Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = if (isVertical) {
            DataBindingUtil.inflate<VideolistItemBinding>(inflater, R.layout.videolist_item, parent, false)
        } else {
            DataBindingUtil.inflate<VideolistItemHorizontalBinding>(inflater, R.layout.videolist_item_horizontal, parent, false)
        }
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = getItem(position)
        holder.bind(video)
        holder.root.setOnClickListener {
            VideoDetailActivity.start(it.context, video)
            onItemClick?.invoke(video, position)
        }
    }

    fun refreshData(list: List<VideoEntity>?) {
        submitList(list?.toList() ?: emptyList())
    }

    fun addData(list: List<VideoEntity>?) {
        if (list.isNullOrEmpty()) return
        val newList = ArrayList(currentList)
        newList.addAll(list)
        submitList(newList)
    }

    class ViewHolder(private val binding: Any) : RecyclerView.ViewHolder(
        when (binding) {
            is VideolistItemBinding -> binding.root
            is VideolistItemHorizontalBinding -> binding.root
            else -> throw IllegalArgumentException("Unknown binding type")
        }
    ) {
        val root = when (binding) {
            is VideolistItemBinding -> binding.root
            is VideolistItemHorizontalBinding -> binding.root
            else -> throw IllegalArgumentException("Unknown binding type")
        }

        fun bind(video: VideoEntity) {
            when (binding) {
                is VideolistItemBinding -> binding.video = video
                is VideolistItemHorizontalBinding -> binding.video = video
            }
        }
    }

    class VideoDiffCallback : DiffUtil.ItemCallback<VideoEntity>() {
        override fun areItemsTheSame(oldItem: VideoEntity, newItem: VideoEntity): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: VideoEntity, newItem: VideoEntity): Boolean {
            return oldItem == newItem
        }
    }

}