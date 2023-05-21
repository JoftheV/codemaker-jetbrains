/*
 * Copyright 2023 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.jetbrains.action

import ai.codemaker.jetbrains.psi.PsiUtils
import ai.codemaker.sdk.client.model.Modify
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiFile

class ReplaceMethodCodeAction : BaseCodeAction(Modify.REPLACE) {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        val presentation = e.presentation
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return
        val element = PsiUtils.getNamedElement(psiFile, editor.caretModel.offset)

        presentation.isVisible = element != null
        if (element == null) {
            return
        }
        presentation.text = "Replace '${element.name}' code"
    }

    override fun getCodePath(psiFile: PsiFile, offset: Int): String? {
        val element = PsiUtils.getNamedElement(psiFile, offset) ?: return null
        return element.codePath
    }
}