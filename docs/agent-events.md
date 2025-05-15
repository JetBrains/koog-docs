## Event Handler

The **EventHandler** serves as an event delegation mechanism that:

- Manages the lifecycle of AI agent operations
- Provides hooks for monitoring and responding to different stages of execution
- Enables error handling and recovery
- Facilitates tool invocation tracking and result processing

### Key Components

The EventHandler entity consists of five main handler types:

- `InitHandler`: Executes at the initialization of an agent run
- `ResultHandler`: Processes successful results from agent operations
- `ErrorHandler`: Handles exceptions and errors that occur during execution
- `ToolCallListener`: Notifies when a tool is about to be invoked
- `ToolResultListener`: Processes the results after a tool has been called

### Creating an event handler

The EventHandler uses the Builder pattern to create instances with specific handler configurations:

```kotlin
val eventHandler = EventHandler {
    handleInit { /* initialization logic */ }
    handleResult { result -> /* process result */ }
    handleError { error -> /* handle error */ }
    onToolCall { stage, tool, args -> /* before tool execution */ }
    afterToolCalled { stage, tool, args, result -> /* after tool execution */ }
}
```

Then it can be passed to the agent as one of the fields in the constructor:

```kotlin
val agent = KotlinAIAgent(
        toolRegistry = toolRegistry,
        strategy = strategy,
        eventHandler = eventHandler,
        promptExecutor = simpleGrazieExecutor(API_TOKEN),
        agentConfig = agentConfig,
        cs = this
    )
```