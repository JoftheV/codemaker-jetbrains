/*
 * Copyright 2024 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.jetbrains.assistant.handler

import ai.codemaker.jetbrains.service.CodeMakerService
import ai.codemaker.sdkv2.client.model.AssistantSpeechResponse
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Disposer
import com.intellij.util.queryParameters
import org.cef.callback.CefCallback
import org.cef.handler.CefResourceHandler
import org.cef.misc.IntRef
import org.cef.misc.StringRef
import org.cef.network.CefRequest
import org.cef.network.CefResponse
import java.io.IOException
import java.net.URI
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.math.min

class SpeechResourceHandler(private val service: CodeMakerService, parent: Disposable) : CefResourceHandler,
    Disposable {

    private var input: Iterator<AssistantSpeechResponse>? = null

    private var buffer: ByteBuffer? = null

    private var mimeType = "audio/mp3"

    override fun processRequest(request: CefRequest?, callback: CefCallback?): Boolean {
        val uri = URI(request!!.url)
        val message = String(Base64.getUrlDecoder().decode(uri.queryParameters.get("input")!!), StandardCharsets.UTF_8)

        input = service.assistantSpeechStream(message)
        callback!!.Continue()
        return true
    }

    override fun getResponseHeaders(response: CefResponse?, responseLength: IntRef?, redirectUrl: StringRef?) {
        responseLength!!.set(1073741824)
        response!!.status = 200
        response.mimeType = mimeType
    }

    override fun readResponse(
        dataOut: ByteArray?,
        bytesToRead: Int,
        bytesRead: IntRef?,
        callback: CefCallback?
    ): Boolean {
        try {
            if (buffer == null || !buffer!!.hasRemaining()) {
                buffer = if (input!!.hasNext()) {
                    input!!.next().audio
                } else {
                    null
                }
            }

            if (buffer != null) {
                val available = buffer!!.remaining()
                buffer!!.get(dataOut, 0, min(available, bytesToRead))
                bytesRead!!.set(available - buffer!!.remaining())
                return true
            }
        } catch (e: IOException) {
            callback!!.cancel()
        }
        bytesRead!!.set(0)
        Disposer.dispose(this)
        return false
    }

    override fun cancel() {
        Disposer.dispose(this)
    }

    override fun dispose() {
        input = null
    }
}