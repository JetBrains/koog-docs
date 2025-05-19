## Event Handler

The **EventHandler** feature serves as an event delegation mechanism that:

- Manages the lifecycle of AI agent operations
- Provides hooks for monitoring and responding to different stages of execution
- Enables error handling and recovery
- Facilitates tool invocation tracking and result processing

### Key Components

The EventHandler configuration consists of five main handler types:

- `InitHandler`: Executes at the initialization of an agent run
- `ResultHandler`: Processes successful results from agent operations
- `ErrorHandler`: Handles exceptions and errors that occur during execution
- `ToolCallListener`: Notifies when a tool is about to be invoked
- `ToolResultListener`: Processes the results after a tool has been called

### Adding an event handler to your agent

The EventHandler is a feature that can be added to your agent 

```kotlin
val agent = AIAgent(
    promptExecutor = simpleOpenAIExecutor(API_TOKEN),
    toolRegistry = toolRegistry,
    strategy = strategy,
    agentConfig = agentConfig,
) {
    handleEvents {
        onAgentFinished = { strategyName: String, result: String? -> /* process result */ }
        onAgentRunError = { strategyName: String, throwable: Throwable -> /* handle error */ }
        onToolCall = { tool: Tool<*, *>, toolArgs: Tool.Args -> /* before tool execution */ }
    }
}
```

Or you may prefer to use an equivalent version with explicit feature installation: 

```kotlin
val agent = AIAgent(
    promptExecutor = simpleOpenAIExecutor(API_TOKEN),
    toolRegistry = toolRegistry,
    strategy = strategy,
    agentConfig = agentConfig,
) {
    install(EventHandler) {
        onAgentFinished = { strategyName: String, result: String? -> /* process result */ }
        onAgentRunError = { strategyName: String, throwable: Throwable -> /* handle error */ }
        onToolCall = { tool: Tool<*, *>, toolArgs: Tool.Args -> /* before tool execution */ }
    }
}
```