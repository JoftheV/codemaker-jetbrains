/*
 * Copyright 2024 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.jetbrains.insight

import com.intellij.codeInsight.codeVision.CodeVisionRelativeOrdering

class AssistantReviewCodeVisionProvider : BaseAssistantCodeVisionProvider("review", "Review") {

    override val relativeOrderings = listOf<CodeVisionRelativeOrdering>(
        CodeVisionRelativeOrdering.CodeVisionRelativeOrderingAfter(
            "ai.codemaker.codevision.assistant.explain"
        )
    )
}