/*
 * Copyright 2024 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.sdkv2.client.model;

public class AssistantSpeechRequest {

    private final String message;

    public AssistantSpeechRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
