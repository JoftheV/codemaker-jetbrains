/*
 * Copyright 2024 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.jetbrains.assistant.publisher

import ai.codemaker.jetbrains.assistant.notification.AssistantNotifier
import ai.codemaker.jetbrains.assistant.view.AssistantWindowFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager

object AssistantMessagePublisher {

    fun publishMessage(project: Project, message: String) {
        project.messageBus.syncPublisher(AssistantNotifier.ASSISTANT_TOPIC).onMessage(message)

        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow(AssistantWindowFactory.TOOL_WINDOW_ID) ?: return
        toolWindow.activate(null)
    }
}