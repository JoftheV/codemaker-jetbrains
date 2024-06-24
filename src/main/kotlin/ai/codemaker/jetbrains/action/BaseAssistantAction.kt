/*
 * Copyright 2023 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.jetbrains.action

import ai.codemaker.jetbrains.assistant.notification.AssistantNotifier
import ai.codemaker.jetbrains.assistant.view.AssistantWindowFactory
import ai.codemaker.jetbrains.psi.PsiMethod
import ai.codemaker.jetbrains.psi.PsiUtils
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager

abstract class BaseAssistantAction : AnAction() {

    abstract fun message(method: PsiMethod): String

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT) ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return
        val method = PsiUtils.getMethod(psiFile, editor.caretModel.offset) ?: return

        publisher(project).onMessage(message(method))

        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow(AssistantWindowFactory.TOOL_WINDOW_ID) ?: return
        toolWindow.activate(null)
    }

    private fun publisher(project: Project) =
        project.messageBus.syncPublisher(AssistantNotifier.ASSISTANT_TOPIC)
}