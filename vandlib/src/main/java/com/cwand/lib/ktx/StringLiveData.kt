package com.cwand.lib.ktx

import androidx.lifecycle.MutableLiveData

class StringLiveData : MutableLiveData<String>() {

    override fun getValue(): String {
        return super.getValue() ?: ""
    }
}