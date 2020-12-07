package com.cwand.lib.ktx.entity

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

data class MenuEntity(
    val title: CharSequence = "",
    @DrawableRes val iconId: Int = -1,
    @ColorInt val titleColor: Int = -1,
    val titleSize: Int = -1
)