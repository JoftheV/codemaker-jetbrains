/*
 * Copyright 2024 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.jetbrains.action

import ai.codemaker.jetbrains.psi.PsiMethod

class UnitTestAssistantAction : BaseAssistantAction() {

    override fun message(method: PsiMethod): String {
        return "Test ${method.elementName} method."
    }
}