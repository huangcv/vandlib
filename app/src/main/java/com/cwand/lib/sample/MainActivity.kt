package com.cwand.lib.sample

import android.os.Bundle
import com.cwand.lib.ktx.BaseTitleActivity

class MainActivity : BaseTitleActivity() {
    override fun bindLayout(): Int {
        return R.layout.activity_main
    }

    override fun initViews(savedInstanceState: Bundle?) {
    }

    override fun initListeners() {
    }

    override fun initData() {
    }

}