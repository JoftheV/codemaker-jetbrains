/*
 * Copyright 2024 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.sdkv2.client.model;

import java.nio.ByteBuffer;

public class AssistantSpeechResponse {

    private final ByteBuffer audio;

    public AssistantSpeechResponse(ByteBuffer audio) {
        this.audio = audio;
    }

    public ByteBuffer getAudio() {
        return audio;
    }
}
