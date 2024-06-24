/*
 * Copyright 2024 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.jetbrains.assistant.notification

import com.intellij.util.messages.Topic
import com.intellij.util.messages.Topic.ProjectLevel


interface AssistantNotifier {

    companion object {
        @ProjectLevel
        val ASSISTANT_TOPIC: Topic<AssistantNotifier> =
            Topic.create("ai.codemaker.assistant.topic", AssistantNotifier::class.java)
    }

    fun onMessage(message: String)
}