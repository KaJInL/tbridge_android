# TBridge - Android (Kotlin)

[![Maven Central](https://img.shields.io/maven-central/v/com.kajlee/tbridge)](https://central.sonatype.com/artifact/com.kajlee/tbridge)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](../../LICENSE)

TBridge 的 Android Kotlin 实现，为 Android WebView 提供与 JavaScript 的双向通信能力。

## 📦 安装

在 `build.gradle.kts` 中添加依赖：

```kotlin
dependencies {
    implementation("com.kajlee:tbridge:0.1.01")
}
```

## 🚀 快速开始

```kotlin
import com.kajinl.tbridge.TBridge
import com.kajinl.tbridge.OnJsMessage
import com.kajinl.tbridge.JsCallback

// 初始化
val bridge = TBridge(webView, object : OnJsMessage {
    override fun onJsMessage(
        method: String,
        params: String?,
        callbackId: String?,
        context: Context,
        callback: JsCallback
    ) {
        when (method) {
            "methodName" -> callback.onSuccess(mapOf("result" to "data"))
            else -> callback.onError(mapOf("error" to "未知方法"))
        }
    }
})

// 注入到 WebView
webView.addJavascriptInterface(bridge, bridge.getBridgeName())

// 调用 JS 方法
bridge.callJS("methodName", mapOf("key" to "value"))
```

## 📊 调用流程

```
① 接收 JS 调用：
   onJsMessage(method, params, callbackId, context, callback)
   ↓
   处理业务逻辑
   ↓
   callback.onSuccess(result)

② 调用 JS：
   bridge.callJS("method", params)
```

## 📖 核心 API

### TBridge 类

#### 构造函数

```kotlin
TBridge(webView: WebView, messageHandler: OnJsMessage)
```

创建 TBridge 实例。

**参数:**
- `webView`: WebView 实例
- `messageHandler`: JS 消息处理器

#### callJS()

```kotlin
fun callJS(method: String, params: Any?)
```

调用 JavaScript 方法。

**示例:**

```kotlin
// 传递 Map
bridge.callJS("onUserLogin", mapOf("userId" to "123"))

// 传递 List
bridge.callJS("updateList", listOf(1, 2, 3))

// 传递字符串
bridge.callJS("showMessage", "Hello")

// 无参数
bridge.callJS("refresh", null)
```

#### destroy()

```kotlin
fun destroy()
```

清理资源，防止内存泄漏。在 Activity/Fragment 销毁时调用。

```kotlin
override fun onDestroy() {
    super.onDestroy()
    bridge.destroy()
}
```

#### getBridgeName()

```kotlin
fun getBridgeName(): String
```

获取 Bridge 名称（返回 `"AndroidBridge"`）。

### OnJsMessage 接口

```kotlin
interface OnJsMessage {
    fun onJsMessage(
        method: String,
        params: String?,
        callbackId: String?,
        context: Context,
        callback: JsCallback
    )
}
```

处理来自 JavaScript 的调用。

### JsCallback 接口

```kotlin
interface JsCallback {
    fun onSuccess(params: Any?)
    fun onError(params: Any?)
}
```

用于返回结果给 JavaScript。

**示例:**

```kotlin
// 成功
callback.onSuccess(mapOf("code" to 0, "data" to result))

// 失败
callback.onError(mapOf("code" to -1, "message" to "错误信息"))
```

## 📚 完整文档

详细的使用指南、示例代码和 API 文档请查看：

- [📖 主文档](https://github.com/KaJInL/tbridge)
- [🔧 集成指南](https://github.com/KaJInL/tbridge/blob/main/packages/tbridge/docs/INTEGRATION_GUIDE.md)
- [📘 API 参考](https://github.com/KaJInL/tbridge/blob/main/packages/tbridge/docs/API_REFERENCE.md)
- [💡 示例代码](https://github.com/KaJInL/tbridge/blob/main/packages/tbridge/docs/EXAMPLES.md)

## 🔗 相关链接

- **Maven Central**: https://central.sonatype.com/artifact/com.kajlee/tbridge
- **GitHub**: https://github.com/KaJInL/tbridge_android
- **主仓库**: https://github.com/KaJInL/tbridge

## 📄 许可证

MIT License
