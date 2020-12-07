package com.cwand.lib.ktx.widgets

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

class LightningView : View {

    private var rotateAngle = 0f
        set(value) {
            field = value
            invalidate()
        }

    private val circlePaint: Paint by lazy {
        Paint().apply {
            this.isAntiAlias = true
            strokeWidth = 10f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            color = Color.WHITE
        }
    }

    private val circlePaint2: Paint by lazy {
        Paint().apply {
            this.isAntiAlias = true
            strokeWidth = 10f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            color = Color.WHITE
        }
    }

    private val lightningPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = Color.WHITE
        }
    }

    private val lightningPath = Path()

    private val circleRF = RectF()
    private val lightningRF = RectF()

    private var rotateAnimator: ObjectAnimator? = null

    private lateinit var linearGradient: LinearGradient
    private lateinit var linearGradient1: LinearGradient

    @Volatile
    var isRunning = false


    constructor(context: Context) : this(context, null) {

    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {

    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr) {
        initView(context, attrs, defStyleAttr)
    }

    private fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        circleRF.set(5f, 5f, measuredWidth.toFloat() - 5, measuredHeight.toFloat() - 5)
        //宽=控件宽*0.25
        //高=宽*1.26
        val lightningWidth = measuredWidth * 0.25
        val lightningHeight = lightningWidth * 1.26
        val start = measuredWidth / 2f - lightningWidth / 2f
        val top = start
        val right = start + lightningWidth
        val bottom = top + lightningHeight
        lightningRF.set(start.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        val colors = intArrayOf(Color.parseColor("#00FFFFFF"), Color.WHITE)
        linearGradient = LinearGradient(measuredWidth / 2f,
            measuredHeight.toFloat(),
            measuredWidth / 2f,
            0f,
            colors,
            floatArrayOf(0.0f, 0.6f),
            Shader.TileMode.CLAMP)
        linearGradient1 = LinearGradient(measuredWidth.toFloat(),
            0f,
            measuredWidth.toFloat(),
            measuredHeight.toFloat(),
            colors,
            floatArrayOf(0.0f, 0.6f),
            Shader.TileMode.CLAMP)
        circlePaint.shader = linearGradient
        circlePaint2.shader = linearGradient1
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawLightning(canvas)
        drawCircle(canvas)
    }

    //高=宽*1.26
    //宽=控件宽*0.25
    private fun drawLightning(canvas: Canvas) {
        lightningPath.reset()
        lightningPath.moveTo((lightningRF.left + lightningRF.width() * 0.25).toFloat(),
            lightningRF.top)
        lightningPath.lineTo((lightningRF.left + lightningRF.width() * 0.75).toFloat(),
            lightningRF.top)
        lightningPath.lineTo(((lightningRF.left + lightningRF.width() * 0.95).toFloat() - lightningRF.width() * 0.4).toFloat(),
            (lightningRF.top + lightningRF.height() * 0.4).toFloat())
        lightningPath.lineTo(lightningRF.right,
            (lightningRF.top + lightningRF.height() * 0.4).toFloat())
        lightningPath.lineTo((lightningRF.left + lightningRF.width() * 0.33).toFloat(),
            lightningRF.bottom)
        lightningPath.lineTo((lightningRF.left + lightningRF.width() * 0.38).toFloat(),
            (lightningRF.bottom - lightningRF.height() * 0.33).toFloat())
        lightningPath.lineTo(lightningRF.left,
            (lightningRF.bottom - lightningRF.height() * 0.33).toFloat())
        lightningPath.close()
        canvas.drawPath(lightningPath, lightningPaint)
    }

    private fun drawCircle(canvas: Canvas) {
        canvas.save()
        canvas.rotate(rotateAngle, measuredWidth / 2f, measuredHeight / 2f)
        canvas.drawArc(circleRF, 90f, 180f, false, circlePaint)
        canvas.drawArc(circleRF, 270f, 180f, false, circlePaint2)
        canvas.restore()
    }

    fun startRotate(duration: Long) {
        isRunning = true
        rotateAnimator?.cancel()
        rotateAnimator = ObjectAnimator.ofFloat(this, "rotateAngle", 0f, 360f)
        rotateAnimator?.duration = duration
        rotateAnimator?.interpolator = LinearInterpolator()
        rotateAnimator?.repeatCount = ValueAnimator.INFINITE
        rotateAnimator?.start()
    }

    fun stopRotate() {
        if (isRunning) {
            rotateAnimator?.cancel()
        }
        isRunning = false
    }

}