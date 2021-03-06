package com.cwand.lib.ktx.utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.LocaleList
import android.text.TextUtils
import com.cwand.lib.ktx.extensions.getAppSpValue
import com.cwand.lib.ktx.extensions.putAppSpValue
import java.util.*

/**
 * @author : chunwei
 * @date : 2020/12/10
 * @description : 语言切换工具类
 *
 */

enum class LanguageType(val language: String) {
    //跟随系统
    AUTO("auto"),

    //简体中文
    CHINESE("zh_CN"),

    //繁体中文
    TRADITIONAL_CHINESE("zh_TW"),

    //英语
    ENGLISH("en"),

    //日语
    JAPANESE("ja"),

    //朝鲜语
    KOREAN("ko"),

    //法语
    FRENCH("fr");
}

class LanguageUtils {

    companion object {
        const val LANGUAGE_SP_KEY = "AppLocalLanguage"
        const val TAG = "LanguageUtils"

        /**
         * 语言是否相同
         */
        fun isSameLanguage(context: Context, newLanguage: String): Boolean {
            return getLanguage(context).equals(newLanguage, true)
        }

        /**
         * 保存语言
         */
        @JvmStatic
        fun saveLanguage(context: Context, key: String, value: String): Boolean {
            //保存切换的语言
            return context.putAppSpValue(key, value)
        }

        /**
         * 获取当前语言类型
         */
        @JvmStatic
        fun getLanguage(context: Context): String {
            var appSpValue = context.getAppSpValue(LANGUAGE_SP_KEY)
            if (TextUtils.isEmpty(appSpValue)) {
                appSpValue = Locale.getDefault().language
            }
            return appSpValue
        }

        /**
         * 应用更改的语言
         */
        @JvmStatic
        fun changeAppLanguage(context: Context, newLanguage: String) {
            if (TextUtils.isEmpty(newLanguage)) {
                return
            }
            val resources = context.resources
            val configuration = resources.configuration
            // 获取想要切换的语言类型
            val locale = getLocaleByLanguage(newLanguage)
            //设置默认语言
            Locale.setDefault(locale)
            //配置语言
            configuration.setLocale(locale)
            // update configuration
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }

        /**
         * 通过语言类型获取本地对象
         */
        @JvmStatic
        private fun getLocaleByLanguage(language: String): Locale {
            return when (language) {
                LanguageType.AUTO.language -> {
                    Locale.getDefault()
                }
                LanguageType.CHINESE.language -> {
                    Locale.SIMPLIFIED_CHINESE
                }
                LanguageType.ENGLISH.language -> {
                    Locale.ENGLISH
                }
                LanguageType.KOREAN.language -> {
                    Locale.KOREAN
                }
                LanguageType.TRADITIONAL_CHINESE.language -> {
                    Locale.TRADITIONAL_CHINESE
                }
                LanguageType.JAPANESE.language -> {
                    Locale.JAPANESE
                }
                LanguageType.FRENCH.language -> {
                    Locale.FRENCH
                }
                else -> {
                    Locale.getDefault()
                }
            }
        }

        /**
         * 包装Context
         */
        @JvmStatic
        fun attachBaseContext(context: Context, language: String = getLanguage(context)): Context {
            return updateResources(context, language)
        }

        /**
         * 更新资源
         */
        @TargetApi(Build.VERSION_CODES.N)
        private fun updateResources(context: Context, language: String): Context {
            val resources = context.resources
            val locale = getLocaleByLanguage(language)
            val configuration = resources.configuration
            return when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                    configuration.setLocale(locale)
                    configuration.setLocales(LocaleList(locale))
                    return context.createConfigurationContext(configuration)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                    configuration.setLocale(locale)
                    context.createConfigurationContext(configuration)
                }
                else -> {
                    context
                }
            }
        }
    }

}