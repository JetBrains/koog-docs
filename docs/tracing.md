# Trace

Agent features provide a way to extend and enhance the functionality of AI agents. Features can:

- Add new capabilities to agents
- Intercept and modify agent behavior
- Provide access to external systems and resources
- Log and monitor agent execution

This section includes details about the Trace feature, which provides comprehensive tracing capabilities for AI agents.

## Feature overview

The Trace feature is a powerful monitoring and debugging tool that captures detailed information about agent runs,
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

1. The Trace feature intercepts events in the agent pipeline.
2. Events are filtered based on the configured message filter.
3. Filtered events are passed to registered message processors.
4. Message processors format and output the events to their respective destinations.

## Configuration and initialization

### Basic setup

To use the Trace feature, you need to:

1. Have one or more message processors (you can use the existing ones or create your own).
2. Install `Tracing` feature in your agent.
3. Configure the message filter (optional).
4. Add the message processors to the feature.

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
    // Installing the `Tracing` feature in your agent
    install(Tracing) {

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
    message is ToolCallsStartEvent || message is ToolCallsEndEvent
}

// Filter for node execution events only
messageFilter = { message ->
    message is NodeExecutionStartEvent || message is NodeExecutionEndEvent
}
```

## API documentation

### Architecture

The Trace feature follows a modular architecture with these key components:

1. **Tracing**: the main feature class that intercepts events in the agent pipeline.
2. **TraceFeatureConfig**: configuration class for customizing feature behavior.
3. **Message Processors**: components that process and output trace events:
   - **TraceFeatureMessageLogWriter**: writes trace events to a logger.
   - **TraceFeatureMessageFileWriter**: writes trace events to a file.

### Class: `Tracing`

The main feature class that intercepts events in the agent pipeline and forwards them to message
processors.

**Usage**:

```kotlin
val agent = AIAgent(/*...*/) {
    // Install the feature when initializing an agent
    install(Tracing) {
        // Add TraceFeatureConfig
    }
}
```

### Class: `TraceFeatureConfig`

Configuration class for the Trace feature.

**Properties**:

| Name          | Type                        | Default  | Description                                                  |
|---------------|-----------------------------|----------|--------------------------------------------------------------|
| messageFilter | (FeatureMessage) -> Boolean | { true } | Filter function that determines which messages are processed |

For more information on existing filtering options, see [Message filtering](#message-filtering).

**Methods**:

| Name                | Parameters                         | Return Type | Description                             |
|---------------------|------------------------------------|-------------|-----------------------------------------|
| addMessageProcessor | processor: FeatureMessageProcessor | Unit        | Adds a message processor to the feature |

**Usage**:

```kotlin
val logger = LoggerFactory.create("my.trace.logger")

val agent = AIAgent(/*...*/) {
    install(Tracing) {
        // The config of the Trace feature
        // Set the messageFilter property
        messageFilter = { message ->
            message is LLMCallWithToolsStartEvent || message is LLMCallWithToolsEndEvent
        }
        // Call the addMessageProcessor method
        addMessageProcessor(TraceFeatureMessageLogWriter(logger))
    }
}
```

### Class: `TraceFeatureMessageLogWriter`

Writes trace events to a logger.

**Constructor parameters**:

| Name         | Type      | Description                   |
|--------------|-----------|-------------------------------|
| targetLogger | MPPLogger | The logger to write events to |

**Usage**:

```kotlin
// Create a logger
val logger = LoggerFactory.create("my.trace.logger")
// Set the created logger as the target for the TraceFeatureMessageLogWriter class
val writer = TraceFeatureMessageLogWriter(logger)

val agent = AIAgent(/*...*/) {
    // Install Tracing
    install(Tracing) {
        // Pass TraceFeatureMessageLogWriter to the addMessageProcessor method
        addMessageProcessor(writer)  // now events are passed to the logger
    }
}
```

### Class: `TraceFeatureMessageFileWriter`

Writes trace events to a file.

**Constructor Parameters**:

| Name | Type                               | Description                            |
|------|------------------------------------|----------------------------------------|
| fs   | FileSystemProvider.ReadWrite<Path> | File system provider for writing files |
| path | Path                               | Path to the file to write events to    |

**Usage**:

```kotlin
// Open a file and allow reading from and writing to it
val fs = JVMFileSystemProvider.ReadWrite
val path = Paths.get("/path/to/trace.log")
// Set the file as the target for the TraceFeatureMessageFileWriter class
val writer = TraceFeatureMessageFileWriter(fs, path)

// Use with Tracing
install(Tracing) {
    // Pass TraceFeatureMessageFileWriter to the addMessageProcessor method
    addMessageProcessor(writer) // Events are written to the file
}
```

### Class: `FeatureMessageRemoteWriter`

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

// Use with Tracing
install(Tracing) {
    // Pass FeatureMessageRemoteWriter to the addMessageProcessor method
    addMessageProcessor(writer) // Events are sent to the remote endpoint
}
```

## Internal helpers and utilities

### Event types

The Trace feature works with the following event types:

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

### Extension properties

Both writer implementations provide extension properties for formatting events:

```kotlin
// Adding a new read-only property `agentCreateEventFormat` to the `AgentCreateEvent` class
val AgentCreateEvent.agentCreateEventFormat
get() = "${this.eventId} (strategy name: ${this.strategyName})"

// Adding new read-only property `nodeExecutionStartEventFormat` to the `NodeExecutionStartEvent` class
val NodeExecutionStartEvent.nodeExecutionStartEventFormat
get() = "${this.eventId} (stage: ${this.stageName}, node: ${this.nodeName}, input: ${this.input})"

// Each property returns a formatted string representation of the event
```

## Error handling and edge cases

### No message processors

If no message processors are added to the Trace feature, a warning will be logged:

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

### Large trace volumes

For agents with complex strategies or long-running executions, the volume of trace events can be substantial. Consider:

1. Using specific message filters to reduce the number of events.
2. Implementing custom message processors with buffering or sampling.
3. Using file rotation for log files to prevent them from growing too large.

## Dependency graph

The Trace feature has the following dependencies:

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
    ├── LLMCallStartEvent
    ├── LLMCallEndEvent
    ├── LLMCallWithToolsStartEvent
    ├── LLMCallWithToolsEndEvent
    ├── ToolCallEvent
    ├── ToolCallResultEvent
    ├── ToolCallFailureEvent
    ├── AIAgentNodeExecutionStartEvent
    └── AIAgentNodeExecutionEndEvent
```

## Examples and quickstarts

### Basic tracing to logger

```kotlin
// Create a logger
val logger = LoggerFactory.create("my.agent.trace")

// Create an agent with tracing
val agent = createAgent(
    strategy = myStrategy,
    coroutineScope = coroutineScope,
) {
    install(Tracing) {
        addMessageProcessor(TraceFeatureMessageLogWriter(logger))
    }
}

// Run the agent
agent.run("Hello, agent!")
```

### Tracing specific events to file

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
val agent = createAgent(
    strategy = myStrategy,
    coroutineScope = coroutineScope,
) {
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

## FAQ and troubleshooting

The following section includes commonly asked questions and answers related to the Trace feature. 

### How do I trace only specific parts of my agent's execution?

Use the `messageFilter` property to filter events. For example, to trace only node execution:

```kotlin
install(Tracing) {
    messageFilter = { message ->
        message is NodeExecutionStartEvent || message is NodeExecutionEndEvent
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
install(Tracing) {
    addMessageProcessor(CustomTraceProcessor())
}
```
