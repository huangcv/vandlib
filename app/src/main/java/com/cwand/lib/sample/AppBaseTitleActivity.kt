package com.cwand.lib.sample

import com.cwand.lib.ktx.ui.BaseTitleActivity

/**
 * @author : chunwei
 * @date : 2020/12/25
 * @description :
 *
 */
abstract class AppBaseTitleActivity : BaseTitleActivity() {
    override fun multiLanguageSupport(): Boolean {
        return true
    }
}