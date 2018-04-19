package com.yanguang

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * @desc TODO
 *
 * @author WKH
 * @date 2018/4/19 0019
 */
abstract class BaseFragment<T : FragmentExtraDataEntity>(@LayoutRes layoutRes: Int) : Fragment() {

    protected var TAG = javaClass.simpleName

    private val extraDataKey: String = "EXTRA_DATA"

    private var fragmentLayoutRes: Int = layoutRes

    private var viewRoot: View? = null

    fun putExtraData(extraData: T) {
        if (extraData != null) {
            var extraBundle = Bundle()
            extraBundle.putSerializable(extraDataKey, extraData)
            arguments = extraBundle
        }
    }

    protected fun getExtraData(): T? {
        if (arguments != null) {
            return arguments.getSerializable(extraDataKey) as T
        }
        return null
    }

    open fun preLoad() {

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        preLoad()

        viewRoot = inflater.inflate(fragmentLayoutRes, container, false)

        return viewRoot
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }


    protected abstract fun initViews()

    override fun onDestroy() {
        super.onDestroy()
    }
}