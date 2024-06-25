/*
 * Copyright 2024 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.jetbrains.insight

import com.intellij.codeInsight.codeVision.CodeVisionRelativeOrdering
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

class AssistantFindBugsCodeVisionProvider : BaseAssistantCodeVisionProvider("bugs", "Find Bugs", "Find errors in") {

    override val relativeOrderings = listOf<CodeVisionRelativeOrdering>(
        CodeVisionRelativeOrdering.CodeVisionRelativeOrderingAfter(
            "ai.codemaker.codevision.assistant.test"
        )
    )
}