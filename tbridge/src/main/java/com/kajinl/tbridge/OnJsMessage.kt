package com.kajinl.tbridge

import android.content.Context

/**
 * JS 消息处理接口
 * 通过匿名内部类实现，处理来自 JS 的调用
 */
interface OnJsMessage {
    /**
     * 处理 JS 调用
     * @param method 方法名
     * @param params 参数字符串（JSON 格式）
     * @param callbackId 回调 ID，如果有值，JSBridge 会自动处理回调
     * @param context 上下文
     * @param callback 回调接口，用于返回结果
     */
    fun onJsMessage(
        method: String,
        params: String?,
        callbackId: String?,
        context: Context,
        callback: JsCallback
    )
}

/**
 * JS 回调接口
 * 用于返回处理结果，如果有 callbackId，JSBridge 会自动处理
 * 只传递业务数据（params），不包含业务字段（code, message, isSuccess）
 */
interface JsCallback {
    /**
     * 成功回调
     * @param params 返回的业务数据（任意类型）
     */
    fun onSuccess(params: Any?)
    
    /**
     * 失败回调
     * @param params 错误信息（任意类型，业务层自己定义结构）
     */
    fun onError(params: Any?)
}

