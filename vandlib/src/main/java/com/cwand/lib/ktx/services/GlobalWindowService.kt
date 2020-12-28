package com.cwand.lib.ktx.services

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.os.EnvironmentCompat
import com.cwand.lib.ktx.R
import com.cwand.lib.ktx.extensions.*
import com.cwand.lib.ktx.utils.ToastUtils
import kotlinx.coroutines.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer


/**
 * @author : chunwei
 * @date : 2020/12/28
 * @description :全局悬浮窗服务
 *
 */
class GlobalWindowService : Service() {

    private var windowViewCreator: WindowViewCreator? = null
    private var windowView: View? = null
    private var weltAnimator: ValueAnimator? = null
    private var autoWelt = true
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var mediaProjectionManager: MediaProjectionManager? = null
    private var mediaProjection: MediaProjection? = null
    private var displayMetrics: DisplayMetrics? = null

    companion object {
        @JvmStatic
        var resultCode = 100

        @JvmStatic
        var intentData: Intent? = null
    }

    private val defaultWindowViewCreator by lazy {
        DefWVCreator(application)
    }

    override fun onCreate() {
        super.onCreate()
        createFloatView()
        createVirtualDisplay()
    }

    @SuppressLint("WrongConstant")
    private fun createVirtualDisplay() {
        mediaProjectionManager = application.mediaProjectionManager
        displayMetrics = DisplayMetrics()
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        imageReader = ImageReader.newInstance(application.screenWidthPixels,
            application.screenHeightPixels,
            0x1,
            2)
    }

    fun setWindowViewCreator(windowViewCreator: WindowViewCreator) {
        this.windowViewCreator = windowViewCreator
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createFloatView() {
        val lp = WindowManager.LayoutParams()
        lp.apply {
            windowViewCreator?.initWindowLayoutParams(lp)
                ?: defaultWindowViewCreator.initWindowLayoutParams(
                    lp)
        }
        val view: View =
            this.windowViewCreator?.createView(application) ?: defaultWindowViewCreator.createView(
                application)
        val windowManager = application.windowManager
        view.isClickable = true
        windowManager?.addView(view, lp)
        view.measure(View.MeasureSpec.makeMeasureSpec(0,
            View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
            .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        view.onClick {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    view.visibility = View.INVISIBLE
                }
                delay(100)
                withContext(Dispatchers.IO) {
                    startVirtual()
                }
                delay(200)
                withContext(Dispatchers.IO) {
                    startCapture()
                }
                delay(200)
                withContext(Dispatchers.Main) {
                    view.visibility = View.VISIBLE
                }
            }
        }
        view.setOnTouchListener { _, event ->
            var downX = 0f
            var downY = 0f
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    weltAnimator?.let {
                        if (it.isRunning) {
                            it.cancel()
                        }
                        weltAnimator = null
                    }
                    downX = event.rawX
                    downY = event.rawY
                    return@setOnTouchListener false
                }
                MotionEvent.ACTION_MOVE -> {
                    lp.x = event.rawX.toInt() - view.measuredWidth / 2
                    lp.y =
                        event.rawY.toInt() - view.measuredHeight / 2 - application.resources.statusBarHeight
                    windowManager?.updateViewLayout(view, lp)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    //自动贴边
                    if (autoWelt) {
                        animateWelt(event.rawX.toInt(), windowManager, view, lp, 500L)
                    }
                }
                else -> {
                }
            }
            false
        }
    }

    private fun animateWelt(
        endX: Int,
        windowManager: WindowManager?,
        view: View,
        lp: WindowManager.LayoutParams,
        duration: Long,
    ) {
        weltAnimator?.let {
            if (it.isRunning) {
                it.cancel()
            }
            weltAnimator = null
        }
        var startValue = endX - view.measuredWidth / 2
        var endValue = 0
        if (endX > application.screenWidthPixels / 2) {
            startValue = endX + view.measuredWidth / 2
            endValue = application.screenWidthPixels
        }
        val animator = ValueAnimator.ofInt(startValue, endValue)
        animator.duration = duration
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener {
            val value = it.animatedValue as Int
            lp.x = value
            windowManager?.updateViewLayout(view, lp)
        }
        animator.start()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDestroy() {
        super.onDestroy()
        windowView?.let {
            windowManager?.removeViewImmediate(it)
        }
        intentData = null
        mediaProjection?.stop()
        mediaProjection = null
        stopVirtual()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setupMediaProjection() {
        if (intentData != null) {
            mediaProjection = mediaProjectionManager?.getMediaProjection(resultCode, intentData!!)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startVirtual() {
        if (mediaProjection == null) {
            setupMediaProjection()
            startVirtualDisplay()
        } else {
            startVirtualDisplay()
        }
    }

    private fun stopVirtual() {
        virtualDisplay?.release()
        virtualDisplay = null
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startVirtualDisplay() {
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "screen-mirror",
            application.screenWidthPixels,
            application.screenHeightPixels,
            displayMetrics?.densityDpi ?: 0,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface,
            null,
            null
        )
    }

    private fun startCapture() {
        imageReader?.let {
            val image = it.acquireLatestImage()
            image?.let {
                val width: Int = image.width
                val height: Int = image.height
                val planes: Array<Image.Plane> = image.planes
                val buffer: ByteBuffer = planes[0].buffer
                val pixelStride: Int = planes[0].pixelStride
                val rowStride: Int = planes[0].rowStride
                val rowPadding = rowStride - pixelStride * width
                var bitmap: Bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride,
                    height,
                    Bitmap.Config.ARGB_8888)
                bitmap.copyPixelsFromBuffer(buffer)
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height)
                image.close()
                if (bitmap != null) {
                    try {
                        val fileImage =
                            File(Environment.getExternalStorageDirectory().path + System.currentTimeMillis() + ".png")
                        if (!fileImage.exists()) {
                            fileImage.createNewFile()
                        }
                        val out = FileOutputStream(fileImage)
                        if (out != null) {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                            out.flush()
                            out.close()
                            val media = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                            val contentUri: Uri = Uri.fromFile(fileImage)
                            media.data = contentUri
                            this.sendBroadcast(media)
                        }
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    interface WindowViewCreator {
        fun initWindowLayoutParams(lp: WindowManager.LayoutParams): WindowManager.LayoutParams
        fun createView(context: Context): View
    }

    internal class DefWVCreator(private val context: Context) : WindowViewCreator {

        override fun initWindowLayoutParams(lp: WindowManager.LayoutParams): WindowManager.LayoutParams {
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                lp.type = WindowManager.LayoutParams.TYPE_PHONE
            }
            lp.format = PixelFormat.RGBA_8888
            lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            lp.gravity = Gravity.START or Gravity.TOP
            lp.x = context.screenWidthPixels
            lp.y = context.screenHeightPixels - context.resources.statusBarHeight - 150.toDp.toInt()
            return lp
        }

        override fun createView(context: Context): View {
            val icon = ImageView(context)
            icon.setBackgroundResource(R.drawable.ic_capture)
            return icon
        }
    }

}