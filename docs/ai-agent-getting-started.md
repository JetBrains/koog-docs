# Getting started

The AI Agent is a robust implementation that lets you build AI agents in Kotlin.
By defining custom strategies, tools, and configurations, you can create agents that handle complex workflows.

## Prerequisites

- You have a valid API key from the LLM provider used for implementing an AI agent. For a list of all available providers, see [Overview](index.md).

!!! tip
    Use environment variables or a secure configuration management system to store your API keys.
    Avoid hardcoding API keys directly in your source code.

## Add dependencies

To use the AI Agent functionality, you need to include all necessary dependencies in your build configuration. For example:

```
dependencies {
    implementation("ai.koog:koog-agents:VERSION")
}
```

For all available methods of installation, refer to [Installation](index.md#installation).

## Nodes and edges

When creating an agent using the AI Agent, you define a workflow using nodes and edges.

Nodes represent processing steps in your agent workflow:

```kotlin
val processNode by node<InputType, OutputType> { input ->
    // Process the input and return an output
    // You can use llm.writeSession to interact with the LLM
    // You can call tools using callTool, callToolRaw, etc.
    transformedOutput
}
```
!!! tip
    There are also pre-defined nodes that you can use in your agent strategies. To learn more, see [Predefined nodes and components](nodes-and-components.md).

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

To learn more about configuration options, see [API reference](https://api.koog.ai/agents/agents-core/ai.koog.agents.core.agent.config/-a-i-agent-config/index.html).

### 1. Provide a prompt executor

Prompt executors manage and run prompts.
You can choose a prompt executor based on the LLM provider you plan to use.
Also, you can create a custom prompt executor using one of the available LLM clients.
To check all available LLM clients and prompt executors, see [API reference](https://api.koog.ai/index.html).

For example, to provide the OpenAI prompt executor, you need to call the `simpleOpenAIExecutor` function and provide it with the API key required for authentication with the OpenAI service:

```kotlin
val promptExecutor = simpleOpenAIExecutor(token)
```

To create a prompt executor that works with multiple LLM providers, do the following:

1. Configure clients for the required LLM providers with the corresponding API keys. For example:
```kotlin
val openAIClient = OpenAILLMClient(System.getenv("OPENAI_KEY"))
val anthropicClient = AnthropicLLMClient(System.getenv("ANTHROPIC_KEY"))
val googleClient = GoogleLLMClient(System.getenv("GOOGLE_KEY"))
```
2. Pass the configured clients to the `DefaultMultiLLMPromptExecutor` class constructor to create a prompt executor with multiple LLM providers:
```kotlin
val multiExecutor = DefaultMultiLLMPromptExecutor(openAIClient, anthropicClient, googleClient)
```

### 2. Create a strategy

A strategy defines the workflow of your agent. To create the strategy, call the `strategy` function and define nodes and edges.
For example:

```kotlin
val agentStrategy = strategy("Simple calculator") {
    // Define nodes for the strategy
    val nodeSendInput by nodeLLMRequest()
    val nodeExecuteTool by nodeExecuteTool()
    val nodeSendToolResult by nodeLLMSendToolResult()

    // Define edges between nodes
    // Start -> Send input
    edge(nodeStart forwardTo nodeSendInput)

    // Send input -> Finish
    edge(
        (nodeSendInput forwardTo nodeFinish)
                transformed { it }
                onAssistantMessage { true }
    )

    // Send input -> Execute tool
    edge(
        (nodeSendInput forwardTo nodeExecuteTool)
                onToolCall { true }
    )

    // Execute tool -> Send the tool result
    edge(nodeExecuteTool forwardTo nodeSendToolResult)

    // Send the tool result -> finish
    edge(
        (nodeSendToolResult forwardTo nodeFinish)
                transformed { it }
                onAssistantMessage { true }
    )
}
```
!!! tip
    The `strategy` function lets you define multiple subgraphs, each containing its own set of nodes and edges.
    This approach offers more flexibility and functionality compared to using simplified strategy builders.
    To learn more about subgraphs, see [Subgraphs](subgraphs-overview.md).

### 3. Configure the agent

Define the agent behavior with a configuration:

```kotlin
val agentConfig = AIAgentConfig.withSystemPrompt(
    prompt = """
        You are a simple calculator assistant.
        You can add two numbers together using the calculator tool.
        When the user provides input, extract the numbers they want to add.
        The input might be in various formats like "add 5 and 7", "5 + 7", or just "5 7".
        Extract the two numbers and use the calculator tool to add them.
        Always respond with a clear, friendly message showing the calculation and result.
        """.trimIndent()
)
```

For more advanced configuration, you can specify which LLM will be used by the agent and set the maximum number of iterations the agent can perform to respond:

```kotlin
val agentConfig = AIAgentConfig(
    prompt = Prompt.build("simple-calculator") {
        system(
            """
                You are a simple calculator assistant.
                You can add two numbers together using the calculator tool.
                When the user provides input, extract the numbers they want to add.
                The input might be in various formats like "add 5 and 7", "5 + 7", or just "5 7".
                Extract the two numbers and use the calculator tool to add them.
                Always respond with a clear, friendly message showing the calculation and result.
                """.trimIndent()
        )
    },
    model = OpenAIModels.Chat.GPT4o,
    maxAgentIterations = 10
)
```

### 4. Implement tools and set up a tool registry

Tools let your agent perform specific tasks. To make a tool available for the agent, you need to add it to a tool registry. For example:

```kotlin
// Implement s simple calculator tool that can add two numbers
public object CalculatorTool : Tool<CalculatorTool.Args, ToolResult>() {
    @Serializable
    data class Args(
        val num1: Int,
        val num2: Int
    ) : Tool.Args

    @Serializable
    data class Result(
        val sum: Int
    ) : ToolResult {
        override fun toStringDefault(): String {
            return "The sum is: $sum"
        }
    }

    override val argsSerializer = Args.serializer()

    override val descriptor = ToolDescriptor(
        name = "calculator",
        description = "Add two numbers together",
        requiredParameters = listOf(
            ToolParameterDescriptor(
                name = "num1",
                description = "First number to add",
                type = ToolParameterType.Integer
            ),
            ToolParameterDescriptor(
                name = "num2",
                description = "Second number to add",
                type = ToolParameterType.Integer
            )
        )
    )

    override suspend fun execute(args: Args): Result {
        // Perform a simple addition operation
        val sum = args.num1 + args.num2
        return Result(sum)
    }
}

// Create the tool to the tool registry
val toolRegistry = ToolRegistry {
    tool(CalculatorTool)
}
```

To learn more about tools, see [Tools](tools.md).

### 5. Install features

Agent features let you add new capabilities to the agent, modify its behavior, provide access to external systems and resources,
and log and monitor events during the agent workflow.
The following features are available:

- EventHandler
- AgentMemory
- Tracing

To install the feature, you need to call the `install` function and provide the feature as an argument.
For example, to install the event handler feature, you need to do the following:

```kotlin
installFeatures = {
    install(EventHandler) {
        onBeforeAgentStarted = { strategy: AIAgentStrategy, agent: AIAgent ->
            println("Starting strategy: ${strategy.name}")
        }
        onAgentFinished = { strategyName: String, result: String? ->
            println("Result: $result")
        }
    }
}
```

To learn more about feature configuration, see the dedicated page.

### 6. Run the agent

Create the agent with the configuration option created on the previous stages and run it with a provided input:

```kotlin
val agent = AIAgent(
    promptExecutor = promptExecutor,
    strategy = agentStrategy,
    agentConfig = agentConfig,
    toolRegistry = toolRegistry,
    installFeatures = {
        install(EventHandler) {
            onBeforeAgentStarted = { strategy: AIAgentStrategy, agent: AIAgent ->
                println("Starting strategy: ${strategy.name}")
            }
            onAgentFinished = { strategyName: String, result: String? ->
                println("Result: $result")
            }
        }
    }
)

suspend fun main() {
    println("Enter two numbers to add (e.g., 'add 5 and 7' or '5 + 7'):")
    
    // Read the user input and send it to the agent
    val userInput = readlnOrNull() ?: ""
    agent.run(userInput)
}
```

## Work with structured data

The AI Agent can process structured data from LLM outputs. For more details, see [Streaming API](streaming-api.md).

## Use parallel tool calls

The AI Agent supports parallel tool calls. This lets you process multiple tools concurrently, improving performance for independent operations.

For more details, see [Parallel tool calls](tools.md#parallel-tool-calls).

## Full code sample

Here is the complete implementation of the agent:

```kotlin
// Use the OpenAI executor with an API key from an environment variable
val promptExecutor = simpleOpenAIExecutor(System.getenv("OPENAI_KEY"))

// Create a simple strategy
val agentStrategy = strategy("Simple calculator") {
    // Define nodes for the strategy
    val nodeSendInput by nodeLLMRequest()
    val nodeExecuteTool by nodeExecuteTool()
    val nodeSendToolResult by nodeLLMSendToolResult()

    // Define edges between nodes
    // Start -> Send input
    edge(nodeStart forwardTo nodeSendInput)

    // Send input -> Finish
    edge(
        (nodeSendInput forwardTo nodeFinish)
                transformed { it }
                onAssistantMessage { true }
    )

    // Send input -> Execute tool
    edge(
        (nodeSendInput forwardTo nodeExecuteTool)
                onToolCall { true }
    )

    // Execute tool -> Send the tool result
    edge(nodeExecuteTool forwardTo nodeSendToolResult)

    // Send the tool result -> finish
    edge(
        (nodeSendToolResult forwardTo nodeFinish)
                transformed { it }
                onAssistantMessage { true }
    )
}

// Configure the agent
val agentConfig = AIAgentConfig(
    prompt = Prompt.build("simple-calculator") {
        system(
            """
                You are a simple calculator assistant.
                You can add two numbers together using the calculator tool.
                When the user provides input, extract the numbers they want to add.
                The input might be in various formats like "add 5 and 7", "5 + 7", or just "5 7".
                Extract the two numbers and use the calculator tool to add them.
                Always respond with a clear, friendly message showing the calculation and result.
                """.trimIndent()
        )
    },
    model = OpenAIModels.Chat.GPT4o,
    maxAgentIterations = 10
)

// Implement s simple calculator tool that can add two numbers
public object CalculatorTool : Tool<CalculatorTool.Args, ToolResult>() {
    @Serializable
    data class Args(
        val num1: Int,
        val num2: Int
    ) : Tool.Args

    @Serializable
    data class Result(
        val sum: Int
    ) : ToolResult {
        override fun toStringDefault(): String {
            return "The sum is: $sum"
        }
    }

    override val argsSerializer = Args.serializer()

    override val descriptor = ToolDescriptor(
        name = "calculator",
        description = "Add two numbers together",
        requiredParameters = listOf(
            ToolParameterDescriptor(
                name = "num1",
                description = "First number to add",
                type = ToolParameterType.Integer
            ),
            ToolParameterDescriptor(
                name = "num2",
                description = "Second number to add",
                type = ToolParameterType.Integer
            )
        )
    )

    override suspend fun execute(args: Args): Result {
        // Perform a simple addition operation
        val sum = args.num1 + args.num2
        return Result(sum)
    }
}

// Create the tool to the tool registry
val toolRegistry = ToolRegistry {
    tool(CalculatorTool)
}

// Create the agent
val agent = AIAgent(
    promptExecutor = promptExecutor,
    strategy = agentStrategy,
    agentConfig = agentConfig,
    toolRegistry = toolRegistry,
    installFeatures = {
        // install the EventHandler feature
        install(EventHandler) {
            onBeforeAgentStarted = { strategy: AIAgentStrategy, agent: AIAgent ->
                println("Starting strategy: ${strategy.name}")
            }
            onAgentFinished = { strategyName: String, result: String? ->
                println("Result: $result")
            }
        }
    }
)

suspend fun main() {
    println("Enter two numbers to add (e.g., 'add 5 and 7' or '5 + 7'):")

    val userInput = readlnOrNull() ?: ""
    agent.run(userInput)
}
```