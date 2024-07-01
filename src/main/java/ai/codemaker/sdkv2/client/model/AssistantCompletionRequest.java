/*
 * Copyright 2023 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.sdkv2.client.model;

public class AssistantCompletionRequest {

    private final String message;

    private final Options options;

    public AssistantCompletionRequest(String message, Options options) {
        this.message = message;
        this.options = options;
    }

    public String getMessage() {
        return message;
    }

    public Options getOptions() {
        return options;
    }
}
