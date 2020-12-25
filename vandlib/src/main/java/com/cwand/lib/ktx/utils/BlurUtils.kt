package com.cwand.lib.ktx.utils

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.annotation.IntRange
import com.vvme.android.natives.NativeBlur

/**
 * @author : chunwei
 * @date : 2020/12/24
 * @description :
 *
 */
class BlurUtils private constructor() {

    companion object {

        //图片缩放比例
        private val BITMAP_SCALE = 0.4f

        fun blurPixelsByNative(img: IntArray?, width: Int, height: Int, radius: Int) {
            NativeBlur.blurPixelsByNative(img, width, height, radius)
        }

        /**
         * Blur Image By Bitmap
         *
         * @param bitmap Img Bitmap
         * @param radius Blur radius
         */
        fun blurBitmapByNative(bitmap: Bitmap?, radius: Int) {
            NativeBlur.blurBitmapByNative(bitmap, radius)
        }

        fun blur(
            context: Context?,
            origin: Bitmap?,
            radius: Int,
            canReuseInBitmap: Boolean,
        ): Bitmap? {
            var radius = radius
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (radius >= 25) {
                    radius = 25
                }
                blurByRenderScript(context, origin, radius, canReuseInBitmap)
            } else {
                blurByJava(origin, radius, canReuseInBitmap)
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        fun blurByRenderScript(
            context: Context?,
            origin: Bitmap?,
            @IntRange(from = 0L, to = 25L)
            radius: Int,
            canReuseInBitmap: Boolean,
        ): Bitmap? {
            var outputBitmap: Bitmap? = null
            try {
                val rs = RenderScript.create(context)
                outputBitmap = createUseBitmap(origin, canReuseInBitmap)
                val theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
                //native层分配内存空间
                val tmpIn = Allocation.createFromBitmap(rs, origin)
                val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
                //设置blur的半径然后进行blur
                theIntrinsic.setRadius(radius.toFloat())
                theIntrinsic.setInput(tmpIn)
                theIntrinsic.forEach(tmpOut)
                //拷贝blur后的数据到java缓冲区中
                tmpOut.copyTo(outputBitmap)
                //销毁Renderscript
                rs.destroy()
            } catch (e: Exception) {
                e.printStackTrace()
                outputBitmap = origin
            }
            return outputBitmap
        }

        fun blurByJava(origin: Bitmap?, radius: Int, canReuseInBitmap: Boolean): Bitmap? {
            if (radius <= 1) {
                return origin
            }
            val readyBlurBitmap = createUseBitmap(origin, canReuseInBitmap)
            val w = readyBlurBitmap.width
            val h = readyBlurBitmap.height
            val pix = IntArray(w * h)
            // get array
            readyBlurBitmap.getPixels(pix, 0, w, 0, 0, w, h)

            // run Blur
            val wm = w - 1
            val hm = h - 1
            val wh = w * h
            val div = radius + radius + 1
            val r = IntArray(wh)
            val g = IntArray(wh)
            val b = IntArray(wh)
            var rSum: Int
            var gSum: Int
            var bSum: Int
            var x: Int
            var y: Int
            var i: Int
            var p: Int
            var yp: Int
            var yi: Int
            var yw: Int
            val vMin = IntArray(Math.max(w, h))
            var divSum = div + 1 shr 1
            divSum *= divSum
            val dv = IntArray(256 * divSum)
            i = 0
            while (i < 256 * divSum) {
                dv[i] = (i / divSum)
                i++
            }
            yi = 0
            yw = yi
            val stack = Array(div) { IntArray(3) }
            var stackPointer: Int
            var stackStart: Int
            var sir: IntArray
            var rbs: Int
            val r1 = radius + 1
            var routSum: Int
            var goutSum: Int
            var boutSum: Int
            var rinSum: Int
            var ginSum: Int
            var binSum: Int
            y = 0
            while (y < h) {
                bSum = 0
                gSum = bSum
                rSum = gSum
                boutSum = rSum
                goutSum = boutSum
                routSum = goutSum
                binSum = routSum
                ginSum = binSum
                rinSum = ginSum
                i = -radius
                while (i <= radius) {
                    p = pix[yi + Math.min(wm, Math.max(i, 0))]
                    sir = stack[i + radius]
                    sir[0] = p and 0xff0000 shr 16
                    sir[1] = p and 0x00ff00 shr 8
                    sir[2] = p and 0x0000ff
                    rbs = r1 - Math.abs(i)
                    rSum += sir[0] * rbs
                    gSum += sir[1] * rbs
                    bSum += sir[2] * rbs
                    if (i > 0) {
                        rinSum += sir[0]
                        ginSum += sir[1]
                        binSum += sir[2]
                    } else {
                        routSum += sir[0]
                        goutSum += sir[1]
                        boutSum += sir[2]
                    }
                    i++
                }
                stackPointer = radius
                x = 0
                while (x < w) {
                    r[yi] = dv[rSum]
                    g[yi] = dv[gSum]
                    b[yi] = dv[bSum]
                    rSum -= routSum
                    gSum -= goutSum
                    bSum -= boutSum
                    stackStart = stackPointer - radius + div
                    sir = stack[stackStart % div]
                    routSum -= sir[0]
                    goutSum -= sir[1]
                    boutSum -= sir[2]
                    if (y == 0) {
                        vMin[x] = Math.min(x + radius + 1, wm)
                    }
                    p = pix[yw + vMin[x]]
                    sir[0] = p and 0xff0000 shr 16
                    sir[1] = p and 0x00ff00 shr 8
                    sir[2] = p and 0x0000ff
                    rinSum += sir[0]
                    ginSum += sir[1]
                    binSum += sir[2]
                    rSum += rinSum
                    gSum += ginSum
                    bSum += binSum
                    stackPointer = (stackPointer + 1) % div
                    sir = stack[stackPointer % div]
                    routSum += sir[0]
                    goutSum += sir[1]
                    boutSum += sir[2]
                    rinSum -= sir[0]
                    ginSum -= sir[1]
                    binSum -= sir[2]
                    yi++
                    x++
                }
                yw += w
                y++
            }
            x = 0
            while (x < w) {
                bSum = 0
                gSum = bSum
                rSum = gSum
                boutSum = rSum
                goutSum = boutSum
                routSum = goutSum
                binSum = routSum
                ginSum = binSum
                rinSum = ginSum
                yp = -radius * w
                i = -radius
                while (i <= radius) {
                    yi = Math.max(0, yp) + x
                    sir = stack[i + radius]
                    sir[0] = r[yi].toInt()
                    sir[1] = g[yi].toInt()
                    sir[2] = b[yi].toInt()
                    rbs = r1 - Math.abs(i)
                    rSum += r[yi] * rbs
                    gSum += g[yi] * rbs
                    bSum += b[yi] * rbs
                    if (i > 0) {
                        rinSum += sir[0]
                        ginSum += sir[1]
                        binSum += sir[2]
                    } else {
                        routSum += sir[0]
                        goutSum += sir[1]
                        boutSum += sir[2]
                    }
                    if (i < hm) {
                        yp += w
                    }
                    i++
                }
                yi = x
                stackPointer = radius
                y = 0
                while (y < h) {

                    // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                    pix[yi] =
                        -0x1000000 and pix[yi] or (dv[rSum] shl 16) or (dv[gSum] shl 8) or dv[bSum]
                            .toInt()
                    rSum -= routSum
                    gSum -= goutSum
                    bSum -= boutSum
                    stackStart = stackPointer - radius + div
                    sir = stack[stackStart % div]
                    routSum -= sir[0]
                    goutSum -= sir[1]
                    boutSum -= sir[2]
                    if (x == 0) {
                        vMin[y] = Math.min(y + r1, hm) * w
                    }
                    p = x + vMin[y]
                    sir[0] = r[p].toInt()
                    sir[1] = g[p].toInt()
                    sir[2] = b[p].toInt()
                    rinSum += sir[0]
                    ginSum += sir[1]
                    binSum += sir[2]
                    rSum += rinSum
                    gSum += ginSum
                    bSum += binSum
                    stackPointer = (stackPointer + 1) % div
                    sir = stack[stackPointer]
                    routSum += sir[0]
                    goutSum += sir[1]
                    boutSum += sir[2]
                    rinSum -= sir[0]
                    ginSum -= sir[1]
                    binSum -= sir[2]
                    yi += w
                    y++
                }
                x++
            }
            readyBlurBitmap.setPixels(pix, 0, w, 0, 0, w, h)
            return readyBlurBitmap
        }

        private fun createUseBitmap(original: Bitmap?, canReuseInBitmap: Boolean): Bitmap {
            if (original == null) {
                throw NullPointerException("Blur bitmap original isn't null")
            }
            val config = original.config
            if (config != Bitmap.Config.ARGB_8888 && config != Bitmap.Config.RGB_565) {
                throw RuntimeException("Blur bitmap only supported Bitmap.Config.ARGB_8888 and Bitmap.Config.RGB_565.")
            }
            val rBitmap: Bitmap
            rBitmap = if (canReuseInBitmap) {
                original
            } else {
                original.copy(config, true)
            }
            return rBitmap
        }


        fun blurBitmap(context: Context?, image: Bitmap, blurRadius: Float): Bitmap? {
            // 计算图片缩小后的长宽
            val width = Math.round(image.width * BITMAP_SCALE)
            val height = Math.round(image.height * BITMAP_SCALE)

            // 将缩小后的图片做为预渲染的图片
            val inputBitmap = Bitmap.createScaledBitmap(image, width, height, false)
            // 创建一张渲染后的输出图片
            val outputBitmap = Bitmap.createBitmap(inputBitmap)
            image.recycle()
            // 创建RenderScript内核对象
            val rs = RenderScript.create(context)
            // 创建一个模糊效果的RenderScript的工具对象
            val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

            // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间
            // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去
            val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
            val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)

            // 设置渲染的模糊程度, 25f是最大模糊度
            blurScript.setRadius(blurRadius)
            // 设置blurScript对象的输入内存
            blurScript.setInput(tmpIn)
            // 将输出数据保存到输出内存中
            blurScript.forEach(tmpOut)

            // 将数据填充到Allocation中
            tmpOut.copyTo(outputBitmap)
            return outputBitmap
        }
    }
}