package com.cwand.lib.sample

import com.cwand.lib.ktx.ui.BaseVMActivity
import com.cwand.lib.ktx.viewmodel.BaseViewModel

/**
 * @author : chunwei
 * @date : 2020/12/25
 * @description :
 *
 */
abstract class AppBaseVMActivity<VM : BaseViewModel> : BaseVMActivity<VM>() {
    override fun multiLanguageSupport(): Boolean {
        return true
    }
}