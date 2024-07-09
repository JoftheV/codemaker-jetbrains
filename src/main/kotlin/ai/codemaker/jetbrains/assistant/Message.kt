/*
 * Copyright 2024 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.jetbrains.assistant

import java.util.*

class Message(val sessionId: String, val messageId: String, val content: String, val role: Role, val date: Date, val autoplay: Boolean) {

}