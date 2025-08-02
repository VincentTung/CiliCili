package com.vincent.android.cili.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vincent.android.cili.R
import com.vincent.android.cili.adapter.VideoListAdapter
import com.vincent.android.cili.view.NoDataView
import com.vincent.android.cili.viewmodel.CollectViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.bumptech.glide.Glide


@AndroidEntryPoint
class CollectFragment : BaseFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var adapter: VideoListAdapter? = null
    private lateinit var collectViewModel: CollectViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_collect, container, false)
        collectViewModel = getActivityScopeVM(CollectViewModel::class.java)!!
        setUpUI(view)
        observeData()
        getData()
        return view
    }


    private fun setUpUI(contentView: View) {
        contentView.let {
            recyclerView = it.findViewById(R.id.rcl_collect)
            val layoutManager = GridLayoutManager(this@CollectFragment.requireContext(), 1)
            recyclerView.layoutManager = layoutManager

            swipeRefreshLayout = it.findViewById<SwipeRefreshLayout>(R.id.swipe)
            swipeRefreshLayout.setColorSchemeResources(R.color.progress_yellow)
            swipeRefreshLayout.setOnRefreshListener {
                collectViewModel.getCollectList()
            }
        }


    }

    private fun observeData() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                collectViewModel.videoList.observe(this@CollectFragment.viewLifecycleOwner) {
                    if (adapter == null) {
                        adapter = VideoListAdapter(false)
                        recyclerView.adapter = adapter
                    }
                    if (it.isEmpty()) {
                        // 无数据时显示图片和提示
                        recyclerView.visibility = View.GONE
                        swipeRefreshLayout.visibility = View.GONE
                        val parent = view as? ViewGroup
                        parent?.removeAllViews()
                        val noDataView = ComposeView(requireContext()).apply {
                            setContent {
                                NoDataView(text = "暂无收藏")
                            }
                        }
                        parent?.addView(noDataView)
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        swipeRefreshLayout.visibility = View.VISIBLE
                        adapter!!.refreshData(it)
                    }
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }

    private fun getData() {
        collectViewModel.getCollectList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter?.let {
            for (i in 0 until it.itemCount) {
                val holder = recyclerView.findViewHolderForAdapterPosition(i)
                if (holder is VideoListAdapter.ViewHolder) {
                    Glide.with(this).clear(holder.itemView)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CollectFragment()
    }
}