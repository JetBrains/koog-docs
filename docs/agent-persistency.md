# Agent Persistency

Agent Persistency is a feature that provides checkpoint functionality for AI agents in the Koog framework. 
It lets you save and restore the state of an agent at specific points during execution, enabling capabilities such as:

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

## Prerequisites

The Agent Persistency feature requires that all nodes in your agent's strategy have unique names.
This is enforced when the feature is installed:

```kotlin
require(ctx.strategy.metadata.uniqueNames) { 
    "Checkpoint feature requires unique node names in the strategy metadata" 
}
```

Make sure to set unique names for nodes in your graph.

## Installation

To use the Agent Persistency feature, add it to your agent's configuration:

```kotlin
val agent = AIAgent(
        executor = executor,
        llmModel = OllamaModels.Meta.LLAMA_3_2,
    ) {
        install(Persistency) {
            // Use in-memory storage for snapshots
            storage = InMemoryPersistencyStorageProvider()
            // Enable automatic persistency
            enableAutomaticPersistency = true 
        }
    }
```

## Configuration options

The Agent Persistency feature has two main configuration options:

- **Storage provider**: the provider used to save and retrieve checkpoints.
- **Continuous persistence**: automatic creation of checkpoints after each node is run.

### Storage provider

Set the storage provider that will be used to save and retrieve checkpoints:

```kotlin
install(Persistency) {
    storage = InMemoryPersistencyStorageProvider()
}
```

The framework includes the following built-in providers:

- `InMemoryPersistencyStorageProvider`: stores checkpoints in memory (lost when the application restarts).
- `FilePersistencyStorageProvider`: persists checkpoints to the file system.
- `NoPersistencyStorageProvider`: a no-op implementation that does not store checkpoints. This is the default provider.

You can also implement custom storage providers by implementing the `PersistencyStorageProvider` interface. 
For more information, see [Custom storage providers](#custom-storage-providers).

### Continuous persistence

Continuous persistence means that a checkpoint is automatically created after each node is run. 
To activate continuous persistence, use the code below:

```kotlin
install(Persistency) {
    enableAutomaticPersistency = true
}
```

When activated, the agent will automatically create a checkpoint after each node is executed, 
allowing for fine-grained recovery.

## Basic usage

### Creating a checkpoint

To learn how to create a checkpoint at a specific point in your agent's execution, see the code sample below:

```kotlin
suspend fun example(context: AIAgentContextBase) {
    // Create a checkpoint with the current state
    val checkpoint = context.persistency().createCheckpoint(
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

To restore the state of an agent from a specific checkpoint, follow the code sample below:

```kotlin
suspend fun example(context: AIAgentContextBase, checkpointId: String) {
    // Roll back to a specific checkpoint
    context.persistency().rollbackToCheckpoint(checkpointId, context)
    
    // Or roll back to the latest checkpoint
    context.persistency().rollbackToLatestCheckpoint(context)
}
```

### Using extension functions

The Agent Persistency feature provides convenient extension functions for working with checkpoints:

```kotlin
suspend fun example(context: AIAgentContextBase) {
    // Access the checkpoint feature
    val checkpointFeature = context.persistency()
    
    // Or perform an action with the checkpoint feature
    context.withPersistency(context) { ctx ->
        // 'this' is the checkpoint feature
        createCheckpoint(ctx.id, ctx, "node-id", inputData)
    }
}
```

## Advanced usage

### Custom storage providers

You can implement custom storage providers by implementing the `PersistencyStorageProvider` interface:

```kotlin
class MyCustomStorageProvider : PersistencyStorageProvider {
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

To use your custom provider in the feature configuration, set it as the storage when configuring the Agent Persistency
feature in your agent.

```kotlin
install(Persistency) {
   storage = MyCustomStorageProvider()
}
```

### Setting execution points

For advanced control, you can directly set the execution point of an agent:

```kotlin
fun example(context: AIAgentContextBase) {
    context.persistency().setExecutionPoint(
        agentContext = context,
        nodeId = "target-node-id",
        messageHistory = customMessageHistory,
        input = customInput
    )
}
```

This allows for more fine-grained control over the agent's state beyond just restoring from checkpoints.