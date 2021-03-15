package com.cwand.lib.ktx.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author : chunwei
 * @date : 2021/3/10
 * @description : Item 分割线
 *
 */
class DividerItemDecoration : RecyclerView.ItemDecoration {

    private val ATTRS = intArrayOf(android.R.attr.listDivider)

    private var dividerDrawable: Drawable? = null
    private val insetRect: Rect by lazy {
        Rect(50, 50, 50, 50)
    }

    constructor(context: Context) {
//        val attr = context.obtainStyledAttributes(ATTRS)
//        dividerDrawable = attr.getDrawable(0)
//        attr.recycle()
        if (dividerDrawable == null) {
            dividerDrawable = ColorDrawable(Color.RED)
        }
    }

    fun setDividerRect(left: Int, top: Int, right: Int, bottom: Int) {
        insetRect.set(left, top, right, bottom)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(insetRect)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        drawVertical(c, parent)
    }

    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        dividerDrawable?.let {
            val left = parent.paddingLeft
            val right = parent.width - parent.paddingRight
            val childCount = parent.childCount
            for (index in 0 until childCount) {
                val childView = parent.getChildAt(index)
                val lp: RecyclerView.LayoutParams =
                    childView.layoutParams as RecyclerView.LayoutParams
                val top = childView.bottom + lp.bottomMargin + 50
                val bottom = top + it.intrinsicHeight + 50
                it.setBounds(left, top, right, bottom)
                it.draw(c)
            }
        }
    }

}