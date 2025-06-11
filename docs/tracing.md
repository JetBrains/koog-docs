# Tracing

This page includes details about the Tracing feature, which provides comprehensive tracing capabilities for AI agents.

## Feature overview

The Tracing feature is a powerful monitoring and debugging tool that captures detailed information about agent runs,
including:

- Agent creation and initialization
- Strategy execution
- LLM calls
- Tool invocations
- Node execution within the agent graph

This feature operates by intercepting key events in the agent pipeline and forwarding them to configurable message
processors. These processors can output the trace information to various destinations such as log files or the
filesystem, enabling developers to gain insights into agent behavior and troubleshoot issues effectively.

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

```kotlin
// Create a file writer
val port = 8080
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

## API documentation

The Tracing feature follows a modular architecture with these key components:

1. [Tracing](https://api.koog.ai/agents/agents-features/agents-features-trace/ai.koog.agents.local.features.tracing.feature/-tracing/index.html): the main feature class that intercepts events in the agent pipeline.
2. [TraceFeatureConfig](https://api.koog.ai/agents/agents-features/agents-features-trace/ai.koog.agents.local.features.tracing.feature/-trace-feature-config/index.html): configuration class for customizing feature behavior.
3. Message Processors: components that process and output trace events:
    - [TraceFeatureMessageLogWriter](https://api.koog.ai/agents/agents-features/agents-features-trace/ai.koog.agents.local.features.tracing.writer/-trace-feature-message-log-writer/index.html): writes trace events to a logger.
    - [TraceFeatureMessageFileWriter](https://api.koog.ai/agents/agents-features/agents-features-trace/ai.koog.agents.local.features.tracing.writer/-trace-feature-message-file-writer/index.html): writes trace events to a file.
    - [TraceFeatureMessageRemoteWriter](https://api.koog.ai/agents/agents-features/agents-features-trace/ai.koog.agents.local.features.tracing.writer/-trace-feature-message-remote-writer/index.html): sends trace events to a remote server.

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

Koog provides predefined event types that can be used in custom message processors. The predefined events can be
classified into several categories, depending on the entity they relate to:

- [Agent events](#agent-events)
- [Strategy events](#strategy-events)
- [Node events](#node-events)
- [LLM call events](#llm-call-events)
- [Tool call events](#tool-call-events)

#### Agent events

##### AIAgentStartedEvent

Represents the start of an agent run. Includes the following fields:

| Name           | Data type | Required | Default | Description                                            |
|----------------|-----------|----------|---------|--------------------------------------------------------|
| `strategyName` | String    | Yes      |         | The name of the strategy that the agent should follow. |
| `eventId`      | String    | Yes      |         | The unique identifier of the event.                    |

##### AIAgentFinishedEvent

Represents the end of an agent run. Includes the following fields:

| Name           | Data type | Required | Default | Description                                       |
|----------------|-----------|----------|---------|---------------------------------------------------|
| `strategyName` | String    | Yes      |         | The name of the strategy that the agent followed. |
| `result`       | String    | No       |         | The result of the agent run.                      |
| `eventId`      | String    | Yes      |         | The unique identifier of the event.               |

##### AIAgentRunErrorEvent

Represents the occurrence of an error during an agent run. Includes the following fields:

| Name           | Data type    | Required | Default | Description                                                                                             |
|----------------|--------------|----------|---------|---------------------------------------------------------------------------------------------------------|
| `strategyName` | String       | Yes      |         | The name of the strategy that the agent followed.                                                       |
| `error`        | AIAgentError | No       |         | The specific error that occurred during the agent run. For more inforomation, see `AIAgentError` below. |
| `eventId`      | String       | Yes      |         | The unique identifier of the event.                                                                     |

**AIAgentError**

An error that occurred during an agent run. Includes the following fields:

| Name         | Data type | Required | Default | Description                                                      |
|--------------|-----------|----------|---------|------------------------------------------------------------------|
| `message`    | String    | Yes      |         | The message that provides more details about the specific error. |
| `stackTrace` | String    | Yes      |         | The collection of stack records until the last executed code.    |
| `cause`      | String    | No       | null    | The cause of the error, if available.                            |

#### Strategy events

##### AIAgentStrategyStartEvent

Represents the start of a strategy run. Includes the following fields:

| Name           | Data type | Required | Default | Description                         |
|----------------|-----------|----------|---------|-------------------------------------|
| `strategyName` | String    | Yes      |         | The name of the strategy.           |
| `eventId`      | String    | Yes      |         | The unique identifier of the event. |

##### AIAgentStrategyFinishedEvent

Represents the end of a strategy run. Includes the following fields:

| Name           | Data type | Required | Default | Description                         |
|----------------|-----------|----------|---------|-------------------------------------|
| `strategyName` | String    | Yes      |         | The name of the strategy.           |
| `result`       | String    | No       |         | The result of the run.              |
| `eventId`      | String    | Yes      |         | The unique identifier of the event. |

#### Node events

##### AIAgentNodeExecutionStartEvent

Represents the start of a node run. Includes the following fields:

| Name       | Data type | Required | Default | Description                             |
|------------|-----------|----------|---------|-----------------------------------------|
| `nodeName` | String    | Yes      |         | The name of the node whose run started. |
| `input`    | String    | Yes      |         | The input value for the node.           |
| `eventId`  | String    | Yes      |         | The unique identifier of the event.     |

##### AIAgentNodeExecutionEndEvent

Represents the end of a node run. Includes the following fields:

| Name       | Data type | Required | Default | Description                            |
|------------|-----------|----------|---------|----------------------------------------|
| `nodeName` | String    | Yes      |         | The name of the node whose run ended.  |
| `input`    | String    | Yes      |         | The input value for the node.          |
| `output`   | String    | Yes      |         | The output value produced by the node. |
| `eventId`  | String    | Yes      |         | The unique identifier of the event.    |

#### LLM call events

##### LLMCallStartEvent

Represents the start of an LLM call. Includes the following fields:

| Name      | Data type    | Required | Default | Description                                |
|-----------|--------------|----------|---------|--------------------------------------------|
| `prompt`  | Prompt       | Yes      |         | The prompt that is sent to the model.      |
| `tools`   | List<String> | Yes      |         | The list of tools that the model can call. |
| `eventId` | String       | Yes      |         | The unique identifier of the event.        |

**Prompt**

The `Prompt` class represents a data structure for a prompt, consisting of a list of messages, a unique identifier, and
optional parameters for language model settings. 
Includes the following fields:

| Name       | Data type     | Required | Default     | Description                                                    |
|------------|---------------|----------|-------------|----------------------------------------------------------------|
| `messages` | List<Message> | Yes      |             | The list of messages that the prompt consists of.              |
| `id`       | String        | Yes      |             | The unique identifier for the prompt.                          |
| `params`   | LLMParams     | No       | LLMParams() | The settings that control the way the model generates content. |

##### LLMCallEndEvent

Represents the end of an LLM call. Includes the following fields:

| Name        | Data type              | Required | Default | Description                                  |
|-------------|------------------------|----------|---------|----------------------------------------------|
| `responses` | List<Message.Response> | Yes      |         | One or more responses returned by the model. |
| `eventId`   | String                 | Yes      |         | The unique identifier of the event.          |

#### Tool call events

##### ToolCallEvent

Represents the event of a model calling a tool. Includes the following fields:

| Name       | Data type | Required | Default | Description                                  |
|------------|-----------|----------|---------|----------------------------------------------|
| `toolName` | String    | Yes      |         | The name of the tool.                        |
| `toolArgs` | Tool.Args | Yes      |         | The arguments that are provided to the tool. |
| `eventId`  | String    | Yes      |         | The unique identifier of the event.          |

##### ToolValidationErrorEvent

Represents the occurrence of a validation error occurs during a tool call. Includes the following fields:

| Name           | Data type | Required | Default | Description                                       |
|----------------|-----------|----------|---------|---------------------------------------------------|
| `toolName`     | String    | Yes      |         | The name of the tool for which validation failed. |
| `toolArgs`     | Tool.Args | Yes      |         | The arguments that failed validation.             |
| `errorMessage` | String    | Yes      |         | The validation error message.                     |
| `eventId`      | String    | Yes      |         | The unique identifier of the event.               |

##### ToolCallFailureEvent

Represents a failure to call a tool. Includes the following fields:

| Name       | Data type    | Required | Default | Description                                                                                       |
|------------|--------------|----------|---------|---------------------------------------------------------------------------------------------------|
| `toolName` | String       | Yes      |         | The name of the tool.                                                                             |
| `toolArgs` | Tool.Args    | Yes      |         | The arguments that are provided to the tool.                                                      |
| `error`    | AIAgentError | Yes      |         | The specific that occurred when trying to call a tool. For more inforomation, see `AIAgentError`. |
| `eventId`  | String       | Yes      |         | The unique identifier of the event.                                                               |

##### ToolCallResultEvent

Represents a successful tool call with the return of a result. Includes the following fields:

| Name       | Data type  | Required | Default | Description                                  |
|------------|------------|----------|---------|----------------------------------------------|
| `toolName` | String     | Yes      |         | The name of the tool.                        |
| `toolArgs` | Tool.Args  | Yes      |         | The arguments that are provided to the tool. |
| `result`   | ToolResult | No       |         | The result of the tool call.                 |
| `eventId`  | String     | Yes      |         | The unique identifier of the event.          |