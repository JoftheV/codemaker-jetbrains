/*
 * Copyright 2024 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.jetbrains.action

import ai.codemaker.jetbrains.dialog.DocumentationDialog
import ai.codemaker.jetbrains.service.CodeMakerService
import ai.codemaker.sdkv2.client.model.LanguageCode
import ai.codemaker.sdkv2.client.model.Modify
import ai.codemaker.sdkv2.client.model.Visibility
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiDocumentManager

class CustomizeDocumentationAction : BaseAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val dialog = DocumentationDialog()
        if (dialog.showAndGet()) {
            val modify = dialog.getModify()
            val language = dialog.getLanguage()
            val overrideIndent = dialog.getOverrideIndent()
            val minimalLinesLength = dialog.getMinimalLinesLength()
            val visibility = dialog.getVisibility()
            generateDocumentation(e, modify, language, overrideIndent, minimalLinesLength, visibility)
        }
    }

    private fun generateDocumentation(
        e: AnActionEvent,
        modify: Modify,
        language: LanguageCode?,
        overrideIndent: Int?,
        minimalLinesLength: Int?,
        visibility: Visibility?
    ) {
        val project = e.getData(CommonDataKeys.PROJECT) ?: return

        val service: CodeMakerService = project.getService(CodeMakerService::class.java)

        val editor = e.getData(CommonDataKeys.EDITOR)
        if (editor != null) {
            val documentManager = PsiDocumentManager.getInstance(project)
            val psiFile = documentManager.getPsiFile(editor.document) ?: return
            documentManager.commitDocument(editor.document)
            service.generateDocumentation(
                psiFile.virtualFile,
                modify,
                null,
                language,
                overrideIndent,
                minimalLinesLength,
                visibility
            )
        } else {
            val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
            service.generateDocumentation(file, Modify.NONE, null, language, overrideIndent, minimalLinesLength, visibility)
        }
    }
}