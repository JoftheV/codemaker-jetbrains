/*
 * Copyright 2024 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.jetbrains.group

import ai.codemaker.jetbrains.psi.PsiUtils
import ai.codemaker.jetbrains.settings.AppSettingsState
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DefaultActionGroup

class AssistantGroup : DefaultActionGroup(){

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        val presentation = e.presentation

        if (!AppSettingsState.instance.assistantActionsEnabled) {
            presentation.isVisible = false
        } else {
            val editor = e.getData(CommonDataKeys.EDITOR) ?: return
            val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return
            val method = PsiUtils.getMethod(psiFile, editor.caretModel.offset)

            presentation.isVisible = method != null
        }
    }
}