package com.kajinl.tbridge

import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.util.Log
import com.kajinl.tbridge.callJs.CallJsHandler
import java.lang.ref.WeakReference

/**
 * JSBridge 类，用于 Android 和 JavaScript 之间的通信
 * 核心层：只处理 JsBridgeMessage 结构（method, callbackId, params）
 * 不处理业务字段（code, message, isSuccess 等）
 */
class TBridge(
    webView: WebView,
    private val messageHandler: OnJsMessage
) {
    companion object {
        private const val TAG = "JSBridge"
        private const val BRIDGE_NAME = "AndroidBridge"
    }

    // 使用弱引用持有 WebView，防止内存泄漏
    private val webViewRef = WeakReference(webView)

    /**
     * JS 调用 Android 方法
     */
    @JavascriptInterface
    fun callNative(method: String, params: String?, callbackId: String?) {
        val webView = webViewRef.get()
        if (webView == null) {
            Log.w(TAG, "WebView has been garbage collected, ignoring callNative: $method")
            return
        }

        Log.d(TAG, "callNative: method=$method, params=$params, callbackId=$callbackId")
        
        // 创建回调包装器，自动处理 callbackId
        val callback = object : JsCallback {
            override fun onSuccess(params: Any?) {
                // 检查 WebView 是否还有效
                val currentWebView = webViewRef.get()
                val currentHandler = currentWebView?.let { CallJsHandler(it) }
                
                // 如果有 callbackId，自动调用 JS 回调
                // 核心层只传递 params，不处理业务字段
                callbackId?.let {
                    currentHandler?.callJS(
                        "onNativeCallback",
                        params,
                        callbackId
                    ) ?: Log.w(TAG, "Cannot call JS callback: WebView is null")
                }
            }

            override fun onError(params: Any?) {
                // 检查 WebView 是否还有效
                val currentWebView = webViewRef.get()
                val currentHandler = currentWebView?.let { CallJsHandler(it) }
                
                // 如果有 callbackId，自动调用 JS 错误回调
                // 核心层只传递 params，不处理业务字段
                callbackId?.let {
                    currentHandler?.callJS(
                        "onNativeCallback",
                        params,
                        callbackId
                    ) ?: Log.w(TAG, "Cannot call JS error callback: WebView is null")
                }
            }
        }

        try {
            messageHandler.onJsMessage(method, params, callbackId, webView.context, callback)
        } catch (e: Exception) {
            Log.e(TAG, "Error handling JS message: $method", e)
            // 错误时传递错误信息，由业务层决定结构
            callback.onError(mapOf("error" to (e.message ?: "Unknown error")))
        }
    }

    /**
     * Android 调用 JS 方法
     * @param method 方法名
     * @param params 业务数据（任意类型，由业务层决定）
     */
    fun callJS(method: String, params: Any?) {
        val webView = webViewRef.get()
        if (webView != null) {
            CallJsHandler(webView).callJS(method, params, null)
        } else {
            Log.w(TAG, "Cannot call JS: WebView has been garbage collected")
        }
    }
    
    /**
     * 清理资源，防止内存泄漏
     * 应该在 WebView 销毁时调用
     */
    fun destroy() {
        webViewRef.clear()
    }

    /**
     * 获取 Bridge 名称
     */
    fun getBridgeName(): String = BRIDGE_NAME
}
