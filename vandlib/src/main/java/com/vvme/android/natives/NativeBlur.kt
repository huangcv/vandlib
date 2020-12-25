package com.vvme.android.natives

import android.graphics.Bitmap

/**
 * @author : chunwei
 * @date : 2020/12/24
 * @description :
 *
 */
class NativeBlur {
    companion object {
        init {
            System.loadLibrary("vvme-android-lib")
        }

        external fun blurPixelsByNative(img: IntArray?, width: Int, height: Int, radius: Int)

        external fun blurBitmapByNative(bitmap: Bitmap?, radius: Int)
    }


}