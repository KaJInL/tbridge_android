package com.kajinl.tbridge.callJs.model

/**
 * JS Bridge 消息结构（核心层）
 */
data class TBridgeMessage(
    val method: String,
    val callbackId: String?,
    val params: Any?  // 业务数据，由业务层决定结构
)