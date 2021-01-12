package com.cwand.lib.ktx.widgets.kline

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.util.AttributeSet
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import androidx.annotation.RequiresApi
import com.cwand.lib.ktx.extensions.logD
import com.cwand.lib.ktx.extensions.toDp
import java.util.jar.Attributes

/**
 * @author : chunwei
 * @date : 2020/12/28
 * @description :
 *
 */
class KLineView : View {

    private val linePaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.GREEN
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 5.toDp
        }
    }

    private val bgPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.GRAY
        }
    }

    private val pointPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.RED
            style = Paint.Style.FILL
        }
    }

    private lateinit var sh: SurfaceHolder

    var isRunDrawing = true
        private set

    private var svHeight = 0
    private var svWidth = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributes: AttributeSet?) : this(context, attributes, 0)

    constructor(context: Context, attributes: AttributeSet?, defStyleAttr: Int) : super(context,
        attributes,
        0) {
        viewInit(context, attributes, defStyleAttr, 0)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attributes: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int,
    ) : super(context, attributes, defStyleAttr, defStyleRes) {
        viewInit(context, attributes, defStyleAttr, 0)
    }

    private fun viewInit(
        context: Context,
        attributes: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int,
    ) {
        initPaint()
    }

    private fun initPaint() {
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        svWidth = measuredWidth
        svHeight = measuredHeight
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制网格

        //绘制标尺
    }

}