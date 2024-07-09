/*
 * Copyright 2024 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.jetbrains.assistant.server

import ai.codemaker.jetbrains.service.CodeMakerService
import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import io.undertow.Undertow
import io.undertow.io.IoCallback
import io.undertow.io.Sender
import io.undertow.server.HttpServerExchange
import io.undertow.util.Headers
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*

class SpeechServer(val service: CodeMakerService, parent: Disposable) : Disposable {

    private var server: Undertow? = null

    init {
        Disposer.register(parent, this)
    }

    fun start() {
        server = Undertow.builder()
            .addHttpListener(53200, "localhost")
            .setHandler { exchange ->
                exchange.responseHeaders.put(Headers.CONTENT_TYPE, "audio/mp3")

                val message = String(
                    Base64.getUrlDecoder().decode(exchange.queryParameters["input"]!!.first),
                    StandardCharsets.UTF_8
                )
                val iter = service.assistantSpeechStream(message)

                while (iter.hasNext()) {
                    exchange.responseSender.send(iter.next().audio, object: IoCallback {
                        override fun onComplete(exchange: HttpServerExchange?, sender: Sender?) {
                            // ignores
                        }

                        override fun onException(
                            exchange: HttpServerExchange?,
                            sender: Sender?,
                            exception: IOException?
                        ) {
                            // ignores
                        }
                    })
                }

                exchange.responseSender.close()
            }
            .build()
        server!!.start()
    }

    fun stop() {
        Disposer.dispose(this)
    }

    override fun dispose() {
        server?.stop()
    }
}