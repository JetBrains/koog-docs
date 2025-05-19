## Creating and configuring stages

### Basic stage creation

Stages are typically created using the `stage` function within a strategy:

```kotlin
strategy("my-strategy") {
    stage(
        name = "my-stage",
        tools = listOf(myTool1, myTool2)
    ) {
        // Define nodes and edges for this stage
        val sendInput by nodeLLMRequest()
        val executeToolCall by nodeExecuteTool()
        val sendToolResult by nodeLLMSendToolResult()

        edge(nodeStart forwardTo sendInput)
        edge(sendInput forwardTo executeToolCall onToolCall { true })
        edge(executeToolCall forwardTo sendToolResult)
        edge(sendToolResult forwardTo nodeFinish onAssistantMessage { true })
    }
}
```

### Static vs. dynamic stages

When creating a stage, you can select between static and dynamic stages:

- **Static stage**: Specify a list of tools that the stage requires.

```kotlin
stage(
    name = "static-stage",
    tools = listOf(myTool1, myTool2)
) {
    // Stage definition
}
```

- **Dynamic stage**: Don't specify any tools, allowing the stage to use whatever tools are available.

```kotlin
stage(name = "dynamic-stage") {
    // Stage definition
}
```

### Configuring stage tools

Tools can be configured for a stage in several ways:

* Directly in the stage definition:
```kotlin
stage(
    name = "my-stage",
    tools = listOf(myTool1, myTool2)
) {
    // Stage definition
}
```

* From a tool registry:
```kotlin
stage(
    name = "my-stage",
    tools = toolRegistry.stagesToolDescriptors.getValue("my-stage")
) {
    // Stage definition
}
```

* Dynamically during execution:
```kotlin
// Make a set of tools
val newTools = context.llm.writeSession {
    val selectedTools = this.requestLLMStructured<SelectedTools>(/*...*/)
    tools.filter { it.name in selectedTools.structure.tools.toSet() }
}

// Pass the tools to the stage context
val context = context.copyWithTools(newTools)
```

## History passing between stages

### LLM history transition policies

The Kotlin Agentic Framework provides three policies for handling LLM conversation history between stages:

1. **PERSIST_LLM_HISTORY**: Keeps the entire conversation history intact between stages. This is the default policy and
   is useful when context continuity is important and the history size is manageable.

2. **COMPRESS_LLM_HISTORY**: Compresses the conversation history between stages to reduce token usage while preserving
   essential context. This is useful for long-running agents with large conversation histories.

3. **CLEAR_LLM_HISTORY**: Completely clears the conversation history between stages. This is useful when stages are
   independent and the previous context might confuse the next stage.

These policies are specified when creating a strategy:

```kotlin
AIAgentStrategy(
    name = "my-strategy",
    stages = listOf(stage1, stage2, stage3),
    llmHistoryTransitionPolicy = ContextTransitionPolicy.COMPRESS_LLM_HISTORY
)
```

### Passing history between stages

History is passed between stages through the LLM context:

1. Each stage receives an LLM context that includes the prompt (conversation history).
2. The stage executes its nodes, potentially modifying the prompt.
3. After the stage completes, the updated prompt is extracted from the context:

```kotlin
currentPrompt = context.llm.readSession { prompt }
```

The updated prompt is passed to the next stage.

If an intermediate stage is inserted based on the history transition policy, it will modify the prompt before it is
passed to the next main stage.

### Managing history size

For long-running conversations, the history can grow large and consume a lot of tokens. There are several ways to manage
history size:

* Using the `COMPRESS_LLM_HISTORY` policy. This automatically inserts a compression stage between each main stage.

* Using the `nodeLLMCompressHistory` node: This lets you compress history at specific points within a stage.
```kotlin
val compressHistory by nodeLLMCompressHistory<Message.Tool.Result>()

edge(
    nodeStart forwardTo nodeCallLLM
            onCondition { _ -> 
                llm.readSession { prompt.messages.size > 100 } 
            }
)
```

* Using a custom compression logic. This lets you implement a custom logic to compress or filter the history.
```kotlin
val customCompression by node<Unit, Unit> {
    llm.writeSession {
        // Custom compression logic
    }
}
```

## Working with stage context

### LLM sessions

The LLM context provides two types of sessions:

* Write session: For modifying the prompt and tools.
```kotlin
llm.writeSession {
    updatePrompt {
        user("New user message")
    }

    val response = requestLLM()
    // The response is automatically added to the prompt
}
```

* Read session: For reading the prompt and tools without modifying them.
```kotlin
llm.readSession {
    val messageCount = prompt.messages.size
    val availableTools = tools.map { it.name }
}
```

### Running tools in context

Tools can be run within a write session:

```kotlin
llm.writeSession {
    // Call a tool by reference
    val result = callTool(myTool, myArgs)

    // Call a tool by name
    val result2 = callTool("myToolName", myArgs)

    // Call a tool by class
    val result3 = callTool(MyTool::class, myArgs)

    // Execute multiple tools in parallel
    parseDataToArgs(data).toParallelToolCalls(MyTool::class).collect { result ->
        // Process each result
    }
}
```

## Advanced stage techniques

### Multi-stage strategies

Complex workflows can be broken down into multiple stages, each handling a specific part of the process:

```kotlin
strategy("complex-workflow") {
    stage("input-processing") {
        // Process the initial input
    }

    stage("reasoning") {
        // Perform reasoning based on the processed input
    }

    stage("tool-execution") {
        // Execute tools based on the reasoning
    }

    stage("response-generation") {
        // Generate a response based on the tool results
    }
}
```

### Intermediate stages

You can insert intermediate stages between main stages for specific purposes using the `insertIntermediateStage` method:

```kotlin
val stages = listOf(stage1, stage2, stage3)
val intermediateStage = createLoggingStage()
val stagesWithLogging = insertIntermediateStage(stages, intermediateStage)
// Result: [stage1, intermediateStage, stage2, intermediateStage, stage3]
```

### Custom history handling

For advanced history management, you can create custom stages that handle history in specific ways:

```kotlin
val customHistoryStage = with(AIAgentStageBuilder("custom-history", tools = null)) {
    val processHistory by node<Unit, Unit> {
        llm.writeSession {
            // Custom history processing logic
            val messages = prompt.messages
            val filteredMessages = messages.filter { /* custom filtering logic */ }
            prompt = prompt.copy(messages = filteredMessages)
        }
    }

    edge(nodeStart forwardTo processHistory)
    edge(processHistory forwardTo nodeFinish transformed { stageInput })

    build()
}
```

## Best practices

When working with stages and history passing, follow these best practices:

1. **Use appropriate history transition policies**: Choose the policy that best balances context preservation with token
   efficiency for your use case.

2. **Break complex workflows into stages**: Each stage should have a clear, focused responsibility.

3. **Monitor history size**: For long-running conversations, monitor the history size and compress when necessary.

4. **Use static stages when possible**: Static stages provide better validation of required tools.

5. **Pass only necessary context**: Only pass the information that subsequent stages need to function correctly.

6. **Document stage dependencies**: Clearly document what each stage expects from previous stages and what it provides
   to subsequent stages.

7. **Test stages in isolation**: Ensure each stage works correctly with various inputs before integrating them into a
   strategy.

8. **Consider token usage**: Be mindful of token usage, especially when passing large histories between stages.

## Troubleshooting

### History not being parsed correctly

If history is not being passed correctly between stages:

- Check that you're using the correct history transition policy.
- Verify that the prompt is being correctly extracted from the context after each stage.
- Ensure that intermediate stages are not inadvertently clearing or corrupting the history.

### Stage tools not available

If tools are not available in a stage:

- For static stages, ensure that all required tools are provided in the stage definition.
- Check that the tools are correctly registered in the tool registry.
- Verify that the context has access to the tools.

### History too large

If the history becomes too large:

- Use the `COMPRESS_LLM_HISTORY` policy or add explicit compression nodes.
- Consider using more aggressive compression strategies.
- Break the workflow into more stages with clear history boundaries.

### Stages not executing in expected order

If stages are not executing in the expected order:

- Check the strategy definition to ensure stages are listed in the correct order.
- Verify that each stage is correctly passing its output to the next stage.
- Ensure that no stage is short-circuiting the execution flow.

### Context properties not available

If context properties are not available:

- Ensure that the context is being correctly passed to each node.
- Check that you're accessing properties with the correct names.
- Verify that the properties are being initialized correctly in the context.
- Make sure you're not trying to access properties that are only available in certain contexts.

## Examples

Here are some examples of how stages and history passing are used in real-world scenarios:

### Multi-stage processing with history compression

This example shows a strategy with multiple stages and history compression between stages:

```kotlin
fun complexProcessingStrategy(): AIAgentStrategy {
    val stage1 = with(AIAgentStageBuilder("input-processing", tools = listOf(inputTool))) {
        val processInput by nodeLLMRequest()
        val executeInputTool by nodeExecuteTool()
        val sendToolResult by nodeLLMSendToolResult()

        edge(nodeStart forwardTo processInput)
        edge(processInput forwardTo executeInputTool onToolCall { true })
        edge(executeInputTool forwardTo sendToolResult)
        edge(sendToolResult forwardTo nodeFinish onAssistantMessage { true })

        build()
    }

    val stage2 = with(AIAgentStageBuilder("reasoning", tools = listOf(reasoningTool))) {
        val processReasoning by nodeLLMRequest()
        val executeReasoningTool by nodeExecuteTool()
        val sendToolResult by nodeLLMSendToolResult()

        edge(nodeStart forwardTo processReasoning)
        edge(processReasoning forwardTo executeReasoningTool onToolCall { true })
        edge(executeReasoningTool forwardTo sendToolResult)
        edge(sendToolResult forwardTo nodeFinish onAssistantMessage { true })

        build()
    }

    val stage3 = with(AIAgentStageBuilder("output-generation", tools = listOf(outputTool))) {
        val generateOutput by nodeLLMRequest()
        val executeOutputTool by nodeExecuteTool()
        val sendToolResult by nodeLLMSendToolResult()

        edge(nodeStart forwardTo generateOutput)
        edge(generateOutput forwardTo executeOutputTool onToolCall { true })
        edge(executeOutputTool forwardTo sendToolResult)
        edge(sendToolResult forwardTo nodeFinish onAssistantMessage { true })

        build()
    }

    return AIAgentStrategy(
        name = "complex-processing",
        stages = listOf(stage1, stage2, stage3),
        llmHistoryTransitionPolicy = ContextTransitionPolicy.COMPRESS_LLM_HISTORY
    )
}
```

### Custom history handling between stages

This example shows a strategy with custom history handling between stages:

```kotlin
fun customHistoryStrategy(): AIAgentStrategy {
    val stage1 = with(AIAgentStageBuilder("first-stage", tools = listOf(firstTool))) {
        // First stage definition
        build()
    }

    val historyProcessingStage = with(AIAgentStageBuilder("history-processing", tools = null)) {
        val processHistory by node<Unit, Unit> {
            llm.writeSession {
                // Extract important information from history
                val messages = prompt.messages
                val importantMessages = messages.filter { message ->
                    message is Message.User ||
                            (message is Message.Assistant && message.content.contains("important"))
                }

                // Create a new prompt with only important messages
                prompt = prompt.copy(messages = prompt.messages.take(1) + importantMessages)
            }
        }

        edge(nodeStart forwardTo processHistory)
        edge(processHistory forwardTo nodeFinish transformed { stageInput })

        build()
    }

    val stage2 = with(AIAgentStageBuilder("second-stage", tools = listOf(secondTool))) {
        // Second stage definition
        build()
    }

    return AIAgentStrategy(
        name = "custom-history",
        stages = listOf(stage1, historyProcessingStage, stage2),
        llmHistoryTransitionPolicy = ContextTransitionPolicy.PERSIST_LLM_HISTORY
    )
}
```

### Conditional history compression within a stage

This example shows how to conditionally compress history within a stage based on the history size:

```kotlin
fun conditionalCompressionStrategy(): AIAgentStrategy = strategy("conditional-compression") {
    stage("main-stage", tools = listOf(myTool1, myTool2)) {
        val sendInput by nodeLLMRequest()
        val executeToolCall by nodeExecuteTool()
        val sendToolResult by nodeLLMSendToolResult()
        val compressHistory by nodeLLMCompressHistory<Message.Tool.Result>()

        edge(nodeStart forwardTo sendInput)
        edge(sendInput forwardTo executeToolCall onToolCall { true })

        // If history is too large, compress it before sending the tool result
        edge(
            (executeToolCall forwardTo compressHistory)
                    onCondition { _ -> 
                        llm.readSession { prompt.messages.size > 50 } 
                    }
        )
        edge(compressHistory forwardTo sendToolResult)

        // Otherwise, send the tool result directly
        edge(
            (executeToolCall forwardTo sendToolResult)
                    onCondition { _ -> 
                        llm.readSession { prompt.messages.size <= 50 } 
                    }
        )

        edge(sendToolResult forwardTo nodeFinish onAssistantMessage { true })
        edge(sendToolResult forwardTo executeToolCall onToolCall { true })
    }
}
```
