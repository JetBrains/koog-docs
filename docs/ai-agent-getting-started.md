# Getting started

The AI Agent is a robust AI agent implementation that offers extensive flexibility and precise control for managing complex AI workflows.

## Prerequisites

- You have a valid API key from the LLM provider used for implementing an AI agent. For a list of all available providers, see [Overview](index.md).

!!! tip
    Use environment variables or a secure configuration management system to store your API keys.
    Avoid hardcoding API keys directly in your source code.

## Add dependencies

To use the AI Agent functionality, you need to add the following dependencies to your project:

```
// Please add installation instructions here
```

## Understanding Nodes and Edges

When creating a KotlinAIAgent, you define the workflow using nodes and edges.

Nodes represent processing steps in your agent's workflow:

```kotlin
val processNode by node<InputType, OutputType> { input ->
    // Process the input and return an output
    // You can use llm.writeSession to interact with the LLM
    // You can call tools using callTool, callToolRaw, etc.
    transformedOutput
}
```

Edges define the connections between nodes:

```kotlin
// Basic edge
edge(sourceNode forwardTo targetNode)

// Edge with condition
edge(sourceNode forwardTo targetNode onCondition { output ->
    // Return true to follow this edge, false to skip it
    output.contains("specific text")
})

// Edge with transformation
edge(sourceNode forwardTo targetNode transformed { output ->
    // Transform the output before passing it to the target node
    "Modified: $output"
})

// Combined condition and transformation
edge(sourceNode forwardTo targetNode onCondition { it.isNotEmpty() } transformed { it.uppercase() })
```

## Create an agent

Unlike agents created with the Simple API, agents built using the AI Agent require explicit configuration:

```kotlin
val agent = AIAgent(
    promptExecutor = promptExecutor,
    strategy = agentStrategy,
    agentConfig = agentConfig,
    toolRegistry = toolRegistry,
    installFeatures = installFeatures
)
```

To learn more about available configuration options, see API reference.<!--[TODO] Link to API reference-->

### 1. Create a custom prompt executor

Prompt executors manage and run prompts. You can create a custom prompt executor as follows:

```kotlin
fun createPromptExecutor(apiToken: String): PromptExecutor {
    val api = SuspendableAPIGatewayClient(
        grazieEnvironment.url,
        SuspendableHTTPClient.WithV5(
            SuspendableClientWithBackoff(
                GrazieKtorHTTPClient.Client.Default,
            ), AuthData(
                apiToken,
                grazieAgent = GrazieAgent("custom-agent-app", "dev")
            )
        )
    )

    return CodePromptExecutor(api, LLMParams())
}
```

### 2. Create a strategy

The strategy defines the workflow of your agent. Use the `strategy` function to create a custom multi-stage strategy:

```kotlin
val agentStrategy = strategy(
    name = "custom-agent-name",
    llmHistoryTransitionPolicy = ContextTransitionPolicy.PERSIST_LLM_HISTORY
) {
    // Define the main stage
    stage {
        // Define nodes for processing
        val processInput by node<String, String> { input ->
            // Process the input
            "Processed: $input"
        }

        val generateResponse by node<String, String> { processed ->
            // Generate a response using LLM
            llm.writeSession {
                requestLLM("Generate a response for: $processed")
            }
        }

        // Define the flow between nodes
        edge(nodeStart forwardTo processInput)
        edge(processInput forwardTo generateResponse)
        edge(generateResponse forwardTo nodeFinish)
    }

    // Optional: Define additional stages for different processing phases
    stage("refinement") {
        val refineOutput by node<String, String> { input ->
            llm.writeSession {
                requestLLM("Refine the following output: $input")
            }
        }

        edge(nodeStart forwardTo refineOutput)
        edge(refineOutput forwardTo nodeFinish)
    }
}
```

The `strategy` function allows you to define multiple stages, each with its own set of nodes and edges. This is more
powerful than using simplified strategy builders.

### 3. Set Up the Tool Registry

Tools allow your agent to perform specific actions:

```kotlin
val toolRegistry = SimpleToolRegistry {
    tool(YourCustomTool())
    // Add more tools as needed
}
```

For more complex scenarios, you can use multi-stage tool registries:

```kotlin
val toolRegistry = multiStageToolRegistry {
    statelessStage("initialStage", initialStageToolDescriptors) {
        tool("firstTool") { arguments: JsonObject ->
            // Tool implementation
            "Result of first tool"
        }
    }

    statelessStage("finalStage", finalStageToolDescriptors) {
        tool("secondTool") { arguments: JsonObject ->
            // Tool implementation
            "Result of second tool"
        }
    }
}
```

### 4. Configure the Agent

Define the agent's behavior with a configuration:

```kotlin
val agentConfig = LocalAgentConfig.withSystemPrompt(
    prompt = """
        You are an AI assistant with specific capabilities.
        Your task is to help users by utilizing your tools and knowledge.
        Always be concise and provide accurate information.
    """.trimIndent()
)
```

For more advanced configuration, you can pass the LLM parameters explicitly:

```kotlin
val agentConfig = LocalAgentConfig(
    systemPrompt = """
        You are an AI assistant with specific capabilities.
        Your task is to help users by utilizing your tools and knowledge.
        Always be concise and provide accurate information.
    """.trimIndent(),
    llmParams = LLMParams(
        temperature = 0.7,
        topP = 0.95,
        maxTokens = 2000
    ),
    historyCompressionStrategy = HistoryCompressionStrategy.NoCompression
)
```

### 5. Run the Agent

Execute the agent with an input:

```kotlin
agent.run("Your input or question here")
```

## Advanced Usage: Working with Structured Data

KotlinAIAgent can process structured data from LLM outputs. Please refer to the [streaming API guide](streaming-api.md)
for more information.

## Advanced Usage: Parallel Tool Calls

KotlinAIAgent supports parallel tool execution:

```kotlin
parseMarkdownStreamToBooks(markdownStream).toParallelToolCallsRaw(BookTool::class).collect()
```

This allows you to process multiple items concurrently, improving performance for independent operations.

## Complete Example

Here's a complete example of a KotlinAIAgent implementation:

```kotlin
fun main() = runBlocking {
    // Create a custom prompt executor
    val token = System.getenv("GRAZIE_TOKEN")
        ?: error("Environment variable GRAZIE_TOKEN is not set")

    val api = SuspendableAPIGatewayClient(
        GrazieEnvironment.Production.url,
        SuspendableHTTPClient.WithV5(
            SuspendableClientWithBackoff(
                GrazieKtorHTTPClient.Client.Default,
            ), AuthData(
                token,
                grazieAgent = GrazieAgent("library-assistant", "dev")
            )
        )
    )
    val promptExecutor = CodePromptExecutor(api, LLMParams())

    // Create a multi-stage strategy
    val agentStrategy = strategy("library-assistant") {
        stage {
            val processQuery by node<String, String> { query ->
                llm.writeSession {
                    val markdownStream = requestLLMStreaming(
                        "Generate information about books related to: $query"
                    )

                    parseMarkdownStreamToBooks(markdownStream).collect { book ->
                        callToolRaw("book", book)
                    }
                }
                "Query processed successfully"
            }

            edge(nodeStart forwardTo processQuery)
            edge(processQuery forwardTo nodeFinish)
        }
    }

    // Set up the tool registry
    val toolRegistry = SimpleToolRegistry {
        tool(BookTool())
    }

    // Configure the agent
    val agentConfig = LocalAgentConfig.withSystemPrompt(
        prompt = """
            You're an AI library assistant. Provide users with comprehensive 
            and structured information about books.
        """.trimIndent()
    )

    // Create and run the agent
    val agent = KotlinAIAgent(
        promptExecutor = promptExecutor,
        toolRegistry = toolRegistry,
        strategy = agentStrategy,
        agentConfig = agentConfig,
        cs = this,
    )

    agent.run("Please recommend science fiction books about space exploration.")
}
```

## Conclusion

KotlinAIAgent provides a powerful framework for building AI agents in Kotlin. By defining custom
strategies, tools, and configurations, you can create agents that handle complex workflows and provide rich, interactive
experiences.