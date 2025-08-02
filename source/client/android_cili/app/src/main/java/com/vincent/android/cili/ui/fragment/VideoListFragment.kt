package com.vincent.android.cili.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vincent.android.cili.R
import com.vincent.android.cili.adapter.VideoListAdapter
import com.vincent.android.cili.viewmodel.VideoListViewModel

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.vincent.android.cili.util.GlideApp
import com.vincent.android.cili.util.GridSpacingItemDecoration


@AndroidEntryPoint
class VideoListFragment : BaseFragment() {
    private val videoListViewModel: VideoListViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var adapter: VideoListAdapter? = null
    private var totalSize = 0


    private var type: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getString(ARG_TYPE)

        }
    }

    companion object {
        private const val ARG_TYPE = "type"
        fun newInstance(type: String) = VideoListFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_TYPE, type)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.video_list_fragment, container, false)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setColorSchemeResources(R.color.progress_yellow)
        setUpUI(view)
        observeData()
        videoListViewModel.initCategory(type.orEmpty(), 10)
        return view
    }


    private fun observeData() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 全量数据用于刷新
                videoListViewModel.videoList.observe(this@VideoListFragment.viewLifecycleOwner) {
                    val dataList = it.toList()
                    if (adapter == null) {
                        adapter = VideoListAdapter()
                        recyclerView.adapter = adapter
                    }
                    adapter!!.refreshData(dataList)
                    totalSize = dataList.size
                }
                // 增量数据用于追加
                videoListViewModel.newPage.observe(this@VideoListFragment.viewLifecycleOwner) { newList ->
                    if (adapter != null && newList != null && newList.isNotEmpty()) {
                        adapter!!.addData(newList)
                        totalSize += newList.size
                    }
                }
                videoListViewModel.isRefreshing.observe(this@VideoListFragment.viewLifecycleOwner) { isRefreshing ->
                    swipeRefreshLayout.isRefreshing = isRefreshing
                }
                videoListViewModel.isLoading.observe(this@VideoListFragment.viewLifecycleOwner) { isLoading ->
                    // 可选：显示底部加载中UI
                }
                videoListViewModel.noMoreData.observe(this@VideoListFragment.viewLifecycleOwner) { noMore ->
                    if (noMore) {
                        Toast.makeText(requireContext(), "没有更多数据了", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun setUpUI(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        val layoutManager = GridLayoutManager(this@VideoListFragment.requireContext(), 2)
        recyclerView.layoutManager = layoutManager
        val spacing = resources.getDimensionPixelSize(R.dimen.video_item_horizontal_spacing)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacing, true))
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount
                if (dy > 0 && lastVisibleItem >= totalItemCount - 1) {
                    videoListViewModel.loadMore()
                }
            }
        })

        swipeRefreshLayout.setOnRefreshListener {
            videoListViewModel.refresh(type.orEmpty(), 10)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 清理 RecyclerView 相关图片（如有）
        adapter?.let {
            for (i in 0 until it.itemCount) {
                val holder = recyclerView.findViewHolderForAdapterPosition(i)
                if (holder is VideoListAdapter.ViewHolder) {
                    GlideApp.with(this).clear(holder.itemView)
                }
            }
        }
    }

    fun scrollToTop() {
        if (this::recyclerView.isInitialized) {
            recyclerView.scrollToPosition(0)
        }
    }
}