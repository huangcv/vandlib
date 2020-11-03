package com.cwand.lib.ktx.widgets

import android.animation.*
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresApi
import kotlin.math.cos
import kotlin.math.sin


open class LoadingView : View {

    private var mHeight: Int = 0
    private var mWidth: Int = 0
    private var isRunning = false

    //外圆环宽度
    private val outerCircleStrokeWidth = 5f

    //内圆半径
    private val innerCircleRadius = 9f

    //中心点坐标
    private val centerPoint: PointF = PointF()

    //外圆内切圆的矩形
    private val outerCircleRect: RectF = RectF()

    //内圆圆心到外圆圆心的实际半径
    private var validRadius = 0f

    private var tempInnerRadius = 0f
        set(value) {
            field = value
            calcEndPoint()
            invalidate()
        }

    var isReverse = false

    var startAngle = 90f
    var endAngle = 450f

    var showOverAnim = true

    private val loadingAnimDuration = 1500L

    private val overAnimDuration = 200L

    private val innerCirclePoint = PointF()

    private var currentAnimator: ObjectAnimator? = null

    private var endAnimator: ObjectAnimator? = null

    private var startAnimSet: AnimatorSet? = null

    private var currentRadian = 0f

    private var spaceXOffset = 0f
    private var spaceYOffset = 0f
        set(value) {
            field = value
            innerCirclePoint.y = field
            invalidate()
        }

    private var rotateDegree = 0f
        set(value) {
            field = value
            calcPoint(field)
            invalidate()
        }

    private val paint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = outerCircleStrokeWidth
        }
    }
    private val centerPointPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.FILL
        }
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        innerInit(context, attrs, defStyleAttr, 0)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        innerInit(context, attrs, defStyleAttr, defStyleRes)
    }

    private fun innerInit(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {

    }

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = measuredWidth
        mHeight = measuredHeight
        outerCircleRect.set(
            outerCircleStrokeWidth / 2f,
            outerCircleStrokeWidth / 2f,
            mWidth.toFloat() - outerCircleStrokeWidth / 2f,
            mHeight.toFloat() - outerCircleStrokeWidth / 2f
        )
        centerPoint.x = outerCircleRect.centerX()
        centerPoint.y = outerCircleRect.centerY()
        validRadius = centerPoint.x - outerCircleStrokeWidth - innerCircleRadius
        spaceXOffset = validRadius / 5f * 3f
        validRadius = spaceXOffset
        innerCirclePoint.x = centerPoint.x
        innerCirclePoint.y = centerPoint.y
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制外圆圆环
        canvas.drawArc(outerCircleRect, 0f, 360f, true, paint)
        //绘制一个中心圆点
        canvas.drawCircle(
            innerCirclePoint.x,
            innerCirclePoint.y,
            innerCircleRadius,
            centerPointPaint
        )
    }

    private fun calcPoint(angle: Float) {
        val radian = Math.toRadians(angle.toDouble()).toFloat()
        val x: Float = centerPoint.x + cos(radian) * validRadius
        val y: Float = centerPoint.y + sin(radian) * validRadius
        innerCirclePoint.x = x
        innerCirclePoint.y = y
    }

    public fun startAnim() {
        currentAnimator = ObjectAnimator.ofFloat(this, "rotateDegree", startAngle, endAngle).apply {
            duration = loadingAnimDuration
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            if (isReverse) {
                repeatMode = ValueAnimator.REVERSE
            }
            addUpdateListener {
                rotateDegree = it.animatedValue as Float
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator?) {
                    super.onAnimationCancel(animation)
                    //预先计算出当前旋转角度下对应的弧度
                    //角度转弧度
                    currentRadian = (Math.PI / 180 * (rotateDegree)).toFloat()
                }

                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    //角度转弧度
                    currentRadian = (Math.PI / 180 * (rotateDegree)).toFloat()
                }
            })
        }
        startAnimSet = AnimatorSet()
        if (showOverAnim) {
            val startTranslateAnim = startTranslateAnim()
            startAnimSet!!.play(startTranslateAnim).before(currentAnimator)
//        set.play(startTranslateAnim)
        } else {
            startAnimSet!!.play(currentAnimator)
            this@LoadingView.isRunning = true
        }
        startAnimSet!!.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        resetAnim()
    }

    private fun startTranslateAnim(): ObjectAnimator {
        //从圆心到(w/2, h)
        return ObjectAnimator.ofFloat(
            this,
            "spaceYOffset",
            innerCirclePoint.y,
            innerCirclePoint.y + validRadius
        ).apply {
            duration = overAnimDuration
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                spaceYOffset = it.animatedValue as Float
            }
            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationStart(animation: Animator?) {
                    super.onAnimationStart(animation)
                    this@LoadingView.isRunning = true
                }

                override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                    super.onAnimationStart(animation, isReverse)
                    this@LoadingView.isRunning = true
                }

                override fun onAnimationCancel(animation: Animator?) {
                    super.onAnimationCancel(animation)
                    spaceYOffset = 0f
                }

                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    spaceYOffset = 0f
                }

            })
        }
    }


    /**
     * 根据不同的半径,算出固定圆心角上的点的坐标
     */
    public fun success() {
        startEndAnimation()
    }

    public fun resetAnim() {
        endAnimator?.cancel()
        startAnimSet?.cancel()
        endAnimator = null
        startAnimSet = null
    }

    public fun switch() {
        resetAnim()
        if (isRunning) {
            success()
        } else {
            startAnim()
        }
    }

    private fun startEndAnimation() {
        if (!showOverAnim) {
            innerCirclePoint.x = centerPoint.x
            innerCirclePoint.y = centerPoint.y
            this@LoadingView.isRunning = false
            invalidate()
            return
        }
        endAnimator = ObjectAnimator.ofFloat(
            this,
            "tempInnerRadius",
            validRadius,
            0f
        ).apply {
            duration = overAnimDuration
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                tempInnerRadius = it.animatedValue as Float
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    this@LoadingView.isRunning = false
                }

                override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                    super.onAnimationEnd(animation, isReverse)
                    this@LoadingView.isRunning = false
                }

                override fun onAnimationCancel(animation: Animator?) {
                    super.onAnimationCancel(animation)
                    this@LoadingView.isRunning = false
                }
            })
        }
        endAnimator!!.start()
    }

    private fun calcEndPoint() {
        val x = centerPoint.x + tempInnerRadius * cos(currentRadian)
        val y = centerPoint.y + tempInnerRadius * sin(currentRadian)
        innerCirclePoint.x = x
        innerCirclePoint.y = y
    }
}