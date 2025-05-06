# Trace Feature Documentation

Agent features provide a way to extend and enhance the functionality of AI agents. Features can:

- Add new capabilities to agents;
- Intercept and modify agent behavior;
- Provide access to external systems and resources;
- Log and monitor agent execution.

In this section, we'll cover the Trace Feature, which provides comprehensive tracing capabilities for AI agents.

## Feature Overview

The Trace Feature is a powerful monitoring and debugging tool that capture detailed information about agent execution,
including:

- Agent creation and initialization;
- Strategy execution;
- LLM calls;
- Tool invocations;
- Node execution within the agent graph.

This feature operates by intercepting key events in the agent pipeline and forwarding them to configurable message
processors. These processors can output the trace information to various destinations such as log files or the
filesystem, enabling developers to gain insights into agent behavior and troubleshoot issues effectively.

### Event Flow

1. The Trace Feature intercepts events in the agent pipeline
2. Events are filtered based on the configured message filter
3. Filtered events are passed to registered message processors
4. Message processors format and output the events to their respective destinations

## Configuration & Initialization

### Basic Setup

To use the Trace Feature, you need to:

1. Have one or more message processors (you can use the existing ones or create your own)
2. Install the `TraceFeature` in your agent
3. Configure the message filter (optional)
4. Add the message processors to the feature

```kotlin
// Defining a logger/file that will be used as a destination of trace messages 
val logger = LoggerFactory.create("my.trace.logger")
val fs = JVMFileSystemProvider.ReadWrite
val path = Paths.get("/path/to/trace.log")

// Creating an agent
val agent = createAgent(
    strategy = myStrategy,
    coroutineScope = coroutineScope,
) {
    // Installing the `TraceFeature` in your agent
    install(TraceFeature) {

        // Configuring message filter (optional)
        messageFilter = { message ->
            // Accept only specific message types
            message is NodeExecutionStartEvent || message is NodeExecutionEndEvent
        }

        // Adding message processors
        addMessageProcessor(TraceFeatureMessageLogWriter(logger))
        addMessageProcessor(TraceFeatureMessageFileWriter(fs, path))
    }
}
```

### Message Filtering

You can process all existing events or choose some of them based on the specific criteria.
The message filter allows you to control which events are processed. This is useful for focusing on specific aspects of
agent execution:

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
    message is ToolCallsStartEvent || message is ToolCallsEndEvent
}

// Filter for node execution events only
messageFilter = { message ->
    message is NodeExecutionStartEvent || message is NodeExecutionEndEvent
}
```

## Public API Documentation

### Architecture

The Trace Feature follows a modular architecture with these key components:

1. **TraceFeature**: The main feature class that intercepts events in the agent pipeline
2. **TraceFeatureConfig**: Configuration class for customizing feature behavior
3. **Message Processors**: Components that process and output trace events
   - **TraceFeatureMessageLogWriter**: Writes trace events to a logger
   - **TraceFeatureMessageFileWriter**: Writes trace events to a file

### TraceFeature

Main feature class that intercepts events in the agent pipeline and forwards them to message
processors.

**Usage**:

```kotlin
val agent = KotlinAIAgent(/*...*/) {
    // Install the feature when initializing an agent
    install(TraceFeature) {
        // here goes the TraceFeatureConfig
    }
}
```

### TraceFeatureConfig

Configuration class for the Trace Feature.

**Properties**:

| Name          | Type                        | Default  | Description                                                  |
|---------------|-----------------------------|----------|--------------------------------------------------------------|
| messageFilter | (FeatureMessage) -> Boolean | { true } | Filter function that determines which messages are processed |

Please check out the existing filtering options in the [Message Filter](#message-filtering) subsection.

**Methods**:

| Name                | Parameters                         | Return Type | Description                             |
|---------------------|------------------------------------|-------------|-----------------------------------------|
| addMessageProcessor | processor: FeatureMessageProcessor | Unit        | Adds a message processor to the feature |

**Usage**:

```kotlin
val logger = LoggerFactory.create("my.trace.logger")

val agent = KotlinAIAgent(/*...*/) {
    install(TraceFeature) {
        // the config of the Trace feature
        // set up the messageFilter property
        messageFilter = { message ->
            message is LLMCallWithToolsStartEvent || message is LLMCallWithToolsEndEvent
        }
        // call the addMessageProcessor method
        addMessageProcessor(TraceFeatureMessageLogWriter(logger))
    }
}
```

### TraceFeatureMessageLogWriter

Writes trace events to a logger.

**Constructor Parameters**:

| Name         | Type      | Description                   |
|--------------|-----------|-------------------------------|
| targetLogger | MPPLogger | The logger to write events to |

**Usage**:

```kotlin
// create a logger
val logger = LoggerFactory.create("my.trace.logger")
// set the created logger as the target one for the TraceFeatureMessageLogWriter class
val writer = TraceFeatureMessageLogWriter(logger)

val agent = KotlinAIAgent(/*...*/) {
    // Install the TraceFeature
    install(TraceFeature) {
        // pass TraceFeatureMessageLogWriter to the addMessageProcessor method
        addMessageProcessor(writer)  // now events are passed to the logger
    }
}
```

### TraceFeatureMessageFileWriter

Writes trace events to a file.

**Constructor Parameters**:

| Name | Type                               | Description                            |
|------|------------------------------------|----------------------------------------|
| fs   | FileSystemProvider.ReadWrite<Path> | File system provider for writing files |
| path | Path                               | Path to the file to write events to    |

**Usage**:

```kotlin
// open a file and allow reading/writing to it
val fs = JVMFileSystemProvider.ReadWrite
val path = Paths.get("/path/to/trace.log")
// set the file as the target one for the TraceFeatureMessageFileWriter class
val writer = TraceFeatureMessageFileWriter(fs, path)

// Use with TraceFeature
install(TraceFeature) {
    // pass TraceFeatureMessageFileWriter to the addMessageProcessor method
    addMessageProcessor(writer) // now events are written to the file
}
```

### Using FeatureMessageRemoteWriter

Writes trace events to a remote endpoint, allowing for distributed tracing and monitoring.

**Constructor Parameters**:

| Name             | Type                   | Description                                                                                      |
|------------------|------------------------|--------------------------------------------------------------------------------------------------|
| connectionConfig | ServerConnectionConfig | The connection configuration for a local server that is used to broadcast agent execution events |

**Usage**:

```kotlin
val port = 8080
val serverConfig = ServerConnectionConfig(port = port)
val writer = FeatureMessageRemoteWriter(
    connectionConfig = serverConfig
)

// Use with TraceFeature
install(TraceFeature) {
    // pass FeatureMessageRemoteWriter to the addMessageProcessor method
    addMessageProcessor(writer) // now events are sent to the remote endpoint
}
```

## Internal Helpers & Utilities

### Event Types

The Trace Feature works with the following event types:

| Event Type                 | When Triggered                |
|----------------------------|-------------------------------|
| AgentCreateEvent           | An agent is created           |
| StrategyStartEvent         | A strategy execution starts   |
| LLMCallStartEvent          | Before a simple LLM call      |
| LLMCallEndEvent            | After a simple LLM call       |
| LLMCallWithToolsStartEvent | Before an LLM call with tools |
| LLMCallWithToolsEndEvent   | After an LLM call with tools  |
| ToolCallsStartEvent        | Before tool calls             |
| ToolCallsEndEvent          | After tool calls              |
| NodeExecutionStartEvent    | Before a node execution       |
| NodeExecutionEndEvent      | After a node execution        |

### Extension Properties

Both writer implementations provide extension properties for formatting events:

```kotlin
// adding new read-only property `agentCreateEventFormat` to the `AgentCreateEvent` class
val AgentCreateEvent.agentCreateEventFormat
get() = "${this.eventId} (strategy name: ${this.strategyName})"

// adding new read-only property `nodeExecutionStartEventFormat` to the `NodeExecutionStartEvent` class
val NodeExecutionStartEvent.nodeExecutionStartEventFormat
get() = "${this.eventId} (stage: ${this.stageName}, node: ${this.nodeName}, input: ${this.input})"

// Each property returns a formatted string representation of the event
```

## Error Handling & Edge Cases

### No Message Processors

If no message processors are added to the Trace Feature, a warning will be logged:

```
Tracing Feature. No feature out stream providers are defined. Trace streaming has no target.
```

The feature will still intercept events, but they won't be processed or output anywhere.

### Resource Management

Message processors may hold resources (like file handles) that need to be properly released. Use the `use` extension
function to ensure proper cleanup:

```kotlin
TraceFeatureMessageFileWriter(fs, path).use { writer ->
    // Use the writer
    install(TraceFeature) {
        addMessageProcessor(writer)
    }

    // Run the agent
    agent.run(input)

    // Writer will be automatically closed when the block exits
}
```

### Large Trace Volumes

For agents with complex strategies or long-running executions, the volume of trace events can be substantial. Consider:

1. Using specific message filters to reduce the number of events
2. Implementing custom message processors with buffering or sampling
3. Using file rotation for log files to prevent them from growing too large

## Dependency Graph

The Trace Feature has the following dependencies:

```
TraceFeature
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
└── Event Types (from ai.jetbrains.code.agents.local.features.common.model)
    ├── AgentCreateEvent
    ├── StrategyStartEvent
    ├── LLMCallStartEvent
    ├── LLMCallEndEvent
    ├── LLMCallWithToolsStartEvent
    ├── LLMCallWithToolsEndEvent
    ├── ToolCallsStartEvent
    ├── ToolCallsEndEvent
    ├── NodeExecutionStartEvent
    └── NodeExecutionEndEvent
```

## Examples & Quickstarts

### Basic Tracing to Logger

```kotlin
// Create a logger
val logger = LoggerFactory.create("my.agent.trace")

// Create an agent with tracing
val agent = createAgent(
    strategy = myStrategy,
    coroutineScope = coroutineScope,
) {
    install(TraceFeature) {
        addMessageProcessor(TraceFeatureMessageLogWriter(logger))
    }
}

// Run the agent
agent.run("Hello, agent!")
```

### Tracing Specific Events to File

```kotlin
// Create a file writer
val fs = JVMFileSystemProvider.ReadWrite
val path = Paths.get("/path/to/llm-calls.log")
val writer = TraceFeatureMessageFileWriter(fs, path)

// Create an agent with filtered tracing
val agent = createAgent(
    strategy = myStrategy,
    coroutineScope = coroutineScope,
) {
    install(TraceFeature) {
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

### Tracing Specific Events to Remote Endpoint

```kotlin
// Create a file writer
val port = 8080
val serverConfig = ServerConnectionConfig(port = port)
val writer = TraceFeatureMessageRemoteWriter(connectionConfig = serverConfig)

// Create an agent with filtered tracing
val agent = createAgent(
    strategy = myStrategy,
    coroutineScope = coroutineScope,
) {
    install(TraceFeature) {
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

## FAQ / Troubleshooting

### How do I trace only specific parts of my agent's execution?

Use the `messageFilter` property to filter events. For example, to trace only node execution:

```kotlin
install(TraceFeature) {
    messageFilter = { message ->
        message is NodeExecutionStartEvent || message is NodeExecutionEndEvent
    }
    addMessageProcessor(writer)
}
```

### Can I use multiple message processors?

Yes, you can add multiple message processors to trace to different destinations simultaneously:

```kotlin
install(TraceFeature) {
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
            is NodeExecutionStartEvent -> {
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
install(TraceFeature) {
    addMessageProcessor(CustomTraceProcessor())
}
```
