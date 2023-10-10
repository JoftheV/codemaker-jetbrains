/*
 * Copyright 2023 CodeMaker AI Inc. All rights reserved.
 */
package ai.codemaker.sdk.client.model;

public class Options {

    private final Modify modify;

    private final String codePath;

    private final String prompt;

    public Options(Modify modify, String codePath, String prompt) {
        this.modify = modify;
        this.codePath = codePath;
        this.prompt = prompt;
    }

    public Modify getModify() {
        return modify;
    }

    public String getCodePath() {
        return codePath;
    }

    public String getPrompt() {
        return prompt;
    }
}
