package com.yanguang

import com.dream.yanguang.R
import kotlinx.android.synthetic.main.fragment_test.*

/**
 * @desc TODO
 *
 * @author WKH
 * @date 2018/4/19 0019
 */
class TestFragment : BaseFragment<FragmentTestExtraData>(R.layout.fragment_test) {

    var content: String? = null

    override fun initViews() {
        descContent.text = content + ""
    }

    override fun preLoad() {
        var extraData = getExtraData()
        if (extraData != null) {
            content = extraData.content
        }
    }

}