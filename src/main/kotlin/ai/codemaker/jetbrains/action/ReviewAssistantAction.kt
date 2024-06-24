/*
 * Copyright 2024 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.jetbrains.action

import ai.codemaker.jetbrains.psi.PsiMethod

class ReviewAssistantAction : BaseAssistantAction() {

    override fun message(method: PsiMethod): String {
        return "Review ${method.elementName} method"
    }
}