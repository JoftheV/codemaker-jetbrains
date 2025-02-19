/*
 * Copyright 2023 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.sdkv2.client.model;

public class AssistantCompletionResponse {

    private final String sessionId;

    private final String messageId;

    private final String message;

    public AssistantCompletionResponse(String sessionId, String messageId, String message) {
        this.sessionId = sessionId;
        this.messageId = messageId;
        this.message = message;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }
}
