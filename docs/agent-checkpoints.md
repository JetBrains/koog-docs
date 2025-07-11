# Agent Persistency

AgentCheckpoint is a feature that provides checkpoint functionality for AI agents in the Koog framework. It allows 
saving and restoring the state of an agent at specific points during execution, enabling capabilities such as:

- Resuming agent execution from a specific point
- Rolling back to previous states
- Persisting agent state across sessions

## Key concepts

### Checkpoints

A checkpoint captures the complete state of an agent at a specific point in its execution, including:

- Message history (all interactions between user, system, assistant, and tools)
- Current node being executed
- Input data for the current node
- Timestamp of creation

Checkpoints are identified by unique IDs and are associated with a specific agent.

### Storage providers

AgentCheckpoint uses storage providers to save and retrieve checkpoints. The framework includes several built-in providers:

- `InMemoryPersistencyStorageProvider`: stores checkpoints in memory (lost when the application restarts).
- `FilePersistencyStorageProvider`: persists checkpoints to the file system.
- `NoPersistencyStorageProvider`: a no-op implementation that does not store checkpoints. This is the default provider.

You can also implement custom storage providers by implementing the `PersistencyStorageProvider` interface.

### Continuous persistence

The feature can be configured to automatically create checkpoints after each node execution, ensuring that the agent's state is continuously persisted and can be recovered at any point.

## Prerequisites

The AgentCheckpoint feature requires that all nodes in your agent's strategy have unique names. This is enforced when the feature is installed:

```kotlin
require(ctx.strategy.metadata.uniqueNames) { 
    "Checkpoint feature requires unique node names in the strategy metadata" 
}
```

Make sure to set `uniqueNames = true` in your strategy metadata when using this feature.

## Installation

To use the AgentCheckpoint feature, you need to add it to your agent's configuration:

```kotlin
val agent = AIAgent {
    // Agent configuration
    
    features {
        install(AgentCheckpoint) {
            // Configure the feature
            snapshotProvider(InMemoryAgentCheckpointStorageProvider())
            // Create checkpoints after each node (optional)
            continuouslyPersistent() 
        }
    }
}
```

## Configuration options

The AgentCheckpoint feature has two main configuration options:

### Storage provider

Set the storage provider that will be used to save and retrieve checkpoints:

```kotlin
install(AgentCheckpoint) {
    snapshotProvider(InMemoryAgentCheckpointStorageProvider())
}
```

Available built-in providers:

- `InMemoryAgentCheckpointStorageProvider`: in-memory storage (non-persistent)
- `FileAgentCheckpointStorageProvider`: file-based storage
- `NoAgentCheckpointStorageProvider`: no-op provider (default)

### Continuous persistence

Enable automatic checkpoint creation after each node execution:

```kotlin
install(AgentCheckpoint) {
    continuouslyPersistent()
}
```

When enabled, the agent will automatically create a checkpoint after each node is executed, allowing for fine-grained recovery.

## Basic usage

### Creating a checkpoint

Create a checkpoint at a specific point in your agent's execution:

```kotlin
suspend fun example(context: AIAgentContextBase) {
    // Create a checkpoint with the current state
    val checkpoint = context.checkpoint().createCheckpoint(
        agentId = context.id,
        agentContext = context,
        nodeId = "current-node-id",
        lastInput = inputData
    )
    
    // The checkpoint ID can be stored for later use
    val checkpointId = checkpoint.checkpointId
}
```

### Restoring from a checkpoint

Restore an agent's state from a specific checkpoint:

```kotlin
suspend fun example(context: AIAgentContextBase, checkpointId: String) {
    // Roll back to a specific checkpoint
    context.checkpoint().rollbackToCheckpoint(checkpointId, context)
    
    // Or roll back to the latest checkpoint
    context.checkpoint().rollbackToLatestCheckpoint(context)
}
```

### Using extension functions

The feature provides convenient extension functions for working with checkpoints:

```kotlin
suspend fun example(context: AIAgentContextBase) {
    // Access the checkpoint feature
    val checkpointFeature = context.checkpoint()
    
    // Or perform an action with the checkpoint feature
    context.withCheckpoints(context) { ctx ->
        // 'this' is the checkpoint feature
        createCheckpoint(ctx.id, ctx, "node-id", inputData)
    }
}
```

## Advanced usage

### Custom storage providers

You can implement custom storage providers by implementing the `AgentCheckpointStorageProvider` interface:

```kotlin
class MyCustomStorageProvider : AgentCheckpointStorageProvider {
    override suspend fun getCheckpoints(agentId: String): List<AgentCheckpointData> {
        // Implementation
    }
    
    override suspend fun saveCheckpoint(agentCheckpointData: AgentCheckpointData) {
        // Implementation
    }
    
    override suspend fun getLatestCheckpoint(agentId: String): AgentCheckpointData? {
        // Implementation
    }
}
```

Then use your custom provider in the feature configuration:

```kotlin
install(AgentCheckpoint) {
    snapshotProvider(MyCustomStorageProvider())
}
```

### Setting execution points

For advanced control, you can directly set the execution point of an agent:

```kotlin
fun example(context: AIAgentContextBase) {
    context.checkpoint().setExecutionPoint(
        agentContext = context,
        nodeId = "target-node-id",
        messageHistory = customMessageHistory,
        input = customInput
    )
}
```

This allows for more fine-grained control over the agent's state beyond just restoring from checkpoints.