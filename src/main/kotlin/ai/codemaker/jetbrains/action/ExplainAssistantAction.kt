/*
 * Copyright 2024 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.jetbrains.action

import ai.codemaker.jetbrains.psi.PsiMethod

class ExplainAssistantAction : BaseAssistantAction() {

    override fun message(method: PsiMethod): String {
        return "Explain ${method.elementName} method."
    }
}