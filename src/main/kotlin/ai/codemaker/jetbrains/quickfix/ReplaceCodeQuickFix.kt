/*
 * Copyright 2023 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.jetbrains.quickfix

import ai.codemaker.jetbrains.psi.PsiUtils
import ai.codemaker.jetbrains.service.CodeMakerService
import ai.codemaker.sdk.client.model.Modify
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile

class ReplaceCodeQuickFix : BaseGenerationQuickFix("Replace code") {

    override fun getCodePath(file: PsiFile, offset: Int): String? {
        val method = PsiUtils.getMethod(file, offset) ?: return null
        return method.codePath
    }

    override fun doInvoke(project: Project, editor: Editor, service: CodeMakerService, file: VirtualFile, codePath: String?) {
        ApplicationManager.getApplication().invokeLater {
            service.generateCode(file, Modify.REPLACE, codePath)
        }
    }
}