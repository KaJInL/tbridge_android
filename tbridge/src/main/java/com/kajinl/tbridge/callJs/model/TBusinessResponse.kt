package com.kajinl.tbridge.callJs.model

import com.kajinl.tbridge.callJs.i.ITBridgeResponse

data class TBusinessResponse<T>(
    val code: Int = 20000,
    val message: String = "",
    val isSuccess: Boolean = true,
    val data: T? = null
) : ITBridgeResponse