package com.dream.base.utils

import android.content.Context
import android.util.TypedValue

/**
 * @desc TODO
 *
 * @author WKH
 * @date 2018/4/18 0018
 */
object DisplayUtil {

    private val mTmpValue = TypedValue()
    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    fun dip2px(context: Context, dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    fun px2sp(context: Context, pxValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    /**
     * 获取xml文件中定义的大小的数值
     */
    fun getXmlDef(context: Context, id: Int, defValue: Int): Int {
        synchronized(mTmpValue) {
            val value = mTmpValue
            context.resources.getValue(id, value, true)
            return TypedValue.complexToFloat(value.data).toInt()
        }
    }
}