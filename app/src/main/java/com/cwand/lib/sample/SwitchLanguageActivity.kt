package com.cwand.lib.sample

import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.cwand.lib.ktx.ui.BaseTitleActivity
import com.cwand.lib.ktx.ext.delayRun
import com.cwand.lib.ktx.ext.onClick
import com.cwand.lib.ktx.utils.ActManager
import com.cwand.lib.ktx.utils.LanguageType
import com.cwand.lib.ktx.utils.LanguageUtils
import kotlinx.android.synthetic.main.activity_switch_language.*

/**
 * @author : chunwei
 * @date : 2020/12/14
 * @description : 切换语言页面
 *
 */
class SwitchLanguageActivity : BaseTitleActivity() {

    private var currentLanguageType: LanguageType? = null
    private var languageType: LanguageType = LanguageType.AUTO

    override fun titleTextRes(): Int {
        return R.string.switch_language
    }

    override fun bindLayout(): Int {
        return R.layout.activity_switch_language
    }

    override fun initViews(savedInstanceState: Bundle?) {
        rg_lang_group?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_lang_zh -> {
                    //简体中文
                    languageType = LanguageType.CHINESE
                }
                R.id.rb_lang_ft -> {
                    //繁体中文
                    languageType = LanguageType.TRADITIONAL_CHINESE
                }
                R.id.rb_lang_en -> {
                    //英文
                    languageType = LanguageType.ENGLISH
                }
                R.id.rb_lang_ko -> {
                    //韩文
                    languageType = LanguageType.KOREAN
                }
                else -> {
                    //自动
                    languageType = LanguageType.AUTO
                }
            }
        }
    }

    override fun initData() {
        //更新语言
        when (LanguageUtils.getLanguage(this)) {
            LanguageType.CHINESE.language -> {
                currentLanguageType = LanguageType.CHINESE
                rb_lang_zh?.isChecked = true
            }
            LanguageType.TRADITIONAL_CHINESE.language -> {
                currentLanguageType = LanguageType.TRADITIONAL_CHINESE
                rb_lang_ft?.isChecked = true
            }
            LanguageType.ENGLISH.language -> {
                currentLanguageType = LanguageType.ENGLISH
                rb_lang_en?.isChecked = true
            }
            LanguageType.KOREAN.language -> {
                currentLanguageType = LanguageType.KOREAN
                rb_lang_ko?.isChecked = true
            }
            else -> {
                currentLanguageType = LanguageType.AUTO
                rb_lang_auto?.isChecked = true
            }
        }
    }

    override fun initListeners() {
        btn_lang_confirm?.onClick {
            if (currentLanguageType == languageType) {
                finish()
                return@onClick
            }
            changeLanguage(languageType.language) {
                ActManager.restartActivity("MainActivity")
                delayRun({
                    finish()
                }, 100)
            }
        }
    }

    private fun restartActivity() {
        // 不同的版本，使用不同的重启方式，达到最好的效果
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            // 6.0 以及以下版本，使用这种方式，并给 activity 添加启动动画效果，可以规避黑屏和闪烁问题
            val intent = Intent(this, SwitchLanguageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        } else {
            // 6.0 以上系统直接调用重新创建函数，可以达到无缝切换的效果
            recreate()
        }
    }
}