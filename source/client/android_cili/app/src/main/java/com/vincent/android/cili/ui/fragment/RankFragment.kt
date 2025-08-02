package com.vincent.android.cili.ui.fragment

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.vincent.android.cili.R
import com.vincent.android.cili.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RankFragment : BaseFragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2

    private lateinit var tabLayoutMediator: TabLayoutMediator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_first2, container, false)
        setUpUI(view)
        return view
    }

    private fun setUpUI(view: View?) {
        view?.let {
            tabLayout = view.findViewById(R.id.tablayout)
            viewPager2 = view.findViewById(R.id.viewpager)
            viewPager2.offscreenPageLimit = 3
            tabLayout.tabGravity = TabLayout.GRAVITY_CENTER
            tabLayout.isTabIndicatorFullWidth = false
            tabLayout.tabMode = TabLayout.MODE_FIXED
            with(viewPager2) {
                registerOnPageChangeCallback(ChangeCallback())
                adapter = TabAdapter(childFragmentManager, lifecycle)
            }
            tabLayoutMediator = TabLayoutMediator(
                tabLayout,
                viewPager2,
                TabStrategy(this@RankFragment.requireContext())
            )
            tabLayoutMediator.attach()
        }

    }

    class ChangeCallback : ViewPager2.OnPageChangeCallback() {

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
        }
    }

    class TabAdapter(
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun getItemCount(): Int {
            return mTypes.size
        }

        override fun createFragment(position: Int): Fragment {
            return RankListFragment.newInstance(mValues[position])
        }

    }

    class TabStrategy(private val context: Context) :
        TabLayoutMediator.TabConfigurationStrategy {
        val typeface = ResourcesCompat.getFont(context, R.font.ali_45r)
        override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {

            val tabView = TextView(context)
            val states = arrayOfNulls<IntArray>(2)
            states[0] = intArrayOf(android.R.attr.state_selected)
            states[1] = intArrayOf()

            val colors = intArrayOf(activeColor, normalColor)
            val colorStateList = ColorStateList(states, colors)
            tabView.run {
                text = mTypes[position]

                textSize = indicatorTextSize
                setTextColor(colorStateList)
                typeface = typeface
            }

            tab.customView = tabView
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RankFragment()

        val mTypes = arrayOf("最热", "最新", "收藏")
        val mValues = arrayOf("hot", "new", "collect")
    }
}