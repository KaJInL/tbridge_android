package com.kajinl.tbridge.callJs

import android.webkit.WebView
import com.google.gson.Gson
import com.kajinl.tbridge.callJs.model.TBridgeMessage

/**
 * 调用 JS 的处理器
 * 核心层只处理 JsBridgeMessage 结构（method, callbackId, params）
 * 不处理业务字段（code, message, isSuccess）
 */
class CallJsHandler(private val webView: WebView) {

    private val gson = Gson()

    /**
     * Android 调用 JS
     * @param method JS 要执行的逻辑名
     * @param params 传递的业务参数（任意类型，由业务层决定）
     * @param callbackId 若有值，则表示希望 JS 执行完后回调
     */
    fun callJS(method: String, params: Any?, callbackId: String? = null) {
        val message = TBridgeMessage(
            method = method,
            callbackId = callbackId,
            params = params
        )

        val json = gson.toJson(message)
        val jsCode = if (callbackId.isNullOrBlank()) {
            "window.TBridge?.onCallFromNative(JSON.parse('${json.replace("'", "\\'")}'))"
        } else {
            "window.TBridge?.onNativeCallback(JSON.parse('${json.replace("'", "\\'")}'))"
        }

        webView.post {
            webView.evaluateJavascript(jsCode, null)
        }
    }
}