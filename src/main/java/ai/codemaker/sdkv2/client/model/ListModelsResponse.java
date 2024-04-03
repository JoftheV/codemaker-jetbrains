/*
 * Copyright 2023 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.sdkv2.client.model;

import java.util.List;

public class ListModelsResponse {

    private final List<Model> models;

    public ListModelsResponse(List<Model> models) {
        this.models = models;
    }

    public List<Model> getModels() {
        return models;
    }
}
