# TraceFeature Documentation

## Feature Overview

The TraceFeature is a powerful monitoring and debugging tool for AI agents in the Kotlin AI platform. It provides comprehensive tracing capabilities that capture detailed information about agent execution, including:

- Agent creation and initialization
- Strategy execution
- LLM (Language Learning Model) calls
- Tool invocations
- Node execution within the agent graph

This feature operates by intercepting key events in the agent pipeline and forwarding them to configurable message processors. These processors can output the trace information to various destinations such as log files or the filesystem, enabling developers to gain insights into agent behavior and troubleshoot issues effectively.

### Architecture

The TraceFeature follows a modular architecture with these key components:

1. **TraceFeature**: The main feature class that intercepts events in the agent pipeline
2. **TraceFeatureConfig**: Configuration class for customizing feature behavior
3. **Message Processors**: Components that process and output trace events
   - **TraceFeatureMessageLogWriter**: Writes trace events to a logger
   - **TraceFeatureMessageFileWriter**: Writes trace events to a file

### Event Flow

1. The TraceFeature intercepts events in the agent pipeline
2. Events are filtered based on the configured message filter
3. Filtered events are passed to registered message processors
4. Message processors format and output the events to their respective destinations

## Public API Documentation

### TraceFeature

**Location**: `ai.grazie.code.agents.local.features.tracing.feature.TraceFeature`

**Description**: Main feature class that intercepts events in the agent pipeline and forwards them to message processors.

**Usage**:
```kotlin
install(TraceFeature) {
    messageFilter = { true }  // Accept all messages
    addMessageProcessor(TraceFeatureMessageLogWriter(logger))
}
```

### TraceFeatureConfig

**Location**: `ai.grazie.code.agents.local.features.tracing.feature.TraceFeatureConfig`

**Description**: Configuration class for the TraceFeature.

**Properties**:

| Name | Type | Default | Description |
|------|------|---------|-------------|
| messageFilter | (FeatureMessage) -> Boolean | { true } | Filter function that determines which messages are processed |

**Methods**:

| Name | Parameters | Return Type | Description |
|------|------------|-------------|-------------|
| addMessageProcessor | processor: FeatureMessageProcessor | Unit | Adds a message processor to the feature |

**Usage**:
```kotlin
install(TraceFeature) {
    messageFilter = { message -> 
        message is LLMCallWithToolsStartEvent || message is LLMCallWithToolsEndEvent
    }
    addMessageProcessor(TraceFeatureMessageLogWriter(logger))
}
```

### TraceFeatureMessageLogWriter

**Location**: `ai.grazie.code.agents.local.features.tracing.writer.TraceFeatureMessageLogWriter`

**Description**: Writes trace events to a logger.

**Constructor Parameters**:

| Name | Type | Description |
|------|------|-------------|
| targetLogger | MPPLogger | The logger to write events to |

**Usage**:
```kotlin
val logger = LoggerFactory.create("my.trace.logger")
val writer = TraceFeatureMessageLogWriter(logger)

// Use with TraceFeature
install(TraceFeature) {
    addMessageProcessor(writer)
}
```

### TraceFeatureMessageFileWriter

**Location**: `ai.grazie.code.agents.local.features.tracing.writer.TraceFeatureMessageFileWriter`

**Description**: Writes trace events to a file.

**Constructor Parameters**:

| Name | Type | Description |
|------|------|-------------|
| fs | FileSystemProvider.ReadWrite<Path> | File system provider for writing files |
| path | Path | Path to the file to write events to |

**Usage**:
```kotlin
val fs = JVMFileSystemProvider.ReadWrite
val path = Paths.get("/path/to/trace.log")
val writer = TraceFeatureMessageFileWriter(fs, path)

// Use with TraceFeature
install(TraceFeature) {
    addMessageProcessor(writer)
}
```

## Internal Helpers & Utilities

### Event Types

The TraceFeature works with the following event types:

| Event Type | Description |
|------------|-------------|
| AgentCreateEvent | Triggered when an agent is created |
| StrategyStartEvent | Triggered when a strategy execution starts |
| LLMCallStartEvent | Triggered before a simple LLM call |
| LLMCallEndEvent | Triggered after a simple LLM call |
| LLMCallWithToolsStartEvent | Triggered before an LLM call with tools |
| LLMCallWithToolsEndEvent | Triggered after an LLM call with tools |
| ToolCallsStartEvent | Triggered before tool calls |
| ToolCallsEndEvent | Triggered after tool calls |
| NodeExecutionStartEvent | Triggered before a node execution |
| NodeExecutionEndEvent | Triggered after a node execution |

### Extension Properties

Both writer implementations provide extension properties for formatting events:

```kotlin
val AgentCreateEvent.agentCreateEventFormat
    get() = "${this.eventId} (strategy name: ${this.strategyName})"

val NodeExecutionStartEvent.nodeExecutionStartEventFormat
    get() = "${this.eventId} (stage: ${this.stageName}, node: ${this.nodeName}, input: ${this.input})"
```

## Configuration & Initialization

### Basic Setup

To use the TraceFeature, you need to:

1. Create one or more message processors
2. Install the TraceFeature in your agent
3. Configure the message filter (optional)
4. Add the message processors to the feature

```kotlin
val agent = createAgent(
    strategy = myStrategy,
    coroutineScope = coroutineScope,
) {
    install(TraceFeature) {
        // Optional: Configure message filter
        messageFilter = { message -> 
            // Accept only specific message types
            message is NodeExecutionStartEvent || message is NodeExecutionEndEvent
        }

        // Add message processors
        addMessageProcessor(TraceFeatureMessageLogWriter(logger))
        addMessageProcessor(TraceFeatureMessageFileWriter(fs, path))
    }
}
```

### Message Filtering

The message filter allows you to control which events are processed. This is useful for focusing on specific aspects of agent execution:

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

## Error Handling & Edge Cases

### No Message Processors

If no message processors are added to the TraceFeature, a warning will be logged:

```
Tracing Feature. No feature out stream providers are defined. Trace streaming has no target.
```

The feature will still intercept events, but they won't be processed or output anywhere.

### Resource Management

Message processors may hold resources (like file handles) that need to be properly released. Use the `use` extension function to ensure proper cleanup:

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

The TraceFeature has the following dependencies:

```
TraceFeature
├── AIAgentPipeline (for intercepting events)
├── TraceFeatureConfig
│   └── FeatureConfig
├── Message Processors
│   ├── TraceFeatureMessageLogWriter
│   │   └── FeatureMessageLogWriter
│   └── TraceFeatureMessageFileWriter
│       └── FeatureMessageFileWriter
└── Event Types (from ai.grazie.code.agents.local.features.common.model)
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
