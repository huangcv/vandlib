package com.cwand.lib.ktx.widgets

import android.app.Activity
import android.content.Context
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import com.cwand.lib.ktx.extensions.safeRun


/**
 * @author : chunwei
 * @date : 2021/3/16
 * @description :
 *
 */

class NiceLoading private constructor() {

    private var config: NiceLoadingConfig = NiceLoadingConfig.defaultConfig


    companion object {

        private val instance: NiceLoading by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NiceLoading()
        }

        @JvmStatic
        fun config(config: NiceLoadingConfig = NiceLoadingConfig.defaultConfig) {
            instance.config = config
        }

        @JvmStatic
        fun bind(activity: Activity): StateHolderBuilder {
            return instance.bindWithActivity(activity)
        }

        @JvmStatic
        fun bind(view: View?): StateHolderBuilder {
            return instance.bindWithView(view)
        }

        @JvmStatic
        fun bind(activity: Activity, @IdRes idRes: Int): StateHolderBuilder {
            return instance.bindWithId(activity, idRes)
        }
    }

    private fun bindWithActivity(activity: Activity): StateHolderBuilder {
        //查找全局配置,是否配置了全局ID,如果没有默认android.R.id.content
        var contentId = android.R.id.content
        if (config.baseContentIdRes != View.NO_ID) {
            contentId = config.baseContentIdRes
        }
        return bindWithId(activity, contentId)
    }

    private fun bindWithId(activity: Activity, @IdRes idRes: Int): StateHolderBuilder =
        bindWithView(activity.findViewById<View>(idRes))

    private fun bindWithView(view: View?): StateHolderBuilder = StateHolderBuilder(view, config)

}

typealias ErrorClickAction = () -> Unit
typealias NoNetworkClickAction = () -> Unit
typealias EmptyClickAction = () -> Unit

class StateHolderBuilder internal constructor(
    internal val originView: View?,
    internal val config: NiceLoadingConfig
) {
    @IdRes
    internal var errorClickIdRes: Int = config.errorClickIdRes
        private set

    @IdRes
    internal var emptyClickIdRes: Int = config.emptyClickIdRes
        private set

    @IdRes
    internal var noNetworkClickIdRes: Int = config.noNetworkClickIdRes
        private set
    internal var defaultState: State = config.defaultState
    internal var viewAdapter: ViewAdapter? = config.viewAdapter
    internal var errorDrawableRes: Int = config.errorDrawableRes
    internal var emptyDrawableRes: Int = config.emptyDrawableRes
    internal var noNetworkDrawableRes: Int = config.noNetworkDrawableRes
    internal var animation: Boolean = config.animation
    internal var animationDuration: Long = config.animationDuration
    internal var animationInterpolator: Interpolator? = config.animationInterpolator
    internal var errorAction: ErrorClickAction? = null
    internal var noNetworkAction: NoNetworkClickAction? = null
    internal var emptyAction: EmptyClickAction? = null
    internal var wrapperView: FrameLayout? = null
    internal var eventPenetration: Boolean = config.eventPenetration


    private fun buildWrapperContent(originView: View?) {
        originView?.let {
            wrapperView = FrameLayout(it.context)
            val lp: ViewGroup.LayoutParams = it.layoutParams
            wrapperView!!.layoutParams = lp
            if (it.parent != null) {
                val parent = it.parent as ViewGroup
                val index = parent.indexOfChild(originView)
                parent.removeView(originView)
                parent.addView(wrapperView, index)
            }
            val newLp = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            wrapperView!!.addView(originView, newLp)
        }

    }

    fun viewAdapter(viewAdapter: ViewAdapter?) = apply {
        this.viewAdapter = viewAdapter
    }

    fun errorDrawable(@DrawableRes idRes: Int) = apply {
        this.errorDrawableRes = idRes
    }

    fun emptyDrawable(@DrawableRes idRes: Int) = apply {
        this.emptyDrawableRes = idRes
    }

    fun noNetworkDrawable(@DrawableRes idRes: Int) = apply {
        this.noNetworkDrawableRes = idRes
    }

    fun animationEnable(enable: Boolean) = apply {
        this.animation = enable
    }

    fun animationDuration(duration: Long) = apply {
        this.animationDuration = duration
    }

    fun animationInterpolator(interpolator: Interpolator) = apply {
        this.animationInterpolator = interpolator
    }

    fun errorClick(@IdRes idRes: Int = config.errorClickIdRes, errorClickAction: ErrorClickAction) =
        apply {
            this.errorClickIdRes = idRes
            this.errorAction = errorClickAction
        }

    fun emptyClick(@IdRes idRes: Int = config.emptyClickIdRes, emptyClickAction: EmptyClickAction) =
        apply {
            this.emptyClickIdRes = idRes
            this.emptyAction = emptyClickAction
        }

    fun noNetworkClick(
        @IdRes idRes: Int = config.noNetworkClickIdRes,
        noNetworkClickAction: NoNetworkClickAction
    ) = apply {
        this.noNetworkClickIdRes = idRes
        this.noNetworkAction = noNetworkClickAction
    }

    fun eventPenetration(penetration: Boolean) = apply {
        this.eventPenetration = penetration
    }

    fun defaultState(state: State) = apply {
        this.defaultState = state
    }

    fun build(): StateHolder {
        buildWrapperContent(originView)
        return StateHolder(this)
    }
}

class NiceLoadingConfig private constructor() {

    var baseContentIdRes = View.NO_ID
        private set
    var viewAdapter: ViewAdapter? = null
        private set
    var errorDrawableRes: Int = 0
        private set
    var emptyDrawableRes: Int = 0
        private set
    var noNetworkDrawableRes: Int = 0
        private set
    var animation: Boolean = true
        private set
    var animationDuration: Long = 300
        private set
    var animationInterpolator: Interpolator? = null
        private set
    var defaultState: State = State.CONTENT
        private set

    @IdRes
    var errorClickIdRes: Int = View.NO_ID
        private set

    @IdRes
    var emptyClickIdRes: Int = View.NO_ID
        private set

    @IdRes
    var noNetworkClickIdRes: Int = View.NO_ID
        private set

    /**
     * 点击事件是否穿透,默认不穿透点击事件
     */
    var eventPenetration: Boolean = false
        private set

    companion object {
        val defaultConfig: NiceLoadingConfig by lazy {
            NiceLoadingConfig()
        }

        @JvmStatic
        fun obtain(): NiceLoadingConfig {
            return NiceLoadingConfig()
        }
    }

    fun baseContentIdRes(@IdRes idRes: Int) = apply {
        this.baseContentIdRes = idRes
    }

    fun viewAdapter(viewAdapter: ViewAdapter?) = apply {
        this.viewAdapter = viewAdapter
    }

    fun errorDrawable(@DrawableRes idRes: Int) = apply {
        this.errorDrawableRes = idRes
    }

    fun emptyDrawable(@DrawableRes idRes: Int) = apply {
        this.emptyDrawableRes = idRes
    }

    fun noNetworkDrawable(@DrawableRes idRes: Int) = apply {
        this.noNetworkDrawableRes = idRes
    }

    fun errorClickIdRes(@IdRes idRes: Int) = apply {
        this.errorClickIdRes = idRes
    }

    fun emptyClickIdRes(@IdRes idRes: Int) = apply {
        this.emptyClickIdRes = idRes
    }

    fun noNetworkClickIdRes(@IdRes idRes: Int) = apply {
        this.noNetworkClickIdRes = idRes
    }

    fun animationEnable(enable: Boolean) = apply {
        this.animation = enable
    }

    fun animationDuration(duration: Long) = apply {
        this.animationDuration = duration
    }

    fun animationInterpolator(interpolator: Interpolator) = apply {
        this.animationInterpolator = interpolator
    }

    fun eventPenetration(penetration: Boolean) = apply {
        this.eventPenetration = penetration
    }

    fun defaultState(state: State) = apply {
        this.defaultState = state
    }

}

class StateHolder internal constructor(val builder: StateHolderBuilder) {

    private val viewCache = SparseArray<View?>()
    private var currentState = builder.defaultState
    private var currentView: View? = builder.originView

    init {
        showDefaultState()
    }

    private fun isShowable(state: State): Boolean = currentState != state

    fun showLoading() {
        if (isShowable(State.LOADING)) {
            doShowState(State.LOADING)
        }
    }

    fun showContent() {
        if (isShowable(State.CONTENT)) {
            doShowState(State.CONTENT)
        }
    }

    fun showError() {
        if (isShowable(State.ERROR)) {
            doShowState(State.ERROR)
        }
    }

    fun showEmpty() {
        if (isShowable(State.EMPTY)) {
            doShowState(State.EMPTY)
        }
    }

    fun showNoNetwork() {
        if (isShowable(State.NO_NETWORK)) {
            doShowState(State.NO_NETWORK)
        }
    }

    private fun doShowState(state: State) {
        if (validate()) {
            safeRun {
                //获取缓存
                var targetView = viewCache.get(state.ordinal)
                if (targetView == null && state != State.CONTENT) {
                    targetView =
                        builder.viewAdapter?.onCreateView(builder.wrapperView!!.context, state)
                            ?: builder.config.viewAdapter?.onCreateView(
                                builder.wrapperView!!.context,
                                state
                            )
                    targetView?.let {
                        builder.viewAdapter?.onViewCreated(it, state, this)
                            ?: builder.config.viewAdapter?.onViewCreated(it, state, this)
                        viewCache.put(state.ordinal, it)
                        bindClickEvent(it, state)
                    }
                    if (targetView == null) {
                        //没有初始化view
                        return@safeRun
                    }
                }
                if (targetView == null && currentState != State.CONTENT) {
                    targetView = builder.originView
                }
                val rootView = builder.wrapperView!!
                if (currentView == targetView) {
                    //判断该View 是否出在最顶层
                    bringTargetViewFront(rootView, targetView)
                } else {
                    updateView(rootView, targetView)
                }
                currentView = targetView
                currentState = state
            }
        }
    }

    private fun bindClickEvent(targetView: View, state: State) {
        //如果设置了点击事件,但是没有指定id,则整个区域可点击
        targetView.isClickable = !builder.eventPenetration
        when (state) {
            State.NO_NETWORK -> {
                if (builder.noNetworkClickIdRes != View.NO_ID) {
                    targetView.findViewById<View>(builder.noNetworkClickIdRes)?.let {
                        it.setOnClickListener {
                            builder.noNetworkAction?.invoke()
                        }
                    }
                } else {
                    //判断点击事件是否为空
                    builder.noNetworkAction?.let { action ->
                        targetView.setOnClickListener {
                            action.invoke()
                        }
                    }
                }
            }
            State.ERROR -> {
                if (builder.errorClickIdRes != View.NO_ID) {
                    targetView.findViewById<View>(builder.errorClickIdRes)?.let {
                        it.setOnClickListener {
                            builder.errorAction?.invoke()
                        }
                    }
                } else {
                    //判断点击事件是否为空
                    builder.errorAction?.let { action ->
                        targetView.setOnClickListener {
                            action.invoke()
                        }
                    }
                }
            }
            State.EMPTY -> {
                if (builder.emptyClickIdRes != View.NO_ID) {
                    targetView.findViewById<View>(builder.emptyClickIdRes)?.let {
                        it.setOnClickListener {
                            builder.emptyAction?.invoke()
                        }
                    }
                } else {
                    //判断点击事件是否为空
                    builder.emptyAction?.let { action ->
                        targetView.setOnClickListener {
                            action.invoke()
                        }
                    }
                }
            }
            else -> {
            }
        }
    }

    private fun updateView(rootView: FrameLayout, targetView: View?) {
        currentView?.let {
            rootView.removeView(currentView)
        }
        //添加要显示的View
        if (rootView.indexOfChild(targetView) >= 0) {
            bringTargetViewFront(rootView, targetView)
        } else {
            //没有添加过
            rootView.addView(targetView)
            targetView!!.layoutParams?.apply {
                height = ViewGroup.LayoutParams.MATCH_PARENT
                width = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }
    }

    private fun bringTargetViewFront(
        rootView: FrameLayout,
        targetView: View?
    ) {
        if (rootView.indexOfChild(targetView) != rootView.childCount - 1) {
            targetView?.bringToFront()
        }
    }

    private fun showDefaultState() {
        //第一次跳过检测
        doShowState(currentState)
    }

    private fun validate(): Boolean {
        return builder.wrapperView != null
    }
}

enum class State {
    LOADING,
    CONTENT,
    ERROR,
    EMPTY,
    NO_NETWORK
}

interface ViewAdapter {
    fun onCreateView(context: Context, state: State): View?
    fun onViewCreated(view: View?, state: State, holder: StateHolder)
}