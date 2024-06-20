/*
 * Copyright 2023 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.sdkv2.client.model;

public class RegisterAssistantFeedbackRequest {

    private final String sessionId;

    private final String messageId;

    private final Vote vote;

    public RegisterAssistantFeedbackRequest(String sessionId, String messageId, Vote vote) {
        this.sessionId = sessionId;
        this.messageId = messageId;
        this.vote = vote;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getMessageId() {
        return messageId;
    }

    public Vote getVote() {
        return vote;
    }
}
