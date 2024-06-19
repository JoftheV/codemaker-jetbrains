/*
 * Copyright 2024 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.jetbrains.window

import com.intellij.openapi.util.io.toNioPath
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.callback.CefCallback
import org.cef.handler.*
import org.cef.misc.BoolRef
import org.cef.network.CefRequest
import java.net.URL

private typealias CefResourceProvider = () -> CefResourceHandler?

class FileResourceProvider() : CefRequestHandlerAdapter() {

    private val protocol = "file"

    private val resources = HashMap<String, CefResourceProvider>()

    private val REJECTING_RESOURCE_HANDLER: CefResourceHandler = object : CefResourceHandlerAdapter() {
        override fun processRequest(request: CefRequest, callback: CefCallback): Boolean {
            callback.cancel()
            return false
        }
    }

    private val RESOURCE_REQUEST_HANDLER = object : CefResourceRequestHandlerAdapter() {
        override fun getResourceHandler(
            browser: CefBrowser?,
            frame: CefFrame?,
            request: CefRequest
        ): CefResourceHandler? {
            val url = URL(request.url)
            if (protocol != url.protocol) {
                return REJECTING_RESOURCE_HANDLER
            }

            return resolveHandler(url)
        }
    }

    fun addResource(path: String, resourceProvider: CefResourceProvider) {
        resources[path] = resourceProvider
    }

    override fun getResourceRequestHandler(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?,
        isNavigation: Boolean,
        isDownload: Boolean,
        requestInitiator: String?,
        disableDefaultHandling: BoolRef?
    ): CefResourceRequestHandler {
        return RESOURCE_REQUEST_HANDLER
    }

    private fun resolveHandler(url: URL): CefResourceHandler? {
        var path = url.path.toNioPath()
        while (path != null) {
            val handler = resources[path.toString()]
            if (handler != null) {
                return handler()
            }
            path = path.parent
        }
        return null
    }
}