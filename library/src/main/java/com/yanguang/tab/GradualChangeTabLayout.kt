package com.dream.base.widget

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.LinearLayout
import com.yanguang.tab.R

/**
 * @desc 滑动渐变的TabLayout 支持3-5个tab
 *
 * @author WKH
 * @date 2018/4/18 0018
 */
class GradualChangeTabLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : LinearLayout(context, attrs, defStyleAttr) {

    private val defaultGradualColor = -0xba3fe6

    private val defaultResourceId = -1

    private val defaultTextSize = 10f

    private val defaultPadding = 5

    private val defaultIconTextSpan = 2

    /**
     * 渐变色
     */
    private var gradualColor: Int = defaultGradualColor

    /**
     * 渐变颜色组，可以单独设置每一个按钮的渐变色，优先级高于gradualColor
     */
    private var gradualColors: IntArray? = null

    /**
     * Tab图标组
     */
    private var gradualIcons: IntArray? = null

    /**
     * Tab文字组
     */
    private var gradualTexts: IntArray? = null

    /**
     * Tab文字大小，默认10，单位sp
     */
    private var tabTextSize: Float = defaultTextSize

    /**
     * Tab的padding值，默认5，单位dp
     */
    private var tabPadding: Int = defaultPadding

    /**
     * Tab文字和icon的间距，默认2，单位dp
     */
    private var tabIconTextSpan = defaultIconTextSpan

    /**
     * 当前tab选中的位置
     */
    private var currentIndexPosition = -1

    /**
     * 绑定的viewPager
     */
    private var currentViewPager: ViewPager? = null

    /**
     * tab的数量
     */
    private var tabItemCount = 0

    private var tabTouchListener: TabTouchListener? = null

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null, 0)

    init {
        orientation = LinearLayout.HORIZONTAL
        val arrays = context.obtainStyledAttributes(attrs, R.styleable.GradualChangeTabLayout)

        (0 until arrays.indexCount)
                .map { arrays.getIndex(it) }
                .forEach {
                    when (it) {

                        R.styleable.GradualChangeTabLayout_tab_color -> {
                            gradualColor = arrays.getColor(it, defaultGradualColor)
                        }

                        R.styleable.GradualChangeTabLayout_tab_icons -> {
                            initIcons(arrays.getResourceId(it, defaultResourceId))
                        }

                        R.styleable.GradualChangeTabLayout_tab_texts -> {
                            initTexts(arrays.getResourceId(it, defaultResourceId))
                        }

                        R.styleable.GradualChangeTabLayout_tab_colors -> {
                            initColors(arrays.getResourceId(it, defaultResourceId))
                        }

                        R.styleable.GradualChangeTabLayout_tab_item_text_size -> {
                            tabTextSize = arrays.getDimension(it, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, defaultTextSize, resources.displayMetrics))
                        }

                        R.styleable.GradualChangeTabLayout_tab_item_padding -> {
                            tabPadding = arrays.getDimension(it, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, defaultPadding.toFloat(), resources.displayMetrics)).toInt()
                        }

                        R.styleable.GradualChangeTabLayout_tab_item_icon_text_span -> {
                            tabIconTextSpan = arrays.getDimension(it, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, defaultIconTextSpan.toFloat(), resources.displayMetrics)).toInt()
                        }
                    }
                }
        arrays.recycle()
        initTabLayout()
    }

    private fun initColors(resourceId: Int) {

        if (resourceId == defaultResourceId) {
            return
        }

        var resList = context.resources.obtainTypedArray(resourceId)
        var len = resList.length()
        gradualColors = IntArray(len)
        for (i in 0 until len)
            gradualColors!![i] = resList.getResourceId(i, defaultResourceId)

        resList.recycle()
    }

    private fun initIcons(resourceId: Int) {

        if (resourceId == defaultResourceId) {
            return
        }

        var resList = context.resources.obtainTypedArray(resourceId)
        var len = resList.length()
        gradualIcons = IntArray(len)
        for (i in 0 until len)
            gradualIcons!![i] = resList.getResourceId(i, defaultResourceId)

        resList.recycle()
    }

    private fun initTexts(resourceId: Int) {

        if (resourceId == defaultResourceId) {
            return
        }

        var resList = context.resources.obtainTypedArray(resourceId)
        var len = resList.length()
        gradualTexts = IntArray(len)
        for (i in 0 until len)
            gradualTexts!![i] = resList.getResourceId(i, defaultResourceId)

        resList.recycle()
    }

    public fun setResource(tabIcons: IntArray, tabTexts: IntArray) {
        gradualIcons = tabIcons
        gradualTexts = tabTexts

        initTabLayout()
    }

    private fun initTabLayout() {

        if (gradualIcons == null || gradualTexts == null || gradualIcons!!.isEmpty() || gradualTexts!!.isEmpty()) {
            throw RuntimeException("GradualChangeTabLayout's resources must not be null!")
        }

        if (gradualIcons!!.size != gradualTexts!!.size) {
            throw RuntimeException("Length of icons and texts must be equal!")
        }

        tabItemCount = gradualIcons!!.size

        var isColorsUseful = false

        if (gradualColors != null && gradualColors!!.size == tabItemCount) {
            isColorsUseful = true
        }

        var params = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT)
        params.weight = 1f

        for (i in 0 until tabItemCount) {
            var tabItem = GradualChangeTabItem(context)
            tabItem.layoutParams = params
            tabItem.setPadding(tabPadding, tabPadding, tabPadding, tabPadding)

            if (isColorsUseful) {
                tabItem.setIconColor(ContextCompat.getColor(context, gradualColors!![i]))
            } else {
                tabItem.setIconColor(gradualColor)
            }

            tabItem.initResource(gradualIcons!![i], gradualTexts!![i], tabTextSize, tabPadding)
            tabItem.setOnClickListener {
                changeTabs(i)
            }

            addView(tabItem)
        }

        changeTabs(0)
    }

    private fun changeTabs(position: Int) {

        if (currentIndexPosition == position) {

            if (tabTouchListener != null) {
                tabTouchListener!!.onTabReSelectedListener(position)
            }

            return
        }

        currentIndexPosition = position

        for (i in 0 until tabItemCount) {
            if (i == position) {
                (getChildAt(i) as GradualChangeTabItem).setIconAlpha(1.0f)

            } else {
                (getChildAt(i) as GradualChangeTabItem).setIconAlpha(0.0f)
            }
        }

        if (currentViewPager != null) {
            currentViewPager!!.setCurrentItem(position, false)
        }

    }

    fun setOnTabTouchListener(listener: TabTouchListener) {
        this.tabTouchListener = listener
    }


    fun bindViewPager(viewPager: ViewPager) {

        currentViewPager = viewPager

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (positionOffset > 0) {
                    val left = getChildAt(position) as GradualChangeTabItem
                    val right = getChildAt(position + 1) as GradualChangeTabItem

                    left.setIconAlpha(1 - positionOffset)
                    right.setIconAlpha(positionOffset)
                }
            }

            override fun onPageSelected(position: Int) {
                currentIndexPosition = position
                if (tabTouchListener != null) {
                    tabTouchListener!!.onTabSelectedListener(currentIndexPosition)
                }
            }
        })
    }

    interface TabTouchListener {

        fun onTabSelectedListener(position: Int)

        fun onTabReSelectedListener(position: Int)
    }
}