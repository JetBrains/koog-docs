# OpenTelemetry support

This page provides details about the support for OpenTelemetry with the Koog agentic framework for tracing and monitoring your AI agents.

## Overview

OpenTelemetry is an observability framework that provides tools for generating, collecting, and exporting telemetry data (metrics, logs, and traces) from your applications. The Koog OpenTelemetry feature allows you to instrument your AI agents to collect telemetry data, which can help you:

- Monitor agent performance and behavior
- Debug issues in complex agent workflows
- Visualize the execution flow of your agents
- Track LLM calls and tool usage
- Analyze agent behavior patterns

## Key OpenTelemetry concepts

- **Spans**: spans represent individual units of work or operations within a distributed trace. They indicate the beginning and end of a specific activity in an application, such as an agent execution, a function call, an LLM call, or a tool call.
- **Attributes**: attributes provide metadata about a telemetry-related item such as a span. Attributes are represented as key-value pairs.
- **Events**: events are specific points in time during the lifetime of a span (span-related events) that represent something potentially noteworthy that happened.
- **Exporters**: exporters are components responsible for sending the collected telemetry data (spans, metrics, logs) to various backends or destinations.
- **Collectors**: collectors receive, process, and export telemetry data. They act as intermediaries between your applications and your observability backend.

The OpenTelemetry feature in Koog automatically creates spans for various agent events, including:

- Agent execution start and end
- Node execution
- LLM calls
- Tool calls

## Installation

To use OpenTelemetry with Koog, add the OpenTelemetry feature to your agent:

```kotlin
val agent = AIAgent(
    executor = simpleOpenAIExecutor(apiKey),
    llmModel = OpenAIModels.Chat.GPT4o,
    systemPrompt = "You are a helpful assistant.",
    installFeatures = {
        install(OpenTelemetry) {
            // Configuration options go here
        }
    }
)
```

## Configuration

### Basic configuration

Here is the full list of available properties that you set when configuring the OpenTelemetry feature in an agent:

| Name             | Data type          | Default value | Description                                                                  |
|------------------|--------------------|---------------|------------------------------------------------------------------------------|
| `serviceName`    | `String`           | `ai.koog`     | The name of the service being instrumented.                                  |
| `serviceVersion` | `String`           | `0.0.0`       | The version of the service being instrumented.                               |
| `sdk`            | `OpenTelemetrySdk` | `null`        | The OpenTelemetry SDK instance to use for telemetry collection.              |
| `isVerbose`      | `Boolean`          | `false`       | Whether to enable verbose logging for debugging OpenTelemetry configuration. |
| `tracer`         | `Tracer`           |               | The OpenTelemetry tracer instance used for creating spans.                   |

The `OpenTelemetryConfig` class also includes methods that represent actions related to different configuration
items. Here is an example of installing the OpenTelemetry feature with a basic set of configuration items:


```kotlin
install(OpenTelemetry) {
    // Set your service configuration
    setServiceInfo("my-agent-service", "1.0.0")
    
    // Add the Logging exporter
    addSpanExporter(LoggingSpanExporter.create())
}
```

For a reference of available methods, see the sections below.

#### setServiceInfo

Sets the service information including name and version. Takes the following arguments:

| Name               | Data type | Required | Default value | Description                                                 |
|--------------------|-----------|----------|---------------|-------------------------------------------------------------|
| `serviceName`      | String    | Yes      |               | The name of the service being instrumented.                 |
| `serviceVersion`   | String    | Yes      |               | The version of the service being instrumented.              |

#### addSpanExporter

Adds a span exporter to send telemetry data to external systems. Takes the following argument:

| Name       | Data type      | Required | Default value | Description                                                                   |
|------------|----------------|----------|---------------|-------------------------------------------------------------------------------|
| `exporter` | `SpanExporter` | Yes      |               | The `SpanExporter` instance to be added to the list of custom span exporters. |

#### addSpanProcessor

Adds a span processor to process spans before they are exported. Takes the following argument:

| Name       | Data type      | Required | Default value | Description                                                                                |
|------------|----------------|----------|---------------|--------------------------------------------------------------------------------------------|
| `processor` | `SpanProcessor` | Yes      |               | The span processor that includes the custom logic to process telemetry data before export. |

#### addResourceAttributes

Adds resource attributes to provide additional context about the service. Takes the following argument:

| Name         | Data type             | Required | Default value | Description                                                            |
|--------------|-----------------------|----------|---------------|------------------------------------------------------------------------|
| `attributes` | `Map<AttributeKey<T>, T>` | Yes      |               | The key-value pairs that provide additional details about the service. |

#### setSampler

Sets the sampling strategy to control which spans are collected. Takes the following argument:

| Name      | Data type | Required | Default value | Description                                                      |
|-----------|-----------|----------|---------------|------------------------------------------------------------------|
| `sampler` | `Sampler` | Yes      |               | The sampler instance to set for the OpenTelemetry configuration. |

#### setVerbose

Enables or disables verbose logging for debugging OpenTelemetry configuration. Takes the following argument:

| Name      | Data type | Required | Default value | Description                                                             |
|-----------|-----------|----------|---------------|-------------------------------------------------------------------------|
| `verbose` | `Boolean` | Yes      | `false`       | If true, `true`, the application collects more detailed telemetry data. |

### Advanced configuration

For more advanced configuration, you can also customize the following configuration options:

- Sampler: configure the sampling strategy to adjust the frequency and amount of collected data.
- Resource attributes: add more information about the process that is producing telemetry data. 

```kotlin
install(OpenTelemetry) {
    // Set your service configuration
    setServiceInfo("my-agent-service", "1.0.0")
    
    // Add the Logging exporter
    addSpanExporter(LoggingSpanExporter.create())
    
    // Set the sampler 
    setSampler(Sampler.alwaysOn()) 

    // Add resource attributes
    addResourceAttributes(mapOf(
        AttributeKey.stringKey("custom.attribute") to "custom-value")
    )
}
```

#### Sampler

To define a sampler, use a corresponding method of the `Sampler` class (`io.opentelemetry.sdk.trace.samplers.Sampler`) 
from the `opentelemetry-java` SDK that represents the sampling strategy you want to use. 

The default sampling strategy is as follows:

- `Sampler.alwaysOn()`: The default sampling strategy where every span (trace) is sampled.

For more information about available samplers and sampling strategies, see the OpenTelemetry [Sampler](https://opentelemetry.io/docs/languages/java/sdk/#sampler) documentation.

#### Resource attributes

Resource attributes represent additional information about a process producing telemetry data. Koog includes a set of
resource attributes that are set by default:

- `service.name`
- `service.version`
- `service.instance.time`
- `os.type`
- `os.version`
- `os.arch`

In addition to default resource attributes, you can also add custom attributes. To add a custom attribute to an 
OpenTelemetry configuration in Koog, use the `addResourceAttribute()` method in an OpenTelemetry configuration that 
takes a key and a value as its arguments.

```kotlin
addResourceAttributes(mapOf(
    AttributeKey.stringKey("custom.attribute") to "custom-value")
)
```

## Span types and attributes

The OpenTelemetry feature automatically creates different types of spans to track various operations in your agent:

- **CreateAgentSpan**: the creation of an agent.
- **InvokeAgentSpan**: the invocation of an agent.
- **NodeExecuteSpan**: the execution of a node in the agent's strategy. This is a custom, Koog-specific span.
- **InferenceSpan**: an LLM call.
- **ExecuteToolSpan**: a tool call.

Spans are organized in a nested, hierarchical structure. Here is an example of a span structure:

```text
CreateAgentSpan
    InvokeAgentSpan
        NodeExecuteSpan
            InferenceSpan
        NodeExecuteSpan
            ExecuteToolSpan
        NodeExecuteSpan
            InferenceSpan    
```

### Span attributes

Span attributes provide metadata related to a span. Each span has its set of attributes, while some spans can also repeat attributes. 

Koog supports a list of predefined attributes that follow OpenTelemetry's [Semantic conventions for generative AI events](https://opentelemetry.io/docs/specs/semconv/gen-ai/gen-ai-events/). For example, the conventions define an attribute named `gen_ai.conversation.id`, which is usually a required attribute for a span. In Koog, the value of this attribute is the unique identifier for an agent run, when you call the `agent.run()` method.

In addition, Koog also includes custom, Koog-specific attributes. You can recognize most of these attributes by the `koog.` 
prefix. Here are the available custom attributes:

- For the `InvokeAgentSpan` span:
    - `koog.agent.strategy.name`: the name of the agent strategy. A strategy is a Koog-related entity that describes the purpose of the agent.
- For the `NodeExecuteSpan` span:
    - `koog.node.name`: the name of the node being run.

### Events

A span can also have an _event_ attached to the span. Events describe a specific point in time when something relevant 
happened. For example, when an LLM call started or finished. Events also have attributes and additionally include 
_event body_ fields.

The following event types are supported in line with OpenTelemetry's [Semantic conventions for generative AI events](https://opentelemetry.io/docs/specs/semconv/gen-ai/gen-ai-events/):

- **UserMessageEvent**: the user message passed to the model.
- **SystemMessageEvent**: the system instructions passed to the model.
- **AssistantMessageEvent**: the assistant message passed to the model.
- **ChoiceEvent**: the response message from a model.
- **ToolMessageEvent**: the response from a tool or function call passed to the model.

!!! note   
    The `optentelemetry-java` SDK does not support adding an event body when adding an event. Therefore, in the OpenTelemetry support in Koog, event body fields are a separate attribute whose key is `body` and value type is string. The string includes the content or payload for the event body, which is usually a JSON-like object. For examples of event bodies, see the [OpenTelemetry documentation](https://opentelemetry.io/docs/specs/semconv/gen-ai/gen-ai-events/#examples).

## Exporters

Exporters send collected telemetry data to an OpenTelemetry Collector or other types of destinations or backend implementations. To add an exporter, use the `addSpanExporter()` method when installing the OpenTelemetry feature. The 
method takes the following argument:

| Name       | Data type    | Required | Default | Description                                                                 |
|------------|--------------|----------|---------|-----------------------------------------------------------------------------|
| `exporter` | SpanExporter | Yes      |         | The SpanExporter instance to be added to the list of custom span exporters. |

The sections below provide information about some of the most commonly used exporters from the `opentelemetry-java` SDK.  

### Logging exporter

A logging exporter that outputs trace information to the console. `LoggingSpanExporter` (`io.opentelemetry.exporter.logging.LoggingSpanExporter`) is a part of the `opentelemetry-java` SDK.

This type of export is useful for development and debugging purposes.

```kotlin
install(OpenTelemetry) {
    // Add the logging exporter
    addSpanExporter(LoggingSpanExporter.create())
   // Add more exporters as needed
}
```

### OpenTelemetry HTTP exporter

OpenTelemetry HTTP exporter (`OtlpHttpSpanExporter`) is a part of the `opentelemetry-java` SDK (`io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter`) and sends span data to a backend through HTTP.

```kotlin
install(OpenTelemetry) {
   // Add OpenTelemetry HTTP exporter 
   addSpanExporter(
      OtlpHttpSpanExporter.builder()
         // Set the maximum time to wait for the collector to process an exported batch of spans 
         .setTimeout(30, TimeUnit.SECONDS)
         // Set the OpenTelemetry endpoint to connect to
         .setEndpoint("http://localhost:3000/api/public/otel/v1/traces")
         // Add the authorization header
         .addHeader("Authorization", "Basic $AUTH_STRING")
         .build()
   )
}
```

### OpenTelemetry gRPC exporter

OpenTelemetry gRPC exporter (`OtlpGrpcSpanExporter`) is a part of the `opentelemetry-java` SDK (`io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter`). It exports telemetry data to a backend through gRPC and lets you define the host and
port of the backend, collector, or endpoint that receives the data. The default port is `4317`.

```kotlin
install(OpenTelemetry) {
   // Add OpenTelemetry gRPC exporter 
   addSpanExporter(
      OtlpGrpcSpanExporter.builder()
          // Set the host and the port
         .setEndpoint("http://localhost:4317")
         .build()
   )
}
```

## Integration with Jaeger

Jaeger is a popular distributed tracing system that works with OpenTelemetry. The `opentelemetry` directory within 
`examples` in the Koog repository includes an example of using OpenTelemetry with Jaeger and Koog agents.

### Prerequisites

To test OpenTelemetry with Koog and Jaeger, start the Jaeger OpenTelemetry all-in-one process using the provided `docker-compose.yaml` file, by running the following command:

```bash
docker compose up -d
```

The provided Docker Compose YAML file includes the following content:

```yaml
# docker-compose.yaml
services:
  jaeger-all-in-one:
    image: jaegertracing/all-in-one:1.39
    container_name: jaeger-all-in-one
    environment:
      - COLLECTOR_OTLP_ENABLED=true
    ports:
      - "4317:4317"
      - "16686:16686"
```

To access the Jaeger UI and view your traces, open `http://localhost:16686`.

### Example

To export telemetry data for use in Jaeger, the example uses `LoggingSpanExporter` (`io.opentelemetry.exporter.logging.LoggingSpanExporter`) and `OtlpGrpcSpanExporter` (`io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter`) from the `opentelemetry-java` SDK.

Here is the full code sample:

```kotlin
import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.example.ApiKeyService
import ai.koog.agents.features.opentelemetry.feature.OpenTelemetry
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import io.opentelemetry.exporter.logging.LoggingSpanExporter
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import kotlinx.coroutines.runBlocking


fun main() = runBlocking {

   val agent = AIAgent(
      executor = simpleOpenAIExecutor(ApiKeyService.openAIApiKey),
      llmModel = OpenAIModels.Reasoning.GPT4oMini,
      systemPrompt = "You are a code assistant. Provide concise code examples."
   ) {
      install(OpenTelemetry) {
         // Add a console logger for local debugging
         addSpanExporter(LoggingSpanExporter.create())

         // Send traces to OpenTelemetry collector
         addSpanExporter(
            OtlpGrpcSpanExporter.builder()
               .setEndpoint("http://localhost:4317")
               .build()
         )
      }
   }

   println("Running agent with OpenTelemetry tracing...")

   val result = agent.run("Tell me a joke about programming")

   println("Agent run completed with result: '$result'." +
           "\nCheck Jaeger UI at http://localhost:16686 to view traces")
}
```

## Integration with Langfuse

Langfuse is a platform for observability and tracing specifically designed for applications that use large language 
models. In addition, Langfuse provides features such as prompt management, evaluation, metrics, and analytics.

OpenTelemetry support in Koog lets you configure the export of telemetry data for use with Langfuse. For an example of 
the specific implementation, see the code below.

```kotlin
import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.example.ApiKeyService
import ai.koog.agents.features.opentelemetry.feature.OpenTelemetry
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import io.opentelemetry.exporter.logging.LoggingSpanExporter
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter
import io.opentelemetry.sdk.trace.export.SpanExporter
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.TimeUnit


fun main() = runBlocking {
    // Set the Langfuse host
    val langfuseUrl = System.getenv()["LANGFUSE_HOST"] ?: throw IllegalArgumentException("LANGFUSE_HOST is not set")
    
   // Create an AI agent with OpenTelemetry configured for Langfuse
    val agent = AIAgent(
        executor = simpleOpenAIExecutor(ApiKeyService.openAIApiKey),
        llmModel = OpenAIModels.Reasoning.GPT4oMini,
        systemPrompt = "You are a code assistant. Provide concise code examples."
    ) {
        install(OpenTelemetry) {
            // Add a console logger for local debugging
            addSpanExporter(LoggingSpanExporter.create())
            // Add the Langfuse span exporter
            addSpanExporter(
                createLangfuseSpanExporter(
                    langfuseUrl,
                    System.getenv()["LANGFUSE_PUBLIC_KEY"] ?: throw IllegalArgumentException("LANGFUSE_PUBLIC_KEY is not set"),
                    System.getenv()["LANGFUSE_SECRET_KEY"] ?: throw IllegalArgumentException("LANGFUSE_SECRET_KEY is not set"),
                )
            )
        }
    }

    println("Running agent with Langfuse tracing")

    val result = agent.run("Tell me a joke about programming")

    println("Result: $result\nSee traces on $langfuseUrl")
}

// The function used to create a Langfuse exporter
fun createLangfuseSpanExporter(
    // Set the URL of the Langfuse service
    langfuseUrl: String,
    // Set Langfuse public and private keys
    langfusePublicKey: String,
    langfuseSecretKey: String,
): SpanExporter {
    val credentials = "$langfusePublicKey:$langfuseSecretKey"
    val auth = Base64.getEncoder().encodeToString(credentials.toByteArray(Charsets.UTF_8))

    return OtlpHttpSpanExporter.builder()
        // Set the maximum time to wait for the collector to process an exported batch of spans 
        .setTimeout(30, TimeUnit.SECONDS)
        // Set the OpenTelemetry endpoint to connect to
        .setEndpoint("$langfuseUrl/api/public/otel/v1/traces")
        // Add the authorization header
        .addHeader("Authorization", "Basic $auth")
        .build()
}
```

## Troubleshooting

### Common issues

1. **No traces appearing in Jaeger or Langfuse**
    - Ensure the service is running and the OpenTelemetry port (4317) is accessible.
    - Check that the OpenTelemetry exporter is configured with the correct endpoint.
    - Make sure to wait a few seconds after agent execution for traces to be exported.

2. **Missing spans or incomplete traces**
    - Verify that the agent execution completes successfully.
    - Ensure that you're not closing the application too quickly after agent execution.
    - Add a delay after agent execution to allow time for spans to be exported.

3. **Excessive number of spans**
    - Consider using a different sampling strategy by configuring the `sampler` property.
    - For example, use `Sampler.traceIdRatioBased(0.1)` to sample only 10% of traces.