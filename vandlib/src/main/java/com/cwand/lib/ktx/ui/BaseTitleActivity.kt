package com.cwand.lib.ktx.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.cwand.lib.ktx.R
import com.cwand.lib.ktx.entity.MenuEntity
import kotlinx.android.synthetic.main.and_lib_base_title_activity.*

abstract class BaseTitleActivity : BaseActivity() {

    private var toolbarIsInit = false

    var mToolbar: Toolbar? = null
        private set

    var rootView: View? = null
        private set

    private var mTitleView: TextView? = null
    private var mTitleIcon: ImageView? = null

    private val menuList: MutableList<MenuEntity> by lazy { mutableListOf<MenuEntity>() }

    private var mMenu: Menu? = null
    private var menuCanReload = false
    private var menuCreateState = 0
    var toolbarElevation: Float = 0f
        set(value) {
            field = value
            configToolbarElevation()
        }

    @ColorInt
    var defMenuTitleColor = Color.WHITE
    var defMenuTitleSize = 14

    @ColorInt
    var toolbarBgColor: Int = Color.WHITE
        set(value) {
            if (value != field) {
                field = value
                themeToolbarBgColor = value
                updateToolbarBgColor(value)
            }
        }


    @DrawableRes
    open fun backIconRes(): Int = View.NO_ID

    open fun backIconDrawable(): Drawable? {
        val backIconRes = backIconRes()
        if (backIconRes == View.NO_ID) {
            return null
        }
        return resources.getDrawable(backIconRes)
    }

    open fun titleText(): CharSequence {
        val titleTextRes = titleTextRes()
        if (titleTextRes == View.NO_ID) {
            return ""
        }
        return getString(titleTextRes)
    }

    @StringRes
    abstract fun titleTextRes(): Int

    open fun isShowToolbar(): Boolean = true

    open fun hideToolbar(show: Boolean) {
        initToolbarConfig(show)
    }

    open fun showBackIcon(): Boolean = true

    open fun hideBackIcon(show: Boolean) {
        supportActionBar?.let {
            configHomeButton(it, show)
        }
    }

    open fun skipBaseToolbarLayout(): Boolean = false

    @ColorInt
    open fun defBackIconColor(): Int {
        return themeToolbarIconColor
    }

    override fun bindLayout(): Int = -1
    override fun initViews(savedInstanceState: Bundle?) {

    }

    override fun initListeners() {

    }

    override fun initData() {
    }

    override fun innerInit(savedInstanceState: Bundle?) {
        if (!skipBaseToolbarLayout()) {
            setContentView(R.layout.and_lib_base_title_activity)
            rootView = findViewById(R.id.and_lib_base_root)
            initToolbarConfig(isShowToolbar())
            if (bindLayout() != -1) {
                and_lib_base_content_root?.let { root ->
                    root.removeAllViews()
                    LayoutInflater.from(this).inflate(bindLayout(), root, true)
                }
            }
        } else {
            if (bindLayout() != -1) {
                setContentView(bindLayout())
            }
        }
        initViews(savedInstanceState)
        initListeners()
    }

    /**
     * 初始化标题栏
     */
    private fun initToolbarConfig(show: Boolean) {
        if (mToolbar == null) {
            if (!show) {
                return
            }
            and_lib_base_vs_tool_bar.inflate()
            mToolbar = findViewById(R.id.and_lib_base_toolbar)
            mTitleView = findViewById(R.id.and_lib_base_toolbar_title)
            mTitleIcon = findViewById(R.id.and_lib_base_toolbar_title_icon)
        }
        mToolbar?.let {
            if (!show) {
                it.visibility = View.GONE
                return
            }
            if (!it.isShown) {
                it.visibility = View.VISIBLE
            }
            if (!toolbarIsInit) {
                setSupportActionBar(it)
                configToolbar(it)
                toolbarIsInit = true
            }
        }
    }

    private fun configToolbarElevation() {
        mToolbar?.let {
            if (toolbarElevation > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    it.elevation = toolbarElevation
                }
            }
        }
    }

    /**
     * 配置标题栏
     */
    open fun configToolbar(toolsBar: Toolbar) {
        //设置主题色
        //app:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
        //    app:popupTheme="@style/ThemeOverlay.AppCompat.Light"
        //设置标题栏背景色
        updateToolbarBgColor(themeToolbarBgColor)
        //设置标题
        mTitleView?.let {
            configTitle(it)
        }
        //设置标题栏字体颜色
        updateTitleColor(themeTitleTextColor)
        //设置标题栏
        configNativeActionBar(showBackIcon())
        //设置标题栏阴影特效
        configToolbarElevation()
    }

    private fun updateToolbarBgColor(color: Int) {
        mToolbar?.setBackgroundColor(color)
    }

    /**
     * 配置标题
     */
    open fun configTitle(title: TextView) {
        //标题
        title.text = titleText()
    }

    /**
     * 设置标题栏字体颜色
     */
    open fun updateTitleColor(@ColorInt color: Int) {
        mTitleView?.setTextColor(themeTitleTextColor)
    }

    /**
     * 更新标题
     */
    open fun updateTitle(title: String) {
        mTitleView?.let {
            it.text = title
        }
    }

    protected fun updateTitleIcon(@DrawableRes iconRes: Int) {
        mTitleIcon?.setBackgroundResource(iconRes)
    }

    @SuppressLint("PrivateResource")
    open fun updateBackIconColor(@ColorInt color: Int) {
        if (!showBackIcon()) {
            return
        }
        supportActionBar?.let { toolBar ->
            val upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material)
            upArrow?.let {
                it.setColorFilter(
                    color,
                    PorterDuff.Mode.SRC_ATOP
                )
                toolBar.setHomeAsUpIndicator(it)
            }
        }
    }

    /**
     * 配置toolbar
     */
    private fun configNativeActionBar(showBackIcon: Boolean) {
        supportActionBar?.let {
            it.setDisplayShowTitleEnabled(false)
            if (!showBackIcon) {
                return
            }
            configHomeButton(it, showBackIcon)
            mToolbar?.let { toolBar ->
                backIconDrawable()?.let { icon ->
                    toolBar.navigationIcon = icon
                }
                toolBar.setNavigationOnClickListener { view ->
                    if (showBackIcon) {
                        onBackClick(view)
                    }
                }
            }
        }
    }

    @SuppressLint("PrivateResource")
    private fun configHomeButton(actionBar: ActionBar, showBackIcon: Boolean) {
        actionBar.setDisplayHomeAsUpEnabled(showBackIcon)
        actionBar.setHomeButtonEnabled(showBackIcon)
        if (!showBackIcon) {
            return
        }
        if (backIconDrawable() == null) {
            val upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material)
            upArrow?.let {
                it.setColorFilter(
                    defBackIconColor(),
                    PorterDuff.Mode.SRC_ATOP
                )
                actionBar.setHomeAsUpIndicator(it)
            }
        }
    }

    protected fun putFragment(fragment: Fragment) {
        if (skipBaseToolbarLayout()) {
            return
        }
        putFragment(fragment, R.id.and_lib_base_content_root)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        supportFragmentManager.fragments.forEach {
            it.onActivityResult(requestCode, resultCode, data)
        }
    }

    /**
     * 点击返回按钮
     */
    open fun onBackClick(view: View) {
        onBackPressed()
    }

    //---右侧菜单相关---

    protected fun addMenu(vararg menus: MenuEntity, isClean: Boolean = false) {
        if (isClean) {
            menuList.clear()
        }
        menuCanReload = true
        //更具菜单个数选择对应的布局
        menuList.addAll(menus)
        if (menuList.size > 3) {
            val subList = menuList.subList(0, 3)
            menuList.clear()
            menuList.addAll(subList)
        }
        initMenu()
    }

    /**
     * 初始化菜单
     */
    private fun initMenu() {
        if (menuCreateState == 1) {
            menuInflater.inflate(bindMenuLayout(), mMenu)
        }
        onPrepareOptionsMenu(mMenu)
    }

    /**
     * 初始化Menu UI, 只会调用一次
     *
     * @param menu
     * @return
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        mMenu = menu
        menuCreateState = 1
        val showMenu =
            !fullScreen && !skipBaseToolbarLayout() && menuList.isNotEmpty() && isShowToolbar()
        if (showMenu) {
            menuCreateState = 2
            menuInflater.inflate(bindMenuLayout(), mMenu)
            return true
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        initOptionMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.and_lib_base_menu_single1 -> {
                onMenuClicked(item, menuList[0].menuId, menuList[0].title)
            }
            R.id.and_lib_base_menu_single2 -> {
                onMenuClicked(item, menuList[1].menuId, menuList[1].title)
            }
            R.id.and_lib_base_menu_single3 -> {
                onMenuClicked(item, menuList[2].menuId, menuList[2].title)
            }
            else -> {
            }
        }
        return onMenuSelected(item)
    }

    protected open fun onMenuSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item!!)
    }

    protected open fun initOptionMenu(menu: Menu?) {
        if (!menuCanReload) {
            return
        }
        if (menuList.isNotEmpty()) {
            menu?.let {
                for (child in it.children) {
                    child.isVisible = false
                }
                for (iv in menuList.withIndex()) {
                    var titleC = defMenuTitleColor
                    var titleS = defMenuTitleSize
                    if (iv.value.titleColor != -1) {
                        titleC = iv.value.titleColor
                    }
                    if (iv.value.titleSize != -1) {
                        titleS = iv.value.titleSize
                    }
                    val ss = SpannableString(iv.value.title)
                    //字体颜色
                    ss.setSpan(
                        ForegroundColorSpan(titleC),
                        0,
                        iv.value.title.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    //字体大小
                    ss.setSpan(
                        AbsoluteSizeSpan(titleS, true),
                        0,
                        iv.value.title.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    when (iv.index) {
                        1 -> {
                            val item2 = it.findItem(R.id.and_lib_base_menu_single2)
                            item2?.let { i2 ->
                                i2.isVisible = true
                            }
                            item2
                        }
                        2 -> {
                            val item3 = it.findItem(R.id.and_lib_base_menu_single3)
                            item3?.let { i3 ->
                                i3.isVisible = true
                            }
                            item3
                        }
                        else -> {
                            val item1 = it.findItem(R.id.and_lib_base_menu_single1)
                            item1?.let { i1 ->
                                i1.isVisible = true
                            }
                            item1
                        }
                    }?.apply {
                        title = ss
                        if (iv.value.iconId != -1) {
                            setIcon(iv.value.iconId)
                        }
                    }
                }
            }
        }
    }

    protected open fun onMenuClicked(menu: MenuItem, menuId: Int, title: CharSequence) {

    }

    @MenuRes
    protected open fun bindMenuLayout(): Int {
        return R.menu.and_lib_base_right_single_menu
    }

    protected fun getStatusBarHeight(): Int {
        var result = 0
        val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resId > 0) {
            result = resources.getDimensionPixelSize(resId)
        }
        return result
    }

    /**
     * 接受来自Fragment发送来的事件消息
     */
    override fun onEventAction(id: Int, extraData: Any?) {
    }

}