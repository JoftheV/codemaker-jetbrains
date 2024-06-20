/*
 * Copyright 2023 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.sdkv2.client;

import ai.codemaker.sdkv2.client.model.AssistantCodeCompletionRequest;
import ai.codemaker.sdkv2.client.model.AssistantCodeCompletionResponse;
import ai.codemaker.sdkv2.client.model.AssistantCompletionRequest;
import ai.codemaker.sdkv2.client.model.AssistantCompletionResponse;
import ai.codemaker.sdkv2.client.model.CompletionRequest;
import ai.codemaker.sdkv2.client.model.CompletionResponse;
import ai.codemaker.sdkv2.client.model.CreateContextRequest;
import ai.codemaker.sdkv2.client.model.CreateContextResponse;
import ai.codemaker.sdkv2.client.model.DiscoverContextRequest;
import ai.codemaker.sdkv2.client.model.DiscoverContextResponse;
import ai.codemaker.sdkv2.client.model.Input;
import ai.codemaker.sdkv2.client.model.Language;
import ai.codemaker.sdkv2.client.model.ListModelsRequest;
import ai.codemaker.sdkv2.client.model.ListModelsResponse;
import ai.codemaker.sdkv2.client.model.Mode;
import ai.codemaker.sdkv2.client.model.Model;
import ai.codemaker.sdkv2.client.model.Modify;
import ai.codemaker.sdkv2.client.model.Options;
import ai.codemaker.sdkv2.client.model.Output;
import ai.codemaker.sdkv2.client.model.PredictRequest;
import ai.codemaker.sdkv2.client.model.PredictResponse;
import ai.codemaker.sdkv2.client.model.ProcessRequest;
import ai.codemaker.sdkv2.client.model.ProcessResponse;
import ai.codemaker.sdkv2.client.model.RegisterAssistantFeedbackRequest;
import ai.codemaker.sdkv2.client.model.RegisterAssistantFeedbackResponse;
import ai.codemaker.sdkv2.client.model.RegisterContextRequest;
import ai.codemaker.sdkv2.client.model.RegisterContextResponse;
import ai.codemaker.sdkv2.client.model.RequiredContext;
import ai.codemaker.sdkv2.client.model.Visibility;
import ai.codemaker.sdkv2.client.model.Vote;
import ai.codemaker.service.CodemakerServiceGrpc;
import ai.codemaker.service.Codemakerai;
import com.google.common.hash.Hashing;
import com.google.protobuf.ByteString;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static com.google.common.base.Preconditions.checkNotNull;

public final class DefaultClient implements Client {

    private static final Logger logger = LoggerFactory.getLogger(DefaultClient.class);

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private static final int DEFAULT_TIMEOUT_IN_MILLIS = 120000;

    private final ManagedChannel channel;

    private final CodemakerServiceGrpc.CodemakerServiceBlockingStub client;

    private final Config config;

    private final AtomicBoolean isShutdown = new AtomicBoolean(false);

    public DefaultClient(ApiKeyProvider apiKeyProvider) {
        this(apiKeyProvider, Config.create());
    }

    public DefaultClient(ApiKeyProvider apiKeyProvider, Config config) {
        checkNotNull(apiKeyProvider, "apiKeyProvider");
        checkNotNull(config, "config");

        this.config = config;

        this.channel = createChannel(config);
        this.client = CodemakerServiceGrpc.newBlockingStub(
                ClientInterceptors.intercept(
                        channel,
                        new AuthenticationInterceptor(apiKeyProvider)
                )
        );
    }

    @Override
    public AssistantCompletionResponse assistantCompletion(AssistantCompletionRequest request) {
        final Codemakerai.AssistantCompletionRequest assistantCompletionRequest = createAssistantCompletionRequest(request);

        final Codemakerai.AssistantCompletionResponse assistantCompletionResponse = doAssistantCompletion(assistantCompletionRequest);

        return createAssistantCompletionResponse(assistantCompletionResponse);
    }

    @Override
    public AssistantCodeCompletionResponse assistantCodeCompletion(AssistantCodeCompletionRequest request) {
        final Codemakerai.AssistantCodeCompletionRequest assistantCodeCompletionRequest = createAssistantCodeCompletionRequest(request);

        final Codemakerai.AssistantCodeCompletionResponse assistantCodeCompletionResponse = doAssistantCodeCompletion(assistantCodeCompletionRequest);

        return createAssistantCodeCompletionResponse(assistantCodeCompletionResponse);
    }

    @Override
    public RegisterAssistantFeedbackResponse registerAssistantFeedback(RegisterAssistantFeedbackRequest request) {
        final Codemakerai.RegisterAssistantFeedbackRequest registerAssistantFeedbackRequest = createRegisterAssistantFeedbackRequest(request);

        final Codemakerai.RegisterAssistantFeedbackResponse registerAssistantFeedbackResponse = doRegisterAssistantFeedback(registerAssistantFeedbackRequest);

        return createRegisterAssistantFeedbackResponse(registerAssistantFeedbackResponse);
    }

    @Override
    public CompletionResponse completion(CompletionRequest request) {
        final Codemakerai.CompletionRequest completionRequest = createCompletionRequest(request);

        final Codemakerai.CompletionResponse completionResponse = doCompletion(completionRequest);

        return createCompletionResponse(completionResponse);
    }

    @Override
    public ProcessResponse process(ProcessRequest request) {
        final Codemakerai.ProcessRequest processRequest = createProcessRequest(request);

        final Codemakerai.ProcessResponse processResponse = doProcess(processRequest);

        return createProcessResponse(processResponse);
    }

    @Override
    public PredictResponse predict(PredictRequest request) {
        final Codemakerai.PredictRequest predictRequest = createPredictRequest(request);

        final Codemakerai.PredictResponse predictResponse = doPredict(predictRequest);

        return createPredictResponse(predictResponse);
    }

    @Override
    public DiscoverContextResponse discoverContext(DiscoverContextRequest request) {
        final Codemakerai.DiscoverSourceContextRequest discoverContextRequest = createDiscoverContextRequest(request);

        final Codemakerai.DiscoverSourceContextResponse discoverContextResponse = doDiscoverContext(discoverContextRequest);

        return createDiscoverContextResponse(discoverContextResponse);
    }

    @Override
    public CreateContextResponse createContext(CreateContextRequest request) {
        final Codemakerai.CreateSourceContextRequest createContextRequest = createCreateContextRequest(request);

        final Codemakerai.CreateSourceContextResponse createContextResponse = doCreateContext(createContextRequest);

        return createCreateContextResponse(createContextResponse);
    }

    @Override
    public RegisterContextResponse registerContext(RegisterContextRequest request) {
        final Codemakerai.RegisterSourceContextRequest registerContextRequest = createRegisterContextRequest(request);

        final Codemakerai.RegisterSourceContextResponse registerContextResponse = doRegisterContext(registerContextRequest);

        return createRegisterContextResponse(registerContextResponse);
    }

    @Override
    public ListModelsResponse listModels(ListModelsRequest request) {
        final Codemakerai.ListModelsRequest listModelsRequest = createListModelsRequest(request);

        final Codemakerai.ListModelsResponse listModelsResponse = doListModels(listModelsRequest);

        return createListModelsResponse(listModelsResponse);
    }

    @Override
    public void close() throws Exception {
        if (isShutdown.compareAndSet(false, true)) {
            this.channel.shutdown();
        }
    }

    private Codemakerai.DiscoverSourceContextResponse doDiscoverContext(Codemakerai.DiscoverSourceContextRequest request) {
        return doCall(client::discoverContext, request);
    }

    private Codemakerai.CreateSourceContextResponse doCreateContext(Codemakerai.CreateSourceContextRequest request) {
        return doCall(client::createContext, request);
    }

    private Codemakerai.RegisterSourceContextResponse doRegisterContext(Codemakerai.RegisterSourceContextRequest request) {
        return doCall(client::registerContext, request);
    }

    private Codemakerai.AssistantCompletionResponse doAssistantCompletion(Codemakerai.AssistantCompletionRequest request) {
        return doCall(client::assistantCompletion, request);
    }

    private Codemakerai.AssistantCodeCompletionResponse doAssistantCodeCompletion(Codemakerai.AssistantCodeCompletionRequest request) {
        return doCall(client::assistantCodeCompletion, request);
    }

    private Codemakerai.RegisterAssistantFeedbackResponse doRegisterAssistantFeedback(Codemakerai.RegisterAssistantFeedbackRequest request) {
        return doCall(client::registerAssistantFeedback, request);
    }

    private Codemakerai.CompletionResponse doCompletion(Codemakerai.CompletionRequest request) {
        return doCall(client::completion, request);
    }

    private Codemakerai.ProcessResponse doProcess(Codemakerai.ProcessRequest request) {
        return doCall(client::process, request);
    }

    private Codemakerai.PredictResponse doPredict(Codemakerai.PredictRequest request) {
        return doCall(client::predict, request);
    }

    private Codemakerai.ListModelsResponse doListModels(Codemakerai.ListModelsRequest request) {
        return doCall(client::listModels, request);
    }

    private <TResp, TReq> TResp doCall(Function<TReq, TResp> operation, TReq request) {
        Exception exception;
        int retry = 0;

        do {
            try {
                return operation.apply(request);
            } catch (StatusRuntimeException e) {
                logger.error("Error calling service {} {}", e.getStatus().getCode(), e.getStatus().getDescription(), e);
                if (e.getStatus().getCode() == Status.Code.PERMISSION_DENIED) {
                    throw new UnauthorizedException("Unauthorized request.");
                } else if (e.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
                    exception = new ClientException("Operation Timeout", e);
                } else {
                    throw new ClientException("Error invoking CodeMaker AI API", e);
                }
            }
        } while(++retry < config.getMaxRetries());

        throw new ClientException("Error invoking CodeMaker AI API.", exception);
    }

    private CodemakerServiceGrpc.CodemakerServiceBlockingStub client() {
        return client.withDeadlineAfter(DEFAULT_TIMEOUT_IN_MILLIS, TimeUnit.MILLISECONDS);
    }

    private Codemakerai.DiscoverSourceContextRequest createDiscoverContextRequest(DiscoverContextRequest request) {
        final Codemakerai.Input input = createInput(request.getContext().getInput());

        return Codemakerai.DiscoverSourceContextRequest.newBuilder()
                .setContext(Codemakerai.SourceContext.newBuilder()
                        .setLanguage(mapLanguage(request.getContext().getLanguage()))
                        .setInput(input)
                        .setMetadata(Codemakerai.Metadata.newBuilder()
                                .setPath(request.getContext().getPath())
                                .build())
                        .build())
                .build();
    }

    private DiscoverContextResponse createDiscoverContextResponse(Codemakerai.DiscoverSourceContextResponse response) {
        final Collection<RequiredContext> requiredContexts = response.getContextsList().stream()
                .map(this::mapRequiredContext)
                .toList();
        return new DiscoverContextResponse(requiredContexts, response.getRequiresProcessing());
    }

    private Codemakerai.CreateSourceContextRequest createCreateContextRequest(CreateContextRequest request) {
        return Codemakerai.CreateSourceContextRequest.newBuilder().build();
    }

    private CreateContextResponse createCreateContextResponse(Codemakerai.CreateSourceContextResponse response) {
        return new CreateContextResponse(response.getId());
    }

    private Codemakerai.RegisterSourceContextRequest createRegisterContextRequest(RegisterContextRequest request) {
        final List<Codemakerai.SourceContext> sourceContexts = request.getContexts().stream()
                .map(context -> {
                            final Codemakerai.Input input = createInput(context.getInput());
                            return Codemakerai.SourceContext.newBuilder()
                                    .setLanguage(mapLanguage(context.getLanguage()))
                                    .setInput(input)
                                    .setMetadata(Codemakerai.Metadata.newBuilder()
                                            .setPath(context.getPath())
                                            .build())
                                    .build();
                        }
                )
                .toList();
        return Codemakerai.RegisterSourceContextRequest.newBuilder()
                .setId(request.getId())
                .addAllSourceContexts(sourceContexts)
                .build();
    }

    private RegisterContextResponse createRegisterContextResponse(Codemakerai.RegisterSourceContextResponse response) {
        return new RegisterContextResponse();
    }

    private Codemakerai.AssistantCompletionRequest createAssistantCompletionRequest(AssistantCompletionRequest request) {
        return Codemakerai.AssistantCompletionRequest.newBuilder()
                .setMessage(request.getMessage())
                .build();
    }

    private AssistantCompletionResponse createAssistantCompletionResponse(Codemakerai.AssistantCompletionResponse response) {
        return new AssistantCompletionResponse(response.getSessionId(), response.getMessageId(), response.getMessage());
    }

    private Codemakerai.AssistantCodeCompletionRequest createAssistantCodeCompletionRequest(AssistantCodeCompletionRequest request) {
        final Codemakerai.Input input = createInput(request.getInput());

        return Codemakerai.AssistantCodeCompletionRequest.newBuilder()
                .setMessage(request.getMessage())
                .setLanguage(mapLanguage(request.getLanguage()))
                .setInput(input)
                .setOptions(createAssistantCodeCompletionOptions(request.getOptions()))
                .build();
    }

    private Codemakerai.RegisterAssistantFeedbackRequest createRegisterAssistantFeedbackRequest(RegisterAssistantFeedbackRequest request) {
        return Codemakerai.RegisterAssistantFeedbackRequest.newBuilder()
                .setSessionId(request.getSessionId())
                .setMessageId(request.getMessageId())
                .setVote(mapVote(request.getVote()))
                .build();
    }

    private RegisterAssistantFeedbackResponse createRegisterAssistantFeedbackResponse(Codemakerai.RegisterAssistantFeedbackResponse registerAssistantFeedbackResponse) {
        return new RegisterAssistantFeedbackResponse();
    }

    private AssistantCodeCompletionResponse createAssistantCodeCompletionResponse(Codemakerai.AssistantCodeCompletionResponse response) {
        final Codemakerai.Source content = response.getOutput().getSource();
        final String output = decodeOutput(content);

        return new AssistantCodeCompletionResponse(response.getSessionId(), response.getMessageId(), response.getMessage(), new Output(output));
    }

    private Codemakerai.CompletionRequest createCompletionRequest(CompletionRequest request) {
        final Codemakerai.Input input = createInput(request.getInput());

        return Codemakerai.CompletionRequest.newBuilder()
                .setLanguage(mapLanguage(request.getLanguage()))
                .setInput(input)
                .setOptions(createCompletionOptions(request.getOptions()))
                .build();
    }

    private CompletionResponse createCompletionResponse(Codemakerai.CompletionResponse response) {
        final Codemakerai.Source content = response.getOutput().getSource();
        final String output = decodeOutput(content);

        return new CompletionResponse(new Output(output));
    }

    private Codemakerai.ProcessRequest createProcessRequest(ProcessRequest request) {
        final Codemakerai.Input input = createInput(request.getInput());

        return Codemakerai.ProcessRequest.newBuilder()
                .setMode(mapMode(request.getMode()))
                .setLanguage(mapLanguage(request.getLanguage()))
                .setInput(input)
                .setOptions(createProcessOptions(request.getOptions()))
                .build();
    }

    private ProcessResponse createProcessResponse(Codemakerai.ProcessResponse response) {
        final Codemakerai.Source content = response.getOutput().getSource();
        final String output = decodeOutput(content);

        return new ProcessResponse(new Output(output));
    }

    private Codemakerai.PredictRequest createPredictRequest(PredictRequest request) {
        final Codemakerai.Input input = createInput(request.getInput());

        return Codemakerai.PredictRequest.newBuilder()
                .setLanguage(mapLanguage(request.getLanguage()))
                .setInput(input)
                .setOptions(createPredictOptions(request.getOptions()))
                .build();
    }

    private Codemakerai.ListModelsRequest createListModelsRequest(ListModelsRequest request) {
        return Codemakerai.ListModelsRequest.newBuilder()
                .build();
    }

    private ListModelsResponse createListModelsResponse(Codemakerai.ListModelsResponse listModelsResponse) {
        final List<Model> models = listModelsResponse.getModelsList().stream()
                .map(value -> new Model(value.getId(), value.getName()))
                .toList();

        return new ListModelsResponse(models);
    }

    private Codemakerai.Input createInput(Input request) {
        return Codemakerai.Input.newBuilder()
                .setSource(encodeInput(request))
                .build();
    }

    private RequiredContext mapRequiredContext(Codemakerai.RequiredSourceContext requiredContext) {
        return new RequiredContext(requiredContext.getPath());
    }

    private PredictResponse createPredictResponse(Codemakerai.PredictResponse response) {
        return new PredictResponse();
    }

    private Codemakerai.PredictionOptions createPredictOptions(Options options) {
        final Codemakerai.PredictionOptions.Builder builder = Codemakerai.PredictionOptions.newBuilder();

        final Optional<String> contextId = Optional.ofNullable(options.getContextId());
        final Optional<String> model = Optional.ofNullable(options.getModel());

        contextId.ifPresent(builder::setContextId);
        model.ifPresent(builder::setModel);

        return builder.build();
    }

    private static Codemakerai.ProcessOptions createProcessOptions(Options options) {
        final Codemakerai.ProcessOptions.Builder builder = Codemakerai.ProcessOptions
                .newBuilder();

        final Optional<Modify> modify = Optional.ofNullable(options.getModify());
        final Optional<String> codePath = Optional.ofNullable(options.getCodePath());
        final Optional<String> prompt = Optional.ofNullable(options.getPrompt());
        final Optional<String> contextId = Optional.ofNullable(options.getContextId());
        final Optional<String> model = Optional.ofNullable(options.getModel());
        final Optional<Integer> overrideIndent = Optional.ofNullable(options.getOverrideIndent());
        final Optional<Integer> minimalLinesLength = Optional.ofNullable(options.getMinimalLinesLength());
        final Optional<Visibility> visibility = Optional.ofNullable(options.getVisibility());

        modify.ifPresent(value -> builder.setModify(mapModify(value)));
        codePath.ifPresent(builder::setCodePath);
        prompt.ifPresent(builder::setPrompt);
        contextId.ifPresent(builder::setContextId);
        model.ifPresent(builder::setModel);
        overrideIndent.ifPresent(builder::setOverrideIndent);
        minimalLinesLength.ifPresent(builder::setMinimalLinesLength);
        visibility.ifPresent(value -> builder.setVisibility(mapVisibility(value)));

        builder.setDetectSyntaxErrors(options.isDetectSyntaxErrors());

        return builder.build();
    }

    private static Codemakerai.CompletionOptions createCompletionOptions(Options options) {
        final Codemakerai.CompletionOptions.Builder builder = Codemakerai.CompletionOptions.newBuilder();

        final Optional<String> codePath = Optional.ofNullable(options.getCodePath());
        final Optional<String> contextId = Optional.ofNullable(options.getContextId());
        final Optional<String> model = Optional.ofNullable(options.getModel());

        codePath.ifPresent(builder::setCodePath);
        contextId.ifPresent(builder::setContextId);
        model.ifPresent(builder::setModel);

        builder.setAllowMultiLineAutocomplete(options.isAllowMultiLineAutocomplete());

        return builder.build();
    }

    private Codemakerai.AssistantCodeCompletionOptions createAssistantCodeCompletionOptions(Options options) {
        final Codemakerai.AssistantCodeCompletionOptions.Builder builder = Codemakerai.AssistantCodeCompletionOptions.newBuilder();

        final Optional<String> contextId = Optional.ofNullable(options.getContextId());
        final Optional<String> model = Optional.ofNullable(options.getModel());

        contextId.ifPresent(builder::setContextId);
        model.ifPresent(builder::setModel);

        return builder.build();
    }

    private String decodeOutput(Codemakerai.Source source) {
        final ByteString content = source.getContent();
        ByteBuffer output = ByteBuffer.allocate(content.size());
        content.copyTo(output);
        output.flip();

        if (source.getEncoding() == Codemakerai.Encoding.GZIP) {
            output = decompress(output);
        }

        return DEFAULT_CHARSET.decode(output).toString();
    }

    private Codemakerai.Source.Builder encodeInput(Input input) {
        Codemakerai.Encoding encoding = Codemakerai.Encoding.NONE;
        ByteBuffer content = DEFAULT_CHARSET.encode(input.getSource());
        final String checksum = checksum(content);

        if (config.isEnableCompression() &&
                content.remaining() >= config.getMinimumCompressionPayloadSize()) {
            encoding = Codemakerai.Encoding.GZIP;
            content = compress(content);
        }

        return Codemakerai.Source.newBuilder()
                .setContent(ByteString.copyFrom(content))
                .setEncoding(encoding)
                .setChecksum(checksum);
    }

    private static Codemakerai.Mode mapMode(Mode mode) {
        return switch (mode) {
            case CODE -> Codemakerai.Mode.CODE;
            case INLINE_CODE -> Codemakerai.Mode.INLINE_CODE;
            case EDIT_CODE -> Codemakerai.Mode.EDIT_CODE;
            case DOCUMENT -> Codemakerai.Mode.DOCUMENT;
            case FIX_SYNTAX -> Codemakerai.Mode.FIX_SYNTAX;
        };
    }

    private static Codemakerai.Language mapLanguage(Language language) {
        return switch (language) {
            case C -> Codemakerai.Language.C;
            case CPP -> Codemakerai.Language.CPP;
            case PHP -> Codemakerai.Language.PHP;
            case JAVASCRIPT -> Codemakerai.Language.JAVASCRIPT;
            case JAVA -> Codemakerai.Language.JAVA;
            case CSHARP -> Codemakerai.Language.CSHARP;
            case GO -> Codemakerai.Language.GO;
            case KOTLIN -> Codemakerai.Language.KOTLIN;
            case TYPESCRIPT -> Codemakerai.Language.TYPESCRIPT;
            case RUST -> Codemakerai.Language.RUST;
        };
    }

    private Codemakerai.Vote mapVote(Vote vote) {
        return switch (vote) {
            case UP_VOTE -> Codemakerai.Vote.UP_VOTE;
            case DOWN_VOTE -> Codemakerai.Vote.DOWN_VOTE;
        };
    }

    private static Codemakerai.Modify mapModify(Modify modify) {
        return switch (modify) {
            case NONE -> Codemakerai.Modify.UNMODIFIED;
            case REPLACE -> Codemakerai.Modify.REPLACE;
        };
    }

    private static Codemakerai.Visibility mapVisibility(Visibility visibility) {
        return switch (visibility) {
            case ALL -> Codemakerai.Visibility.ALL;
            case PUBLIC -> Codemakerai.Visibility.PUBLIC;
        };
    }

    private static ManagedChannel createChannel(Config config) {
        return ManagedChannelBuilder.forAddress(config.getEndpoint(), config.getPort())
                .useTransportSecurity()
                .build();
    }

    private static ByteBuffer compress(ByteBuffer input) {
        return ByteBuffer.wrap(compress(input.array(), input.remaining()));
    }

    private static byte[] compress(byte[] bytes, int length) {
        try {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            try (final GZIPOutputStream gzip = new GZIPOutputStream(output)) {
                gzip.write(bytes, 0, length);
                gzip.finish();
            }
            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to compress input.", e);
        }
    }

    private static ByteBuffer decompress(ByteBuffer input) {
        return ByteBuffer.wrap(decompress(input.array(), input.remaining()));
    }

    public static byte[] decompress(byte[] bytes, int length) {
        try {
            final ByteArrayInputStream input = new ByteArrayInputStream(bytes, 0, length);
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            try (final GZIPInputStream gzip = new GZIPInputStream(input)) {
                final byte[] buffer = new byte[1024];
                int len;
                while ((len = gzip.read(buffer)) > 0) {
                    output.write(buffer, 0, len);
                }
            }
            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to decompress input.", e);
        }
    }

    private static String checksum(ByteBuffer content) {
        return Hashing.sha256()
                .hashBytes(content.duplicate())
                .toString();
    }
}
