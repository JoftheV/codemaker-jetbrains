/*
 * Copyright 2023 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.sdkv2.client.model;

public class AssistantCodeCompletionResponse {

    private final String sessionId;

    private final String messageId;

    private final String message;

    private final Output output;

    public AssistantCodeCompletionResponse(String sessionId, String messageId, String message, Output output) {
        this.sessionId = sessionId;
        this.messageId = messageId;
        this.message = message;
        this.output = output;
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

    public Output getOutput() {
        return output;
    }
}
