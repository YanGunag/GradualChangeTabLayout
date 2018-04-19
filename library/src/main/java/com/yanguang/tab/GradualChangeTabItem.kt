package com.dream.base.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.os.Looper
import android.os.Parcelable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.dream.base.utils.CvsUtil
import com.dream.base.utils.DisplayUtil
import com.yanguang.tab.R


/**
 * @desc Tab滑动渐变
 *
 * @author WKH
 * @date 2018/4/18 0018
 */
class GradualChangeTabItem(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null, 0)

    private val INSTANCE_STATE = "instance_state"

    private val STATE_ALPHA = "state_alpha"

    private var mBitmap: Bitmap? = null

    private var mCanvas: Canvas? = null

    private var mPaint: Paint? = null
    /**
     * 颜色
     */
    private var mColor = -0xba3fe6
    /**
     * 透明度 0.0-1.0
     */
    private var mAlpha = 0f
    /**
     * 图标
     */
    private var mIconBitmap: Bitmap? = null
    /**
     * 限制绘制icon的范围
     */
    private var mIconRect: Rect? = null
    /**
     * icon底部文本
     */
    private var mText: String = "Tab"

    /**
     * icon和文字的间隔高度，默认2dp
     */
    private var iconTextSpan: Int = DisplayUtil.dip2px(context, 3f)

    private var mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, resources.displayMetrics).toInt()

    private lateinit var mTextPaint: Paint

    private val mTextBound = Rect()

    init {
        val arrays = context.obtainStyledAttributes(attrs, R.styleable.GradualChangeTabItem)

        (0 until arrays.indexCount)
                .map { arrays.getIndex(it) }
                .forEach {
                    when (it) {

                        R.styleable.GradualChangeTabItem_tab_icon -> {
                            var getDrawable = arrays.getDrawable(it)
                            if (getDrawable is VectorDrawable) {
                                mIconBitmap = CvsUtil.toBitmap(getDrawable)
                            } else if (getDrawable is BitmapDrawable) {
                                mIconBitmap = getDrawable.bitmap
                            }
                        }
                        R.styleable.GradualChangeTabItem_tab_color -> mColor = arrays.getColor(it, 0x45C01A)
                        R.styleable.GradualChangeTabItem_tab_text -> mText = arrays.getString(it)
                        R.styleable.GradualChangeTabItem_tab_text_size -> mTextSize = arrays.getDimension(it, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10f, resources.displayMetrics)).toInt()
                    }
                }

        arrays.recycle()

        if (mIconBitmap != null) {
            initTextPaint()
        }
    }

    private fun initTextPaint() {
        mTextPaint = Paint()
        mTextPaint.textSize = mTextSize.toFloat()
        mTextPaint.isAntiAlias = true
        mTextPaint.color = -0xaaaaab
        // 得到text绘制范围
        mTextPaint.getTextBounds(mText, 0, mText!!.length, mTextBound)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val bitmapSize = Math.min(measuredWidth - paddingLeft - paddingRight, measuredHeight - paddingTop - paddingBottom - mTextBound.height() - iconTextSpan)
        val left = measuredWidth / 2 - bitmapSize / 2
        val top = (measuredHeight - mTextBound.height() - iconTextSpan) / 2 - bitmapSize / 2

        // 设置icon的绘制范围
        mIconRect = Rect(left, top, left + bitmapSize, top + bitmapSize)

    }

    override fun onDraw(canvas: Canvas) {
        val alpha = Math.ceil((255 * mAlpha).toDouble()).toInt()
        canvas.drawBitmap(mIconBitmap!!, null, mIconRect!!, null)
        setupTargetBitmap(alpha)
        drawSourceText(canvas, alpha)
        drawTargetText(canvas, alpha)
        canvas.drawBitmap(mBitmap!!, 0f, 0f, null)
    }

    private fun setupTargetBitmap(alpha: Int) {
        mBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap!!)
        mPaint = Paint()
        mPaint!!.color = mColor
        mPaint!!.isAntiAlias = true
        mPaint!!.isDither = true
        mPaint!!.alpha = alpha
        mCanvas!!.drawRect(mIconRect!!, mPaint!!)
        mPaint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        mPaint!!.alpha = 255
        mCanvas!!.drawBitmap(mIconBitmap!!, null, mIconRect!!, mPaint)
    }

    private fun drawSourceText(canvas: Canvas, alpha: Int) {
        mTextPaint.textSize = mTextSize.toFloat()
        mTextPaint.color = -0xcccccd
        mTextPaint.alpha = 255 - alpha
        canvas.drawText(mText!!, (mIconRect!!.left + mIconRect!!.width() / 2 - mTextBound.width() / 2).toFloat(), (mIconRect!!.bottom + mTextBound.height()).toFloat() + iconTextSpan, mTextPaint)
    }

    private fun drawTargetText(canvas: Canvas, alpha: Int) {
        mTextPaint.color = mColor
        mTextPaint.alpha = alpha
        canvas.drawText(mText!!, (mIconRect!!.left + mIconRect!!.width() / 2 - mTextBound.width() / 2).toFloat(), (mIconRect!!.bottom + mTextBound.height()).toFloat() + iconTextSpan, mTextPaint)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState())
        bundle.putFloat(STATE_ALPHA, mAlpha)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            mAlpha = state.getFloat(STATE_ALPHA)
            super.onRestoreInstanceState(state.getParcelable(INSTANCE_STATE))
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    fun setIconAlpha(alpha: Float) {
        this.mAlpha = alpha
        invalidateView()
    }

    private fun invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate()
        } else {
            postInvalidate()
        }
    }

    fun setIconColor(color: Int) {
        mColor = color
    }

    fun setIcon(resId: Int) {
        mIconBitmap = getBitmapFromRes(resId)
        if (mIconRect != null) {
            invalidateView()
        }
    }

    fun setIcon(iconBitmap: Bitmap) {
        this.mIconBitmap = iconBitmap
        if (mIconRect != null) {
            invalidateView()
        }
    }

    fun initResource(iconResource: Int, textResource: Int, textSize: Float, span: Int) {

        mIconBitmap = getBitmapFromRes(iconResource)

        mText = context.resources.getString(textResource)
        mTextSize = textSize.toInt()
        iconTextSpan = span

        initTextPaint()
        invalidateView()
    }

    private fun getBitmapFromRes(imageRes: Int): Bitmap? {
        var tempBitmap: Bitmap? = null
        val getDrawable = ContextCompat.getDrawable(context, imageRes)
        if (getDrawable is VectorDrawable) {
            tempBitmap = CvsUtil.toBitmap(getDrawable)
        } else if (getDrawable is BitmapDrawable) {
            tempBitmap = getDrawable.bitmap
        }
        return tempBitmap
    }
}