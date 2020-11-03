package com.cwand.lib.ktx

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.and_lib_base_title_activity.*

abstract class BaseTitleActivity : AbsActivity() {

    private var toolbarIsInit = false

    var mToolbar: Toolbar? = null
        private set

    private var mTitleView: TextView? = null

    private var showMenu: Boolean = false


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
    open fun titleTextRes(): Int = View.NO_ID

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
        return ContextCompat.getColor(this, android.R.color.white)
    }

    override fun innerInit(savedInstanceState: Bundle?) {
        if (!skipBaseToolbarLayout()) {
            setContentView(R.layout.and_lib_base_title_activity)
            initToolbarConfig(isShowToolbar())
            and_lib_base_content_root?.let { root ->
                root.removeAllViews()
                LayoutInflater.from(this).inflate(bindLayout(), root, true)
            }
        } else {
            setContentView(bindLayout())
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
        }
        mToolbar?.let {
            if (!show) {
                it.visibility = View.GONE
                return
            }
            if (!it.isShown) {
                it.visibility = View.VISIBLE
            }
            if (mTitleView != null) {
                configTitle(mTitleView!!)
            }
            if (!toolbarIsInit) {
                configToolbar(it)
                setSupportActionBar(it)
                configNativeActionBar(showBackIcon())
            }
            toolbarIsInit = true
        }
    }

    /**
     * 配置标题栏
     */
    open fun configToolbar(toolsBar: Toolbar) {
        //设置主题色
        //app:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
        //    app:popupTheme="@style/ThemeOverlay.AppCompat.Light"
        //标题栏背景色,默认跟随状态栏颜色
        toolsBar.setBackgroundColor(statusBarBgColor)
    }

    /**
     * 配置标题
     */
    open fun configTitle(title: TextView) {
        //标题
        title.text = titleText()
    }

    /**
     * 更新标题
     */
    open fun updateTitle(title: String) {
        mTitleView?.let {
            it.text = title
        }
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

    protected fun addMenu(vararg menus: CharSequence) {
        //更具菜单个数选择对应的布局
        // TODO: 2020/11/3 选择菜单布局
    }

    /**
     * 初始化Menu UI, 只会调用一次
     *
     * @param menu
     * @return
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val condition =
            fullScreen || skipBaseToolbarLayout() || !isShowToolbar() || bindMenuLayout() == -1
        if (!condition) {
            menuInflater.inflate(bindMenuLayout(), menu)
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (bindMenuLayout() != -1) {
            initOptionMenu(menu)
            return true
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return onMenuSelected(item)
    }

    protected open fun onMenuSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item!!)
    }

    protected open fun initOptionMenu(menu: Menu?) {}

    @MenuRes
    protected open fun bindMenuLayout(): Int {
        return -1
    }


}