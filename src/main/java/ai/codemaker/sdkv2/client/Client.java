/*
 * Copyright 2023 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.sdkv2.client;

import ai.codemaker.sdkv2.client.model.AssistantCodeCompletionRequest;
import ai.codemaker.sdkv2.client.model.AssistantCodeCompletionResponse;
import ai.codemaker.sdkv2.client.model.AssistantCompletionRequest;
import ai.codemaker.sdkv2.client.model.AssistantCompletionResponse;
import ai.codemaker.sdkv2.client.model.AssistantSpeechRequest;
import ai.codemaker.sdkv2.client.model.AssistantSpeechResponse;
import ai.codemaker.sdkv2.client.model.CompletionRequest;
import ai.codemaker.sdkv2.client.model.CompletionResponse;
import ai.codemaker.sdkv2.client.model.CreateContextRequest;
import ai.codemaker.sdkv2.client.model.CreateContextResponse;
import ai.codemaker.sdkv2.client.model.DiscoverContextRequest;
import ai.codemaker.sdkv2.client.model.DiscoverContextResponse;
import ai.codemaker.sdkv2.client.model.ListModelsRequest;
import ai.codemaker.sdkv2.client.model.ListModelsResponse;
import ai.codemaker.sdkv2.client.model.PredictRequest;
import ai.codemaker.sdkv2.client.model.PredictResponse;
import ai.codemaker.sdkv2.client.model.ProcessRequest;
import ai.codemaker.sdkv2.client.model.ProcessResponse;
import ai.codemaker.sdkv2.client.model.RegisterAssistantFeedbackRequest;
import ai.codemaker.sdkv2.client.model.RegisterAssistantFeedbackResponse;
import ai.codemaker.sdkv2.client.model.RegisterContextRequest;
import ai.codemaker.sdkv2.client.model.RegisterContextResponse;

import java.util.Iterator;
import java.util.stream.Stream;

public interface Client extends AutoCloseable {

    AssistantCompletionResponse assistantCompletion(AssistantCompletionRequest request);

    AssistantCodeCompletionResponse assistantCodeCompletion(AssistantCodeCompletionRequest request);

    AssistantSpeechResponse assistantSpeech(AssistantSpeechRequest request);

    Iterator<AssistantSpeechResponse> assistantSpeechStream(AssistantSpeechRequest request);

    RegisterAssistantFeedbackResponse registerAssistantFeedback(RegisterAssistantFeedbackRequest request);

    CompletionResponse completion(CompletionRequest request);

    ProcessResponse process(ProcessRequest request);

    PredictResponse predict(PredictRequest request);

    DiscoverContextResponse discoverContext(DiscoverContextRequest request);

    CreateContextResponse createContext(CreateContextRequest request);

    RegisterContextResponse registerContext(RegisterContextRequest request);

    ListModelsResponse listModels(ListModelsRequest request);
}
