package com.vincent.android.cili.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vincent.android.cili.R
import com.vincent.android.cili.viewmodel.CommentViewModel
import com.vincent.android.cili.entity.CommentResponse
import com.vincent.android.cili.entity.Resource
import com.vincent.android.cili.extensions.loadImage
import kotlinx.coroutines.flow.collectLatest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoCommentFragment : Fragment() {
    private val commentViewModel: CommentViewModel by viewModels()
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: CommentAdapter
    private val commentList = mutableListOf<CommentResponse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_video_comment, container, false)
        recycler = view.findViewById(R.id.recycler_comment)
        recycler.layoutManager = LinearLayoutManager(context)
        adapter = CommentAdapter(commentList)
        recycler.adapter = adapter
        return view
    }

    // 提供公开刷新方法
    fun refreshComments() {
        val videoId = activity?.intent?.getParcelableExtra<com.vincent.android.cili.entity.VideoEntity>("video")?.id
        if (videoId != null) {
            commentViewModel.getComments(videoId)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 获取 videoId
        val videoId = activity?.intent?.getParcelableExtra<com.vincent.android.cili.entity.VideoEntity>("video")?.id
        if (videoId != null) {
            commentViewModel.getComments(videoId)
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            commentViewModel.commentList.collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        commentList.clear()
                        commentList.addAll(result.data ?: emptyList())
                        adapter.notifyDataSetChanged()
                        // 滚动到顶部（最新评论）
                        if (commentList.isNotEmpty()) {
                            recycler.scrollToPosition(0)
                        }
                    }
                    is Resource.Error -> {
                        // 可选：显示错误提示
                    }
                    else -> {}
                }
            }
        }
    }

    companion object {
        fun newInstance(): VideoCommentFragment = VideoCommentFragment()
    }
}

// 只保留 CommentAdapter，数据类型用 CommentResponse
class CommentAdapter(private val list: List<CommentResponse>) : RecyclerView.Adapter<CommentAdapter.VH>() {
    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val user: TextView = view.findViewById(R.id.txt_comment_user)
        val content: TextView = view.findViewById(R.id.txt_comment_content)
        val date: TextView = view.findViewById(R.id.txt_comment_date)
        val avatar: ImageView = view.findViewById(R.id.iv_avatar)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return VH(v)
    }
    override fun getItemCount() = list.size
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]
        holder.user.text = item.username
        holder.content.text = item.content
        holder.date.text = item.createTime ?: ""
        item.avatar?.let { holder.avatar.loadImage(it, isCircle = true) }
    }
} 