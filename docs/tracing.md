# Tracing

This page includes details about the Tracing feature, which provides comprehensive tracing capabilities for AI agents.

## Feature overview

The Tracing feature is a powerful monitoring and debugging tool that captures detailed information about agent runs,
including:

- Strategy execution
- LLM calls
- Tool invocations
- Node execution within the agent graph

This feature operates by intercepting key events in the agent pipeline and forwarding them to configurable message
processors. These processors can output the trace information to various destinations such as log files or other types
of files in the filesystem, enabling developers to gain insights into agent behavior and troubleshoot issues effectively.

### Event flow

1. The Tracing feature intercepts events in the agent pipeline.
2. Events are filtered based on the configured message filter.
3. Filtered events are passed to registered message processors.
4. Message processors format and output the events to their respective destinations.

## Configuration and initialization

### Basic setup

To use the Tracing feature, you need to:

1. Have one or more message processors (you can use the existing ones or create your own).
2. Install `Tracing` in your agent.
3. Configure the message filter (optional).
4. Add the message processors to the feature.

```kotlin
// Defining a logger/file that will be used as a destination of trace messages 
val logger = LoggerFactory.create("my.trace.logger")
val fs = JVMFileSystemProvider.ReadWrite
val path = Paths.get("/path/to/trace.log")

// Creating an agent
val agent = AIAgent(...) {
    install(Tracing) {
        // Configure message processors to handle trace events
        addMessageProcessor(TraceFeatureMessageLogWriter(logger))
        addMessageProcessor(TraceFeatureMessageFileWriter(outputPath, fileSystem::sink))

        // Optionally filter messages
        messageFilter = { message -> 
            // Only trace LLM calls and tool calls
            message is LLMCallStartEvent || message is ToolCallEvent 
        }
    }
}
```

### Message filtering

You can process all existing events or select some of them based on specific criteria.
The message filter lets you control which events are processed. This is useful for focusing on specific aspects of
agent runs:

```kotlin
// Filter for LLM-related events only
messageFilter = { message ->
    message is LLMCallStartEvent ||
            message is LLMCallEndEvent ||
            message is LLMCallWithToolsStartEvent ||
            message is LLMCallWithToolsEndEvent
}

// Filter for tool-related events only
messageFilter = { message ->
    message is ToolCallsEvent ||
            message is ToolCallResultEvent ||
            message is ToolValidationErrorEvent ||
            message is ToolCallFailureEvent
}

// Filter for node execution events only
messageFilter = { message ->
    message is AIAgentNodeExecutionStartEvent || message is AIAgentNodeExecutionEndEvent
}
```

### Large trace volumes

For agents with complex strategies or long-running executions, the volume of trace events can be substantial. Consider using the following methods to manage the volume of events:

- Use specific message filters to reduce the number of events.
- Implement custom message processors with buffering or sampling.
- Use file rotation for log files to prevent them from growing too large.

### Dependency graph

The Tracing feature has the following dependencies:

```
Tracing
├── AIAgentPipeline (for intercepting events)
├── TraceFeatureConfig
│   └── FeatureConfig
├── Message Processors
│   ├── TraceFeatureMessageLogWriter
│   │   └── FeatureMessageLogWriter
│   ├── TraceFeatureMessageFileWriter
│   │   └── FeatureMessageFileWriter
│   └── TraceFeatureMessageRemoteWriter
│       └── FeatureMessageRemoteWriter
└── Event Types (from ai.koog.agents.core.feature.model)
    ├── AIAgentStartedEvent
    ├── AIAgentFinishedEvent
    ├── AIAgentRunErrorEvent
    ├── AIAgentStrategyStartEvent
    ├── AIAgentStrategyFinishedEvent
    ├── AIAgentNodeExecutionStartEvent
    ├── AIAgentNodeExecutionEndEvent
    ├── LLMCallStartEvent
    ├── LLMCallWithToolsStartEvent
    ├── LLMCallEndEvent
    ├── LLMCallWithToolsEndEvent
    ├── ToolCallEvent
    ├── ToolValidationErrorEvent
    ├── ToolCallFailureEvent
    └── ToolCallResultEvent
```

## Examples and quickstarts

### Basic tracing to logger

```kotlin
// Create a logger
val logger = LoggerFactory.create("my.agent.trace")

// Create an agent with tracing
val agent = AIAgent(...) {
    install(Tracing) {
        addMessageProcessor(TraceFeatureMessageLogWriter(logger))
    }
}

// Run the agent
agent.run("Hello, agent!")
```

## Error handling and edge cases

### No message processors

If no message processors are added to the Tracing feature, a warning will be logged:

```
Tracing Feature. No feature out stream providers are defined. Trace streaming has no target.
```

The feature will still intercept events, but they will not be processed or output anywhere.

### Resource management

Message processors may hold resources (like file handles) that need to be properly released. Use the `use` extension
function to ensure proper cleanup:

```kotlin
TraceFeatureMessageFileWriter(fs, path).use { writer ->
    // Use the writer
    install(Tracing) {
        addMessageProcessor(writer)
    }

    // Run the agent
    agent.run(input)

    // Writer will be automatically closed when the block exits
}
```

### Tracing specific events to file

```kotlin
// Create a file writer
val fs = JVMFileSystemProvider.ReadWrite
val path = Paths.get("/path/to/llm-calls.log")
val writer = TraceFeatureMessageFileWriter(fs, path)

// Create an agent with filtered tracing
val agent = AIAgent(...) {
    install(Tracing) {
        // Only trace LLM calls
        messageFilter = { message ->
            message is LLMCallWithToolsStartEvent || message is LLMCallWithToolsEndEvent
        }
        addMessageProcessor(writer)
    }
}

// Run the agent
agent.run("Generate a story about a robot.")
```

### Tracing specific events to remote endpoint

You use tracing to remote endpoints when you need to send event data via the network. Once initiated, tracing to a
remote endpoint launches a light server at the specified port number and sends events via Kotlin Server-Sent Events 
(SSE).

```kotlin
// Create a file writer
val port = 4991
val serverConfig = ServerConnectionConfig(port = port)
val writer = TraceFeatureMessageRemoteWriter(connectionConfig = serverConfig)

// Create an agent with filtered tracing
val agent = AIAgent(...) {
    install(Tracing) {
        // Only trace LLM calls
        messageFilter = { message ->
            message is LLMCallWithToolsStartEvent || message is LLMCallWithToolsEndEvent
        }
        addMessageProcessor(writer)
    }
}

// Run the agent
agent.run("Generate a story about a robot.")
```
On the client side, you can use `FeatureMessageRemoteClient` to receive events and deserialize them.

```kotlin
// Create the client configuration
// Use the same port number as for the server emitting agent events
val clientConfig = AIAgentFeatureClientConnectionConfig(
   host = "127.0.0.1",
   port = 4991
)

// Create a client instance
val client = FeatureMessageRemoteClient(
   connectionConfig = clientConfig,
   scope = this
)

// Connect the client to the remote feature messaging service
client.connect()

// Collect events from the remote feature messaging service
val collectEvents = launch {
   client.receivedMessages.consumeAsFlow().collect { message: FeatureMessage ->
      // Process the received agent event
   }
}
```

## API documentation

The Tracing feature follows a modular architecture with these key components:

1. [Tracing](https://api.koog.ai/agents/agents-features/agents-features-trace/ai.koog.agents.features.tracing.feature/-tracing/index.html): the main feature class that intercepts events in the agent pipeline.
2. [TraceFeatureConfig](https://api.koog.ai/agents/agents-features/agents-features-trace/ai.koog.agents.features.tracing.feature/-trace-feature-config/index.html): configuration class for customizing feature behavior.
3. Message Processors: components that process and output trace events:
    - [TraceFeatureMessageLogWriter](https://api.koog.ai/agents/agents-features/agents-features-trace/ai.koog.agents.features.tracing.writer/-trace-feature-message-log-writer/index.html): writes trace events to a logger.
    - [TraceFeatureMessageFileWriter](https://api.koog.ai/agents/agents-features/agents-features-trace/ai.koog.agents.features.tracing.writer/-trace-feature-message-file-writer/index.html): writes trace events to a file.
    - [TraceFeatureMessageRemoteWriter](https://api.koog.ai/agents/agents-features/agents-features-trace/ai.koog.agents.features.tracing.writer/-trace-feature-message-remote-writer/index.html): sends trace events to a remote server.

## FAQ and troubleshooting

The following section includes commonly asked questions and answers related to the Tracing feature. 

### How do I trace only specific parts of my agent's execution?

Use the `messageFilter` property to filter events. For example, to trace only node execution:

```kotlin
install(Tracing) {
    messageFilter = { message ->
        message is AIAgentNodeExecutionStartEvent || message is AIAgentNodeExecutionEndEvent
    }
    addMessageProcessor(writer)
}
```

### Can I use multiple message processors?

Yes, you can add multiple message processors to trace to different destinations simultaneously:

```kotlin
install(Tracing) {
    addMessageProcessor(TraceFeatureMessageLogWriter(logger))
    addMessageProcessor(TraceFeatureMessageFileWriter(fs, path))
    addMessageProcessor(TraceFeatureMessageRemoteWriter(connectionConfig))
}
```

### How can I create a custom message processor?

Implement the `FeatureMessageProcessor` interface:

```kotlin
class CustomTraceProcessor : FeatureMessageProcessor {
    override suspend fun onMessage(message: FeatureMessage) {
        // Custom processing logic
        when (message) {
            is AIAgentNodeExecutionStartEvent -> {
                // Process node start event
            }
            is LLMCallWithToolsEndEvent -> {
                // Process LLM call end event
            }
            // Handle other event types
        }
    }
}

// Use your custom processor
install(Tracing) {
    addMessageProcessor(CustomTraceProcessor())
}
```

For more information about existing event types that can be handled by message processors, see [Predefined event types](#predefined-event-types).

## Predefined event types

Koog provides predefined event types that can be used in custom message processors. The predefined events can be
classified into several categories, depending on the entity they relate to:

- [Agent events](#agent-events)
- [Strategy events](#strategy-events)
- [Node events](#node-events)
- [LLM call events](#llm-call-events)
- [Tool call events](#tool-call-events)

### Agent events

#### AIAgentStartedEvent

Represents the start of an agent run. Includes the following fields:

| Name           | Data type | Required | Default             | Description                                                               |
|----------------|-----------|----------|---------------------|---------------------------------------------------------------------------|
| `strategyName` | String    | Yes      |                     | The name of the strategy that the agent should follow.                    |
| `eventId`      | String    | No       | `AIAgentStartedEvent` | The identifier of the event. Usually the `simpleName` of the event class. |

#### AIAgentFinishedEvent

Represents the end of an agent run. Includes the following fields:

| Name           | Data type | Required | Default              | Description                                                               |
|----------------|-----------|----------|----------------------|---------------------------------------------------------------------------|
| `strategyName` | String    | Yes      |                      | The name of the strategy that the agent followed.                         |
| `result`       | String    | Yes      |                      | The result of the agent run. Can be `null` if there is no result.         |
| `eventId`      | String    | No       | `AIAgentFinishedEvent` | The identifier of the event. Usually the `simpleName` of the event class. |

#### AIAgentRunErrorEvent

Represents the occurrence of an error during an agent run. Includes the following fields:

| Name           | Data type    | Required | Default              | Description                                                                                                     |
|----------------|--------------|----------|----------------------|-----------------------------------------------------------------------------------------------------------------|
| `strategyName` | String       | Yes      |                      | The name of the strategy that the agent followed.                                                               |
| `error`        | AIAgentError | Yes      |                      | The specific error that occurred during the agent run. For more information, see [AIAgentError](#aiagenterror). |
| `eventId`      | String       | No       | `AIAgentRunErrorEvent` | The identifier of the event. Usually the `simpleName` of the event class.                                       |

<a id="aiagenterror"></a>
The `AIAgentError` class provides more details about an error that occurred during an agent run. Includes the following fields:

| Name         | Data type | Required | Default | Description                                                      |
|--------------|-----------|----------|---------|------------------------------------------------------------------|
| `message`    | String    | Yes      |         | The message that provides more details about the specific error. |
| `stackTrace` | String    | Yes      |         | The collection of stack records until the last executed code.    |
| `cause`      | String    | No       | null    | The cause of the error, if available.                            |

### Strategy events

#### AIAgentStrategyStartEvent

Represents the start of a strategy run. Includes the following fields:

| Name           | Data type | Required | Default                   | Description                                                               |
|----------------|-----------|----------|---------------------------|---------------------------------------------------------------------------|
| `strategyName` | String    | Yes      |                           | The name of the strategy.                                                 |
| `eventId`      | String    | No       | `AIAgentStrategyStartEvent` | The identifier of the event. Usually the `simpleName` of the event class. |

#### AIAgentStrategyFinishedEvent

Represents the end of a strategy run. Includes the following fields:

| Name           | Data type | Required | Default                      | Description                                                               |
|----------------|-----------|----------|------------------------------|---------------------------------------------------------------------------|
| `strategyName` | String    | Yes      |                              | The name of the strategy.                                                 |
| `result`       | String    | Yes      |                              | The result of the run.                                                    |
| `eventId`      | String    | No       | `AIAgentStrategyFinishedEvent` | The identifier of the event. Usually the `simpleName` of the event class. |

### Node events

#### AIAgentNodeExecutionStartEvent

Represents the start of a node run. Includes the following fields:

| Name       | Data type | Required | Default                        | Description                                                               |
|------------|-----------|----------|--------------------------------|---------------------------------------------------------------------------|
| `nodeName` | String    | Yes      |                                | The name of the node whose run started.                                   |
| `input`    | String    | Yes      |                                | The input value for the node.                                             |
| `eventId`  | String    | No       | `AIAgentNodeExecutionStartEvent` | The identifier of the event. Usually the `simpleName` of the event class. |

#### AIAgentNodeExecutionEndEvent

Represents the end of a node run. Includes the following fields:

| Name       | Data type | Required | Default                      | Description                                                               |
|------------|-----------|----------|------------------------------|---------------------------------------------------------------------------|
| `nodeName` | String    | Yes      |                              | The name of the node whose run ended.                                     |
| `input`    | String    | Yes      |                              | The input value for the node.                                             |
| `output`   | String    | Yes      |                              | The output value produced by the node.                                    |
| `eventId`  | String    | No       | `AIAgentNodeExecutionEndEvent` | The identifier of the event. Usually the `simpleName` of the event class. |

### LLM call events

#### LLMCallStartEvent

Represents the start of an LLM call. Includes the following fields:

| Name      | Data type    | Required | Default           | Description                                                                        |
|-----------|--------------|----------|-------------------|------------------------------------------------------------------------------------|
| `prompt`  | Prompt       | Yes      |                   | The prompt that is sent to the model. For more information, see [Prompt](#prompt). |
| `tools`   | List<String> | Yes      |                   | The list of tools that the model can call.                                         |
| `eventId` | String       | No       | `LLMCallStartEvent` | The identifier of the event. Usually the `simpleName` of the event class.          |

<a id="prompt"></a>
The `Prompt` class represents a data structure for a prompt, consisting of a list of messages, a unique identifier, and
optional parameters for language model settings. Includes the following fields:

| Name       | Data type     | Required | Default     | Description                                                  |
|------------|---------------|----------|-------------|--------------------------------------------------------------|
| `messages` | List<Message> | Yes      |             | The list of messages that the prompt consists of.            |
| `id`       | String        | Yes      |             | The unique identifier for the prompt.                        |
| `params`   | LLMParams     | No       | LLMParams() | The settings that control the way the LLM generates content. |

#### LLMCallEndEvent

Represents the end of an LLM call. Includes the following fields:

| Name        | Data type              | Required | Default         | Description                                                               |
|-------------|------------------------|----------|-----------------|---------------------------------------------------------------------------|
| `responses` | List<Message.Response> | Yes      |                 | One or more responses returned by the model.                              |
| `eventId`   | String                 | No       | `LLMCallEndEvent` | The identifier of the event. Usually the `simpleName` of the event class. |

### Tool call events

#### ToolCallEvent

Represents the event of a model calling a tool. Includes the following fields:

| Name       | Data type | Required | Default       | Description                                                               |
|------------|-----------|----------|---------------|---------------------------------------------------------------------------|
| `toolName` | String    | Yes      |               | The name of the tool.                                                     |
| `toolArgs` | Tool.Args | Yes      |               | The arguments that are provided to the tool.                              |
| `eventId`  | String    | No       | `ToolCallEvent` | The identifier of the event. Usually the `simpleName` of the event class. |

#### ToolValidationErrorEvent

Represents the occurrence of a validation error during a tool call. Includes the following fields:

| Name           | Data type | Required | Default                  | Description                                                               |
|----------------|-----------|----------|--------------------------|---------------------------------------------------------------------------|
| `toolName`     | String    | Yes      |                          | The name of the tool for which validation failed.                         |
| `toolArgs`     | Tool.Args | Yes      |                          | The arguments that are provided to the tool.                              |
| `errorMessage` | String    | Yes      |                          | The validation error message.                                             |
| `eventId`      | String    | No       | `ToolValidationErrorEvent` | The identifier of the event. Usually the `simpleName` of the event class. |

#### ToolCallFailureEvent

Represents a failure to call a tool. Includes the following fields:

| Name       | Data type    | Required | Default              | Description                                                                                                           |
|------------|--------------|----------|----------------------|-----------------------------------------------------------------------------------------------------------------------|
| `toolName` | String       | Yes      |                      | The name of the tool.                                                                                                 |
| `toolArgs` | Tool.Args    | Yes      |                      | The arguments that are provided to the tool.                                                                          |
| `error`    | AIAgentError | Yes      |                      | The specific error that occurred when trying to call a tool. For more information, see [AIAgentError](#aiagenterror). |
| `eventId`  | String       | No       | `ToolCallFailureEvent` | The identifier of the event. Usually the `simpleName` of the event class.                                             |

#### ToolCallResultEvent

Represents a successful tool call with the return of a result. Includes the following fields:

| Name       | Data type  | Required | Default             | Description                                                               |
|------------|------------|----------|---------------------|---------------------------------------------------------------------------|
| `toolName` | String     | Yes      |                     | The name of the tool.                                                     |
| `toolArgs` | Tool.Args  | Yes      |                     | The arguments that are provided to the tool.                              |
| `result`   | ToolResult | Yes      |                     | The result of the tool call.                                              |
| `eventId`  | String     | No       | `ToolCallResultEvent` | The identifier of the event. Usually the `simpleName` of the event class. |