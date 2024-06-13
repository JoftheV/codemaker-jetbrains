/*
 * Copyright 2024 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.jetbrains.client

import ai.codemaker.jetbrains.settings.AppSettingsState
import ai.codemaker.sdkv2.client.Client
import ai.codemaker.sdkv2.client.Config
import ai.codemaker.sdkv2.client.DefaultClient
import com.jetbrains.rd.util.AtomicReference

class ClientManager {

    private val defaultMaxRetries = 10

    private val client = AtomicReference<Holder?>(null)

    class Holder(val endpoint: String?, val client: Client) {

    }

    fun getClient(endpoint: String?): Client {
        val instance = client.get()
        if (instance == null || instance.endpoint != endpoint) {
            instance?.client?.close()
            client.getAndSet(Holder(endpoint, createClient(endpoint)))
        }
        return client.get()!!.client
    }

    private fun createClient(endpoint: String?): Client {
        val builder = Config.builder()
        if (!endpoint.isNullOrEmpty()) {
            builder.withEndpoint(endpoint)
        }
        builder.withMaxRetries(defaultMaxRetries)
        return DefaultClient({ AppSettingsState.instance.apiKey }, builder.build())
    }
}