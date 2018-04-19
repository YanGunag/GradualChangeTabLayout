package com.yanguang

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.dream.base.widget.GradualChangeTabLayout
import com.dream.yanguang.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var fragments: Array<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var tabFragmentOne = TestFragment()
        tabFragmentOne.putExtraData(FragmentTestExtraData("Tab one"))

        var tabFragmentTwo = TestFragment()
        tabFragmentTwo.putExtraData(FragmentTestExtraData("Tab Two"))

        var tabFragmentThree = TestFragment()
        tabFragmentThree.putExtraData(FragmentTestExtraData("Tab Three"))

        var tabFragmentFour = TestFragment()
        tabFragmentFour.putExtraData(FragmentTestExtraData("Tab Four"))

        fragments = arrayOf(tabFragmentOne, tabFragmentTwo, tabFragmentThree, tabFragmentFour)

        fragmentContainer.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return fragments[position]
            }

            override fun getCount(): Int {
                return fragments.size
            }
        }

        gradualChangeTab.bindViewPager(fragmentContainer)
        gradualChangeTab.setOnTabTouchListener(object : GradualChangeTabLayout.TabTouchListener {
            override fun onTabSelectedListener(position: Int) {
                Log.i("MainActivity", "Tab $position is selected")
            }

            override fun onTabReSelectedListener(position: Int) {
                Log.i("MainActivity", "Tab $position is reselected")
            }
        })
    }

}
