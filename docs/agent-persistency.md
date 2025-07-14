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

<!--- INCLUDE
/*
KNIT ignore this example
-->
<!--- SUFFIX
*/
-->
```kotlin
require(ctx.strategy.metadata.uniqueNames) {
    "Checkpoint feature requires unique node names in the strategy metadata"
}
```

<!--- KNIT example-agent-persistency-01.kt -->

Make sure to set unique names for nodes in your graph.

## Installation

To use the Agent Persistency feature, add it to your agent's configuration:

<!--- INCLUDE
import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.snapshot.feature.Persistency
import ai.koog.agents.snapshot.providers.InMemoryPersistencyStorageProvider
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.llm.OllamaModels

val executor = simpleOllamaAIExecutor()
-->

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

<!--- KNIT example-agent-persistency-02.kt -->

## Configuration options

The Agent Persistency feature has two main configuration options:

- **Storage provider**: the provider used to save and retrieve checkpoints.
- **Continuous persistence**: automatic creation of checkpoints after each node is run.

### Storage provider

Set the storage provider that will be used to save and retrieve checkpoints:

<!--- INCLUDE
import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.snapshot.feature.Persistency
import ai.koog.agents.snapshot.providers.InMemoryPersistencyStorageProvider
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.llm.OllamaModels

val agent = AIAgent(
    executor = simpleOllamaAIExecutor(),
    llmModel = OllamaModels.Meta.LLAMA_3_2,
) {
-->
<!--- SUFFIX 
} 
-->

```kotlin
install(Persistency) {
    storage = InMemoryPersistencyStorageProvider()
}
```

<!--- KNIT example-agent-persistency-03.kt -->


The framework includes the following built-in providers:

- `InMemoryPersistencyStorageProvider`: stores checkpoints in memory (lost when the application restarts).
- `FilePersistencyStorageProvider`: persists checkpoints to the file system.
- `NoPersistencyStorageProvider`: a no-op implementation that does not store checkpoints. This is the default provider.

You can also implement custom storage providers by implementing the `PersistencyStorageProvider` interface.
For more information, see [Custom storage providers](#custom-storage-providers).

### Continuous persistence

Continuous persistence means that a checkpoint is automatically created after each node is run.
To activate continuous persistence, use the code below:

<!--- INCLUDE
import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.snapshot.feature.Persistency
import ai.koog.agents.snapshot.providers.InMemoryPersistencyStorageProvider
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.llm.OllamaModels

val agent = AIAgent(
    executor = simpleOllamaAIExecutor(),
    llmModel = OllamaModels.Meta.LLAMA_3_2,
) {
-->
<!--- SUFFIX 
} 
-->

```kotlin
install(Persistency) {
    enableAutomaticPersistency = true
}
```

<!--- KNIT example-agent-persistency-04.kt -->

When activated, the agent will automatically create a checkpoint after each node is executed,
allowing for fine-grained recovery.

## Basic usage

### Creating a checkpoint

To learn how to create a checkpoint at a specific point in your agent's execution, see the code sample below:

<!--- INCLUDE
import ai.koog.agents.core.agent.context.AIAgentContextBase
import ai.koog.agents.snapshot.feature.persistency

const val inputData = "some-input-data"
-->

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

<!--- KNIT example-agent-persistency-05.kt -->

### Restoring from a checkpoint

To restore the state of an agent from a specific checkpoint, follow the code sample below:

<!--- INCLUDE
import ai.koog.agents.core.agent.context.AIAgentContextBase
import ai.koog.agents.snapshot.feature.persistency
-->

```kotlin
suspend fun example(context: AIAgentContextBase, checkpointId: String) {
    // Roll back to a specific checkpoint
    context.persistency().rollbackToCheckpoint(checkpointId, context)

    // Or roll back to the latest checkpoint
    context.persistency().rollbackToLatestCheckpoint(context)
}
```

<!--- KNIT example-agent-persistency-06.kt -->

### Using extension functions

The Agent Persistency feature provides convenient extension functions for working with checkpoints:

<!--- INCLUDE
import ai.koog.agents.core.agent.context.AIAgentContextBase
import ai.koog.agents.snapshot.feature.persistency
import ai.koog.agents.snapshot.feature.withPersistency

const val inputData = "some-input-data"
-->

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
<!--- KNIT example-agent-persistency-07.kt -->

## Advanced usage

### Custom storage providers

You can implement custom storage providers by implementing the `PersistencyStorageProvider` interface:

<!--- INCLUDE
import ai.koog.agents.snapshot.feature.AgentCheckpointData
import ai.koog.agents.snapshot.providers.PersistencyStorageProvider

/*
// KNIT: Ignore example
-->
<!--- SUFFIX
*/
-->
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

<!--- KNIT example-agent-persistency-08.kt -->

To use your custom provider in the feature configuration, set it as the storage when configuring the Agent Persistency
feature in your agent.

<!--- INCLUDE
import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.example.exampleAgentPersistency08.MyCustomStorageProvider
import ai.koog.agents.snapshot.feature.Persistency
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.llm.OllamaModels

val agent = AIAgent(
    executor = simpleOllamaAIExecutor(),
    llmModel = OllamaModels.Meta.LLAMA_3_2,
) {
-->
<!--- SUFFIX 
} 
-->

```kotlin
install(Persistency) {
    storage = MyCustomStorageProvider()
}
```

<!--- KNIT example-agent-persistency-09.kt -->

### Setting execution points

For advanced control, you can directly set the execution point of an agent:

<!--- INCLUDE
import ai.koog.agents.core.agent.context.AIAgentContextBase
import ai.koog.agents.snapshot.feature.persistency
import ai.koog.prompt.message.Message.User

const val customInput = "custom-input"
val customMessageHistory = emptyList<User>()
-->

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

<!--- KNIT example-agent-persistency-10.kt -->

This allows for more fine-grained control over the agent's state beyond just restoring from checkpoints.
