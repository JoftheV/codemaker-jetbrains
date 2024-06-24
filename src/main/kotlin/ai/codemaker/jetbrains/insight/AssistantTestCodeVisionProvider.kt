/*
 * Copyright 2024 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.jetbrains.insight

import ai.codemaker.jetbrains.assistant.notification.AssistantNotifier
import ai.codemaker.jetbrains.assistant.publisher.AssistantMessagePublisher
import ai.codemaker.jetbrains.file.FileExtensions
import ai.codemaker.jetbrains.psi.PsiMethod
import ai.codemaker.jetbrains.settings.AppSettingsState
import com.intellij.codeInsight.codeVision.CodeVisionRelativeOrdering
import com.intellij.codeInsight.hints.codeVision.CodeVisionProviderBase
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNameIdentifierOwner
import java.awt.event.MouseEvent

class AssistantTestCodeVisionProvider : BaseAssistantCodeVisionProvider("test", "Test") {

    override val relativeOrderings = listOf<CodeVisionRelativeOrdering>(
        CodeVisionRelativeOrdering.CodeVisionRelativeOrderingAfter(
            "ai.codemaker.codevision.assistant.review"
        )
    )

    override fun getHint(element: PsiElement, file: PsiFile): String? {
        return "Unit Test"
    }
}