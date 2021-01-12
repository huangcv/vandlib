package com.cwand.lib.ktx.widgets

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cwand.lib.ktx.extensions.logD

/**
 * @author : chunwei
 * @date : 2020/12/29
 * @description : 无限循环
 *  与自定义ViewGroup类似,自定LayoutManager所要做的就是对ItemView的添加,测量和布局,与之不同点在于,LayoutManager多了回收的工作.
 *
 */
class CycleLayoutManager : RecyclerView.LayoutManager() {

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    /**
     * 支持横向滚动
     */
    override fun canScrollHorizontally(): Boolean {
        return true
    }

    /**
     * 支持纵向滚动
     */
    override fun canScrollVertically(): Boolean {
        return false
    }

    /**
     * 对ItemView进行添加,测量和布局
     */
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount <= 0) return
        if (state.isPreLayout) return
        //将所有的ItemView分离至Scrap
        detachAndScrapAttachedViews(recycler)
        //对所有的ItemView进行添加,测量和布局
        var itemLeft = paddingLeft
        val itemRight = paddingRight
        val itemBottom = paddingBottom
        val itemTop = paddingTop
        var i = 0
        while (true) {
            if (itemLeft >= width - itemRight) return
            val itemView = recycler.getViewForPosition(i % itemCount)
            //添加ItemView
            addView(itemView)
            //测量ItemView
            measureChildWithMargins(itemView, 0, 0)
            //布局ItemView
            val r = itemLeft + getDecoratedMeasuredWidth(itemView)
            val t = itemTop
            val b = t + getDecoratedMeasuredHeight(itemView) - itemBottom
            layoutDecoratedWithMargins(itemView, itemLeft, t, r, b)
            itemLeft = r
            i++
        }
    }


    /**
     * 说明:
     * 滑动与填充
     * 当左滑后子View被左移动时，RecyclerView的右侧会出现可见的未填充区域，这时需要在RecyclerView右侧添加并布局好新的子View，直到没有可见的未填充区域为止。同样，在右滑后需要对左侧的未填充区域进行填充。
     */


    /**
     * 对子View整体左右移动,为了在滑动RecyclerView时有子View移动的效果，需要复写scrollHorizontallyBy函数，并在其中调用offsetChildrenHorizontal(int x)。
     */
    override fun offsetChildrenHorizontal(dx: Int) {
        super.offsetChildrenHorizontal(dx)
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
    ): Int {
        //填充ItemView
        fill(recycler, dx > 0)
        //调用 offsetChildrenHorizontal(dx)
        offsetChildrenHorizontal(-dx)
        //回收工作:不可见的ItemView
        recycleChildView(recycler, dx > 0)
        return dx
    }

    private fun fill(recycler: RecyclerView.Recycler, fillEnd: Boolean) {
        if (childCount == 0) return
        if (fillEnd) {
            //填充尾部
            //需要填充的ItemView
            val anchorView = getChildAt(childCount - 1)
            anchorView?.let {
                var tempAnchorView = it
                //ItemView所在的位置
                val anchorPosition = getPosition(tempAnchorView)
                while (true) {
                    if (tempAnchorView.right < width - paddingRight) return@let
                    var position = (anchorPosition + 1) % itemCount
                    if (position < 0) position += itemCount
                    val scrapItem = recycler.getViewForPosition(position)
                    //添加
                    addView(scrapItem)
                    //测量
                    measureChildWithMargins(scrapItem, 0, 0)
                    //布局
                    val r = tempAnchorView.left
                    val t = paddingTop
                    val b = t + getDecoratedMeasuredHeight(scrapItem) - paddingBottom
                    val l = r - getDecoratedMeasuredWidth(scrapItem)
                    layoutDecoratedWithMargins(scrapItem, l, t, r, b)
                    tempAnchorView = scrapItem
                }
            }

        } else {
            //填充首部
            val anchorView = getChildAt(0)
            anchorView?.let {
                var tempAnchorView = it
                val anchorPosition = getPosition(tempAnchorView)
                while (true) {
                    if (tempAnchorView.left > paddingLeft) return@let
                    var position = (anchorPosition - 1) % itemCount
                    if (position < 0) position += itemCount
                    val scrapItem = recycler.getViewForPosition(position)
                    //添加
                    addView(scrapItem)
                    //测量
                    measureChildWithMargins(scrapItem, 0, 0)
                    //布局
                    val r = tempAnchorView.left
                    val t = paddingTop
                    val b = t + getDecoratedMeasuredHeight(scrapItem) - paddingBottom
                    val l = r - getDecoratedMeasuredWidth(scrapItem)
                    layoutDecoratedWithMargins(scrapItem, l, t, r, b)
                    tempAnchorView = scrapItem
                }
            }
        }
    }

    private fun recycleChildView(recycler: RecyclerView.Recycler, fillEnd: Boolean) {
        if (fillEnd) {
            //回收首部
            for (i in 0 until childCount - 1) {
                val view = getChildAt(i)
                val needRecycle = view != null && view.right < paddingLeft
                if (needRecycle) {
                    removeAndRecycleView(view!!, recycler)
                    continue
                }
                break
            }
        } else {
            //回收尾部
            for (i in childCount - 1 downTo 0) {
                val view = getChildAt(i)
                val needRecycle = view != null && view.left > width - paddingRight
                if (needRecycle) {
                    removeAndRecycleView(view!!, recycler)
                    continue
                }
                break
            }
        }
    }
}