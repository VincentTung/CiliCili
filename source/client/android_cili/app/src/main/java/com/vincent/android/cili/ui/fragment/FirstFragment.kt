package com.vincent.android.cili.ui.fragment

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.vincent.android.cili.R
import com.vincent.android.cili.entity.Type
import com.vincent.android.cili.ui.theme.activeColor
import com.vincent.android.cili.ui.theme.indicatorTextSize
import com.vincent.android.cili.ui.theme.normalColor
import com.vincent.android.cili.viewmodel.RecommendViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FirstFragment : BaseFragment() {
    companion object {
        @JvmStatic
        fun newInstance() =
            FirstFragment()
    }

    private val recommendViewModel: RecommendViewModel by viewModels()

    private var tabTypes = arrayOf("推荐") // 默认只有推荐，后续动态加载
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayoutMediator: TabLayoutMediator
    private var tabAdapter: TabAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_first2, container, false)
        setUpUI(view)
        observeCategories()
        // 初始化推荐数据，这会同时获取分类列表
        recommendViewModel.initRecommend(10)
        return view
    }

    private fun setUpUI(view: View?) {
        view?.let {
            tabLayout = view.findViewById(R.id.tablayout)
            viewPager2 = view.findViewById(R.id.viewpager)
            viewPager2.offscreenPageLimit = 5
            with(viewPager2) {
                registerOnPageChangeCallback(ChangeCallback())
                tabAdapter = TabAdapter(tabTypes, childFragmentManager, lifecycle)
                adapter = tabAdapter
            }
            tabLayoutMediator = TabLayoutMediator(
                tabLayout,
                viewPager2,
                TabStrategy(this@FirstFragment.requireContext(), tabTypes)
            )
            tabLayoutMediator.attach()

            // 添加Tab双击监听
            addTabDoubleClickListener()
        }
    }

    private fun observeCategories() {
        recommendViewModel.categoryList.observe(viewLifecycleOwner) { categories ->
            Log.d("FirstFragment", "监听到分类列表变化: ${categories.map { it.name }}")
            if (categories.isNotEmpty()) {
                updateTabsWithCategories(categories)
            } else {
                Log.w("FirstFragment", "分类列表为空")
            }
        }
    }

    private fun updateTabsWithCategories(categories: List<Type>) {
        // 构建新的分类数组，确保"推荐"在第一位
        val newTabTypes = mutableListOf("推荐")
        newTabTypes.addAll(categories.map { it.name })
        val newTabTypesArray = newTabTypes.toTypedArray()
        
        // 检查是否需要更新Tab
        if (!newTabTypesArray.contentEquals(tabTypes)) {
            tabTypes = newTabTypesArray
            
            // 重新创建TabAdapter
            tabAdapter = TabAdapter(tabTypes, childFragmentManager, lifecycle)
            viewPager2.adapter = tabAdapter
            
            // 重新设置TabLayout
            tabLayoutMediator.detach()
            tabLayoutMediator = TabLayoutMediator(
                tabLayout,
                viewPager2,
                TabStrategy(requireContext(), tabTypes)
            )
            tabLayoutMediator.attach()
            
            // 重新添加Tab双击监听
            addTabDoubleClickListener()

        } else {
            Log.d("FirstFragment", "Tab类型没有变化，跳过更新")
        }
    }

    // 添加Tab双击监听
    private fun addTabDoubleClickListener() {
        repeat(tabLayout.tabCount) { i ->
            val tab = tabLayout.getTabAt(i)
            val tabView = tab?.customView
            tabView?.let { view ->
                var lastClickTime = 0L
                view.setOnClickListener {
                    val now = System.currentTimeMillis()
                    if (now - lastClickTime < 400) {
                        // 双击，滚动到顶部
                        scrollCurrentListToTop()
                    }
                    lastClickTime = now
                    // 让Tab正常切换
                    tab.select()
                }
            }
        }
    }

    // 让当前ViewPager2的Fragment滚动到顶部
    private fun scrollCurrentListToTop() {
        val current = viewPager2.currentItem
        val fragment = childFragmentManager.findFragmentByTag("f$current")
        if (fragment is VideoListFragment) {
            fragment.scrollToTop()
        } else if (fragment is RecommendFragment) {
            fragment.scrollToTop()
        }
    }

    class ChangeCallback : ViewPager2.OnPageChangeCallback() {

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
        }
    }

    class TabAdapter(
        private val types: Array<String>,
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun getItemCount(): Int {
            return types.size
        }

        override fun createFragment(position: Int): Fragment {
            val categoryName = types[position]
            return if (categoryName == "推荐") {
                RecommendFragment.newInstance()
            } else {
                // 其他分类使用VideoListFragment，传入分类名去加载对应数据
                VideoListFragment.newInstance(categoryName)
            }
        }

    }

    class TabStrategy(private val context: Context, private val types: Array<String>) :
        TabLayoutMediator.TabConfigurationStrategy {
        val typeface = ResourcesCompat.getFont(context, R.font.ali_45r)
        override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {

            val tabView = TextView(context)
            val states = arrayOfNulls<IntArray>(2)
            states[0] = intArrayOf(android.R.attr.state_selected)
            states[1] = intArrayOf()

            val colors = intArrayOf(activeColor, normalColor)
            val colorStateList = ColorStateList(states, colors)
            tabView.text = types[position]
            tabView.textSize = indicatorTextSize
            tabView.setTextColor(colorStateList)

            tabView.typeface = typeface
            tab.customView = tabView
        }

    }
}

