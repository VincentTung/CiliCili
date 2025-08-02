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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vincent.android.cili.R
import com.vincent.android.cili.adapter.VideoListAdapter
import com.vincent.android.cili.databinding.ActivityVideoListBinding
import com.vincent.android.cili.viewmodel.RankListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.fragment.app.viewModels

private const val ARG_TYPE = "type"

@AndroidEntryPoint
class RankListFragment : BaseFragment() {
    private val rankViewModel: RankListViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private var adapter: VideoListAdapter? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var lastVisibleItem = 0
    private var pageNum = 1
    private var totalSize = 0
    private var type: String? = null

    companion object {
        fun newInstance(type: String) = RankListFragment().apply {
            arguments = Bundle().apply { putString(ARG_TYPE, type) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments?.getString(ARG_TYPE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.video_list_fragment, container, false).apply {
            setUpUI(this)
            observeData()
            loadData()
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                rankViewModel.videoList.observe(viewLifecycleOwner) { list ->
                    if (adapter == null) {
                        adapter = VideoListAdapter(false)
                        recyclerView.adapter = adapter
                    }
                    adapter!!.refreshData(list)
                    totalSize = list.size
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }

    private fun setUpUI(root: View) {
        recyclerView = root.findViewById(R.id.recyclerView)
        swipeRefreshLayout = root.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout).apply {
            setColorSchemeResources(R.color.progress_yellow)
            setOnRefreshListener { refreshData() }
        }
        val layoutManager = GridLayoutManager(requireContext(), 1)
        recyclerView.layoutManager = layoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                // 上拉进行数据加载
                if (newState == RecyclerView.SCROLL_STATE_IDLE && layoutManager.findFirstVisibleItemPosition() != 0) {
                    if (lastVisibleItem + 1 == adapter?.itemCount) {
                        pageNum++
                        loadData()
                    }
                }
            }
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                lastVisibleItem = layoutManager.findLastVisibleItemPosition()
            }
        })
    }

    private fun loadData() {
        // 加载当前页数据
        rankViewModel.getRankList(type.orEmpty(), pageNum)
    }

    private fun refreshData() {
        // 下拉刷新，重置页码和总数
        pageNum = 1
        totalSize = 0
        rankViewModel.refreshData(type.orEmpty())
    }
}