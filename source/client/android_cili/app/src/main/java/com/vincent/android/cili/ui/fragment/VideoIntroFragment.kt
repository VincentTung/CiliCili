package com.vincent.android.cili.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vincent.android.cili.R
import com.vincent.android.cili.entity.VideoDetailEntity
import com.vincent.android.cili.entity.VideoEntity
import com.vincent.android.cili.extensions.loadImage
import com.vincent.android.cili.extensions.loadRoundImage
import com.vincent.android.cili.extensions.showToast
import com.vincent.android.cili.ui.activity.VideoDetailActivity
import com.vincent.android.cili.util.AccountManager
import com.vincent.android.cili.util.FormatUtil
import com.vincent.android.cili.util.FormatUtil.countFormat
import com.vincent.android.cili.util.ShareUtil
import com.vincent.android.cili.viewmodel.VideoDetailViewModel
import com.vincent.android.cili.viewmodel.VideoDetailViewModel.VideoOption
import kotlinx.coroutines.launch

class VideoIntroFragment : Fragment(), View.OnClickListener {
    private var videoDetail: VideoDetailEntity? = null
    private lateinit var detailViewModel: VideoDetailViewModel

    private lateinit var fansCount: TextView
    private lateinit var btnLike: Button
    private lateinit var btnCoin: Button
    private lateinit var btnStar: Button
    private lateinit var btnShare: Button
    private lateinit var btnFocus: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videoDetail = arguments?.getParcelable("videoDetail")
        detailViewModel =
            ViewModelProvider(requireActivity())[VideoDetailViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_video_intro, container, false)
        bindViews(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 监听ViewModel数据变化，只刷新按钮高亮
        viewLifecycleOwner.lifecycleScope.launch {
            detailViewModel.videoState.collect { result ->

                val detail = result.data ?: return@collect
                videoDetail = detail
                updateButtonState(detail)
            }
        }
    }

    private fun updateButtonState(detail: VideoDetailEntity) {
        btnLike.isSelected = detail.isLike
        btnLike.text = countFormat(detail.videoInfo?.like ?: 0)

        btnCoin.isSelected = detail.isCoin
        btnCoin.text = countFormat(detail.videoInfo?.coin ?: 0)

        btnStar.isSelected = detail.isFavorite
        btnFocus.text = if (detail.videoInfo?.isFocus == true) "已关注" else "关注"

        btnStar.text = countFormat(detail.videoInfo?.favorite ?: 0)
        btnShare.text = countFormat(detail.videoInfo?.share ?: 0)
        fansCount.text = "粉丝:${countFormat(detail.videoInfo?.fans ?: 0)}"

        if (detail.option == VideoOption.COIN) {
            btnCoin.isSelected = true

            AccountManager.run {
                getUserInfo()?.let {
                    setUserInfo(it.copy(coin = it.coin - 1))
                }
            }

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_like -> videoOption(
                VideoDetailViewModel.VideoOption.LIKE
            )

            R.id.btn_coin -> {

                coinVideo()

            }

            R.id.btn_star -> videoOption(VideoDetailViewModel.VideoOption.STAR)
            R.id.btn_share -> shareVideo()
            R.id.btn_focus -> videoOption(if (videoDetail?.videoInfo?.isFocus != true) VideoDetailViewModel.VideoOption.FOCUS else VideoDetailViewModel.VideoOption.UNFOCUS)
        }
    }

    /**
     * 分享视频
     */
    private fun shareVideo() {
        videoDetail?.let { detail ->
            detail.videoInfo?.let { videoInfo ->
                ShareUtil.shareVideo(requireContext(), videoInfo) {
                    // 分享成功回调，调用分享接口
                    videoOption(VideoDetailViewModel.VideoOption.SHARE)
                }
            }
        }
    }

    private fun videoOption(option: VideoDetailViewModel.VideoOption) {
        detailViewModel.sendOption(option)
    }

    private fun bindViews(view: View) {
        val cover = view.findViewById<ImageView>(R.id.img_cover)
        val title = view.findViewById<TextView>(R.id.txt_title)
        val author = view.findViewById<TextView>(R.id.txt_author)
        fansCount = view.findViewById<TextView>(R.id.txt_fans_count)


        val barrage = view.findViewById<TextView>(R.id.tv_barrage_times)
        val playTime = view.findViewById<TextView>(R.id.tv_play_times)
        val date = view.findViewById<TextView>(R.id.tv_date)

        btnLike = view.findViewById<Button>(R.id.btn_like)
            .apply { setOnClickListener { onClick(it) } }
        btnCoin = view.findViewById<Button>(R.id.btn_coin)
            .apply { setOnClickListener { onClick(it) } }
        btnStar = view.findViewById<Button>(R.id.btn_star)
            .apply { setOnClickListener { onClick(it) } }
        btnShare = view.findViewById<Button>(R.id.btn_share)
            .apply { setOnClickListener { onClick(it) } }
        btnFocus = view.findViewById<Button>(R.id.btn_focus)
            .apply { setOnClickListener { onClick(it) } }
        val recommendList = view.findViewById<RecyclerView>(R.id.recycler_recommend)

        // 设置selector为drawableTop
        btnLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_like, 0, 0)
        btnCoin.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_coin, 0, 0)
        btnStar.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_collect, 0, 0)
        btnShare.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_share, 0, 0)

        videoDetail?.let { detail ->
            // 加载圆形头像
            cover.loadImage(detail.videoInfo?.face ?: "", isCircle = true)
            title.text = detail.videoInfo?.title ?: ""
            author.text = detail.videoInfo?.name ?: ""
            fansCount.text = "粉丝:${countFormat(detail.videoInfo?.fans ?: 0)}"

            barrage.text = countFormat((detail.videoInfo?.reply ?: 0).toLong())
            playTime.text = countFormat((detail.videoInfo?.view ?: 0).toLong())
            date.text =
                FormatUtil.timestampSecondsToDate(detail.videoInfo?.pubdate ?: 0, pattern = "MM-dd")
            updateButtonState(detail)
            // 推荐列表
            recommendList.layoutManager = LinearLayoutManager(context)
            recommendList.adapter = RecommendAdapter(detail.videoList ?: emptyList())
        }
    }

    fun likeVideo() {
        videoOption(VideoOption.LIKE)
    }

    fun coinVideo() {
        AccountManager.getUserInfo()?.let {

            if (it.coin != 0) {
                // 调用Activity的投币动画
                (activity as? VideoDetailActivity)?.playCoinAnimation()
                videoOption(VideoDetailViewModel.VideoOption.COIN)
            } else {
                showToast( "金币不足")
            }
        }
    }

    companion object {
        fun newInstance(videoDetail: VideoDetailEntity?): VideoIntroFragment {
            val fragment = VideoIntroFragment()
            val args = Bundle()
            args.putParcelable("videoDetail", videoDetail)
            fragment.arguments = args
            return fragment
        }
    }
}

class RecommendAdapter(private val list: List<VideoEntity>) :
    RecyclerView.Adapter<RecommendAdapter.VH>() {
    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val cover: ImageView = view.findViewById(R.id.img_recommend_cover)
        val title: TextView = view.findViewById(R.id.txt_recommend_title)
        val playTime: TextView = view.findViewById(R.id.tv_play_times)
        val avatar: ImageView = view.findViewById(R.id.iv_avatar)
        val uperName: TextView = view.findViewById(R.id.tv_uper_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_recommend, parent, false)
        return VH(v)
    }

    override fun getItemCount() = list.size
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]
        holder.cover.loadRoundImage(item.cover ?: "", 16)
        holder.title.text = item.title ?: ""
        holder.uperName.text = item.name
        holder.avatar.loadImage(item.face ?: "", isCircle = true)
        holder.playTime.text = FormatUtil.countFormat(item.view)
        // 添加点击事件
        holder.itemView.setOnClickListener {
            // 跳转到视频详情页
            VideoDetailActivity.start(holder.itemView.context, item)
        }
    }
} 