/*
 * Copyright 2023 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.sdkv2.client.model;

public class ProcessRequest {

    private final Mode mode;

    private final Language language;

    private final Input input;

    private final Options options;

    private final String path;

    public ProcessRequest(Mode mode, Language language, Input input, String path, Options options) {
        this.mode = mode;
        this.language = language;
        this.input = input;
        this.path = path;
        this.options = options;
    }

    public Mode getMode() {
        return mode;
    }

    public Language getLanguage() {
        return language;
    }

    public Input getInput() {
        return input;
    }

    public String getPath() {
        return path;
    }

    public Options getOptions() {
        return options;
    }
}
