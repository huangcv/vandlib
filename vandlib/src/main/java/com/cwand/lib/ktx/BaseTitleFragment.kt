//package com.cwand.lib.ktx
//
//import android.annotation.SuppressLint
//import android.graphics.Color
//import android.graphics.PorterDuff
//import android.graphics.drawable.Drawable
//import android.os.Bundle
//import android.text.Spannable
//import android.text.SpannableString
//import android.text.style.ForegroundColorSpan
//import android.view.*
//import android.widget.TextView
//import androidx.annotation.ColorInt
//import androidx.annotation.DrawableRes
//import androidx.annotation.MenuRes
//import androidx.annotation.StringRes
//import androidx.appcompat.app.ActionBar
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.core.content.ContextCompat
//import androidx.core.view.children
//import com.cwand.lib.ktx.entity.MenuEntity
//import kotlinx.android.synthetic.main.and_lib_base_title_fragment.*
//
//open abstract class BaseTitleFragment : AbsFragment() {
//
//    protected var contentView: View? = null
//
//    private var toolbarIsInit = false
//
//    var mToolbar: Toolbar? = null
//        private set
//
//    private var mTitleView: TextView? = null
//
//    private val menuList: MutableList<MenuEntity> by lazy { mutableListOf() }
//
//    private var mMenu: Menu? = null
//    private var menuCanReload = false
//    private var menuCreateState = 0
//
//    @ColorInt
//    var defMenuTitleColor = Color.WHITE
//
//    private var menuInflater: MenuInflater? = null
//
//    //页面是否全屏,已适配>=P的异形刘海屏
//    var fullScreen: Boolean = false
//        set(value) {
//            if (value != field) {
//                field = value
//                if (field) {
//                    this@BaseTitleFragment.activity?.let {
//                        if (it is AbsActivity) {
//                            it.fullScreen()
//                        }
//                    }
//                } else {
//                    this@BaseTitleFragment.activity?.let {
//                        if (it is AbsActivity) {
//                            it.exitFullScreen()
//                        }
//                    }
//                }
//            }
//        }
//
//
//    @DrawableRes
//    open fun backIconRes(): Int = View.NO_ID
//
//    open fun backIconDrawable(): Drawable? {
//        val backIconRes = backIconRes()
//        if (backIconRes == View.NO_ID) {
//            return null
//        }
//        return resources.getDrawable(backIconRes)
//    }
//
//    open fun titleText(): CharSequence {
//        val titleTextRes = titleTextRes()
//        if (titleTextRes == View.NO_ID) {
//            return ""
//        }
//        return getString(titleTextRes)
//    }
//
//    @StringRes
//    open fun titleTextRes(): Int = View.NO_ID
//
//    open fun isShowToolbar(): Boolean = true
//
//    open fun hideToolbar(show: Boolean) {
//        initToolbarConfig(show)
//    }
//
//    open fun showBackIcon(): Boolean = true
//
//    open fun hideBackIcon(show: Boolean) {
//        requireActionBarDoWith {
//            configHomeButton(it, show)
//        }
//    }
//
//    open fun skipBaseToolbarLayout(): Boolean = false
//
//    @ColorInt
//    open fun defBackIconColor(): Int {
//        return ContextCompat.getColor(requireContext(), android.R.color.white)
//    }
//
//    override fun createViews(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?,
//    ): View? {
//        if (!skipBaseToolbarLayout()) {
//            rootView = inflater.inflate(R.layout.and_lib_base_title_fragment, container, false)
//            initToolbarConfig(isShowToolbar())
//            and_lib_base_content_root_fragment?.let { root ->
//                root.removeAllViews()
//                contentView = inflater.inflate(bindLayout(), root, true)
//            }
//            return rootView
//        }
//        return super.createViews(inflater, container, savedInstanceState)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        contentView?.let {
//            initViews(arguments, it)
//        }
//        initListeners()
//    }
//
//    /**
//     * 初始化标题栏
//     */
//    private fun initToolbarConfig(show: Boolean) {
//        //设置fragment 是否有menu
//        setHasOptionsMenu(show)
//        rootView?.let {
//            if (mToolbar == null) {
//                if (!show) {
//                    return
//                }
//                it.findViewById<ViewStub>(R.id.and_lib_base_vs_tool_bar_fragment).inflate()
//                mToolbar = it.findViewById(R.id.and_lib_base_toolbar)
//                mTitleView = it.findViewById(R.id.and_lib_base_toolbar_title)
//            }
//            mToolbar?.let { toolBar ->
//                if (!show) {
//                    toolBar.visibility = View.GONE
//                    return
//                }
//                if (!toolBar.isShown) {
//                    toolBar.visibility = View.VISIBLE
//                }
//                if (mTitleView != null) {
//                    configTitle(mTitleView!!)
//                }
//                if (!toolbarIsInit) {
//                    configToolbar(toolBar)
//                    if (activity is AppCompatActivity) {
//                        (activity as AppCompatActivity).setSupportActionBar(toolBar)
//                        configNativeActionBar(showBackIcon())
//                    }
//                }
//                toolbarIsInit = true
//            }
//        }
//    }
//
//    /**
//     * 配置标题栏
//     */
//    open fun configToolbar(toolsBar: Toolbar) {
//        //设置主题色
//        //app:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
//        //    app:popupTheme="@style/ThemeOverlay.AppCompat.Light"
//        //标题栏背景色,默认跟随状态栏颜色
//        toolsBar.setBackgroundColor(statusBarBgColor)
//    }
//
//    /**
//     * 配置标题
//     */
//    open fun configTitle(title: TextView) {
//        //标题
//        title.text = titleText()
//    }
//
//    /**
//     * 更新标题
//     */
//    open fun updateTitle(title: String) {
//        mTitleView?.let {
//            it.text = title
//        }
//    }
//
//    @SuppressLint("PrivateResource")
//    open fun updateBackIconColor(@ColorInt color: Int) {
//        if (!showBackIcon()) {
//            return
//        }
//        (activity as AppCompatActivity).supportActionBar?.let { toolBar ->
//            val upArrow =
//                ContextCompat.getDrawable(requireContext(), R.drawable.abc_ic_ab_back_material)
//            upArrow?.let {
//                it.setColorFilter(
//                    color,
//                    PorterDuff.Mode.SRC_ATOP
//                )
//                toolBar.setHomeAsUpIndicator(it)
//            }
//        }
//    }
//
//    /**
//     * 配置toolbar
//     */
//    private fun configNativeActionBar(showBackIcon: Boolean) {
//        requireActionBarDoWith {
//            it.setDisplayShowTitleEnabled(false)
//            if (!showBackIcon) {
//                return@requireActionBarDoWith
//            }
//            configHomeButton(it, showBackIcon)
//            mToolbar?.let { toolBar ->
//                backIconDrawable()?.let { icon ->
//                    toolBar.navigationIcon = icon
//                }
//                toolBar.setNavigationOnClickListener { view ->
//                    if (showBackIcon) {
//                        onBackClick(view)
//                    }
//                }
//            }
//        }
//    }
//
//    @SuppressLint("PrivateResource")
//    private fun configHomeButton(actionBar: ActionBar, showBackIcon: Boolean) {
//        actionBar.setDisplayHomeAsUpEnabled(showBackIcon)
//        actionBar.setHomeButtonEnabled(showBackIcon)
//        if (!showBackIcon) {
//            return
//        }
//        if (backIconDrawable() == null) {
//            val upArrow =
//                ContextCompat.getDrawable(requireContext(), R.drawable.abc_ic_ab_back_material)
//            upArrow?.let {
//                it.setColorFilter(
//                    defBackIconColor(),
//                    PorterDuff.Mode.SRC_ATOP
//                )
//                actionBar.setHomeAsUpIndicator(it)
//            }
//        }
//    }
//
//    /**
//     * 点击返回按钮
//     */
//    open fun onBackClick(view: View) {
//        activity?.onBackPressed()
//    }
//
//    //---右侧菜单相关---
//
//    protected fun addMenu(vararg menus: MenuEntity) {
//        menuList.clear()
//        menuCanReload = true
//        //更具菜单个数选择对应的布局
//        menuList.addAll(menus)
//        if (menuList.size > 3) {
//            val subList = menuList.subList(0, 3)
//            menuList.clear()
//            menuList.addAll(subList)
//        }
//        initMenu()
//    }
//
//    /**
//     * 初始化菜单
//     */
//    private fun initMenu() {
//        if (menuCreateState == 1) {
//            mMenu?.let {
//                menuInflater?.inflate(bindMenuLayout(), it)
//            }
//        } else {
//            mMenu?.let {
//                onPrepareOptionsMenu(it)
//            }
//        }
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        mMenu = menu
//        menuInflater = inflater
//        menuCreateState = 1
//        val showMenu =
//            !fullScreen && !skipBaseToolbarLayout() && menuList.isNotEmpty() && isShowToolbar()
//        if (showMenu) {
//            menuCreateState = 2
//            inflater.inflate(bindMenuLayout(), mMenu)
//            if (menuCanReload) {
//                onPrepareOptionsMenu(mMenu!!)
//            }
//        }
//    }
//
//    override fun onPrepareOptionsMenu(menu: Menu) {
//        super.onPrepareOptionsMenu(menu)
//        initOptionMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.and_lib_base_menu_f_single1 -> {
//                onMenuClicked(0, menuList[0].title)
//            }
//            R.id.and_lib_base_menu_f_single2 -> {
//                onMenuClicked(1, menuList[1].title)
//            }
//            R.id.and_lib_base_menu_f_single3 -> {
//                onMenuClicked(2, menuList[2].title)
//            }
//            else -> {
//            }
//        }
//        return onMenuSelected(item)
//    }
//
//    protected open fun onMenuSelected(item: MenuItem?): Boolean {
//        return super.onOptionsItemSelected(item!!)
//    }
//
//    protected open fun initOptionMenu(menu: Menu?) {
//        if (!menuCanReload) {
//            return
//        }
//        if (menuList.isNotEmpty()) {
//            menu?.let {
//                menuCanReload = false
//                for (child in it.children) {
//                    child.isVisible = false
//                }
//                for (iv in menuList.withIndex()) {
//                    var titleC = defMenuTitleColor
//                    if (iv.value.titleColor != -1) {
//                        titleC = iv.value.titleColor
//                    }
//                    val ss = SpannableString(iv.value.title)
//                    ss.setSpan(
//                        ForegroundColorSpan(titleC),
//                        0,
//                        iv.value.title.length,
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                    )
//                    when (iv.index) {
//                        1 -> {
//                            val item2 = it.findItem(R.id.and_lib_base_menu_f_single2)
//                            item2.isVisible = true
//                            item2
//                        }
//                        2 -> {
//                            val item3 = it.findItem(R.id.and_lib_base_menu_f_single3)
//                            item3.isVisible = true
//                            item3
//                        }
//                        else -> {
//                            val item1 = it.findItem(R.id.and_lib_base_menu_f_single1)
//                            item1.isVisible = true
//                            item1
//                        }
//                    }.apply {
//                        title = ss
//                        if (iv.value.iconId != -1) {
//                            setIcon(iv.value.iconId)
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    protected open fun onMenuClicked(index: Int, title: CharSequence) {
//
//    }
//
//    @MenuRes
//    protected open fun bindMenuLayout(): Int {
//        return R.menu.and_lib_base_right_single_menu_fragment
//    }
//}
//
//fun BaseTitleFragment.requireActionBar(): ActionBar? {
//    if (this is AppCompatActivity) {
//        return (this as AppCompatActivity).supportActionBar
//    }
//    return null
//}
//
//fun BaseTitleFragment.requireActionBarDoWith(block: (ActionBar) -> Unit) {
//    requireActionBar()?.let {
//        block.invoke(it)
//    }
//}