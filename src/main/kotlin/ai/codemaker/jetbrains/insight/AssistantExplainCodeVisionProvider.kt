/*
 * Copyright 2024 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.jetbrains.insight

import com.intellij.codeInsight.codeVision.CodeVisionRelativeOrdering

class AssistantExplainCodeVisionProvider : BaseAssistantCodeVisionProvider("explain", "Explain") {

    override val relativeOrderings = emptyList<CodeVisionRelativeOrdering>()
}