# Simple API Quickstart Guide

## Overview

The SimpleAPI provides an easy way to create and run AI agents using the JetBrains AI API. It offers an
interface for creating chat agents and single-run agents with customizable tools and configurations.

The SimpleAPI uses two types of agents:

1. **Chat Agent**:
    - Maintains a conversation until the ExitTool is called
    - Enforces tool usage instead of plain text responses

2. **Single-Run Agent**
    - Processes a single input and provides a response
    - Can return either a message or a tool result (if the tool registry is passed to the agent)

## Prerequisites

- JetBrains AI API token
- Kotlin project with coroutine support

**Tip**: Never hardcode API tokens in your code. Use environment variables or secure configuration
management.

## Installation

Add the Code Engine dependencies to your project:

```kotlin
dependencies {
    implementation("ai.grazie:code-agents-local:VERSION")
    // Other dependencies as needed
}
```

## Basic Usage

**Tip**: Provide clear and concise system prompts to guide the agent's behavior.

### Creating a Chat Agent

A chat agent maintains a conversation with the user until explicitly terminated:

```kotlin
fun main() = runBlocking {
    val apiToken = "YOUR_API_TOKEN"

    val agent = simpleChatAgent(
        apiToken = apiToken,
        cs = this,
        systemPrompt = "You are a helpful assistant. Answer user questions concisely."
    )
    agent.run("Hello, how can you help me?")
}
```

**NB:** `simpleChatAgent` uses `AskUser` and `Exit` built-in tools by default. This means, they are used even if no
`toolRegistry` parameter is passed when creating the
agent. If the custom tool registry is passed under the `toolRegistry` parameter, the mentioned built-in tools are
used **in a combination** with the added tool registry. This means you can't create the `simpleChatAgent` without the
`AskUser` and `Exit` built-in tools.

### Creating a Single-Run Agent

A single-run agent processes a single input and provides a response:

```kotlin
fun main() = runBlocking {
    val apiToken = "YOUR_API_TOKEN"

    val agent = simpleSingleRunAgent(
        apiToken = apiToken,
        cs = this,
        systemPrompt = "You are a code assistant. Provide concise code examples."
    )

    agent.run("Write a Kotlin function to calculate factorial")
}
```

**NB:** `simpleSingleRunAgent` uses no tools by default. If no `toolRegistry` parameter with built-in or custom tools is
passed when creating the agent, only plain text responses are received.

## Available Tools

### Built-in Tools

The SimpleAPI provides the following built-in tools:

1. **SayToUser**: Allows the agent to output a message to the user
    - Prints the agent's message to the console with "Agent says: " prefix
    - Tool name: `__say_to_user__`

2. **AskUser**: Allows the agent to ask the user for input
    - Prints the agent's message to the console
    - Reads user input and returns it to the agent
    - Tool name: `__ask_user__`

3. **ExitTool**: Allows the agent to end the conversation
    - Used in chat agents to terminate the session
    - Tool name: `__exit__`

### Custom Tools

You can create custom tools by extending the `SimpleTool` class and register them in a tool registry and pass it to the
created agent. Please find more information on how to create the custom tools on
the [corresponding page](customTool.md).

## Handling events during the agent's run

Simple agents support custom event handlers. While having an event handler is not required for creating an agent, it
might be helpful for testing, debugging, or making hooks for chained agent interactions.

On the page [Handling Agent Events](eventHandler.md) you'll find more information on how to use the `EventHandler` class
for monitoring your agent's
interactions.

## Configuration Options

If your goal is to create a simple agent to experiment with, you can use any of the  `simpleChatAgent` and
`simpleSingleRunAgent` with required parameters only: the API token from the JetBrains AI platform and the coroutine
scope (`cs = this` if you don't have multiple coroutines).

However, if you want more flexibility and customization, you can pass optional params as well to configure the agent
your way.

Both `simpleChatAgent` and `simpleSingleRunAgent` accept the following parameters:

**Required:**

- `apiToken`: Your JetBrains AI API token
- `cs`: CoroutineScope for running the agent. Of you don't need to set up a complicated corutine interaction, you can
  pass the current one into it: `cs = this`

**Optional:**

- `systemPrompt`: the system instruction to guide the agent's behavior (default: `empty string`)
- `llmModel`: a specific LLM to use (default: `OpenAI GPT 4o Mini`)
- `temperature`: temperature for LLM generation (default: `1.0`)
- `eventHandler`: custom mechanism to manage the agent operations lifecycle (default: empty handler)
- `toolRegistry`: the list of tools that you'd like your agent to be able to use (default: built-in tools for chat
  agent, empty for single-run agent)
- `maxIterations`: maximum number of the steps agent can take before it's forced to stop (default: `50`)
- `apiUrl`: JetBrains AI API URL (default: `https://api.jetbrains.ai`)

## Example: Creating a Code Assistant

```kotlin
object GenerateCodeTool : SimpleTool<GenerateCodeTool.Args>() {
    @Serializable
    data class Args(val language: String, val task: String) : Tool.Args

    override val argsSerializer = Args.serializer()

    override val descriptor = ToolDescriptor(
        name = "generate_code",
        description = "Generates code in the specified language for the given task",
        requiredParameters = listOf(
            ToolParameterDescriptor(
                name = "language",
                description = "Programming language (e.g., Kotlin, Java, Python)",
                type = ToolParameterType.String
            ),
            ToolParameterDescriptor(
                name = "task",
                description = "Description of the coding task",
                type = ToolParameterType.String
            )
        )
    )

    override suspend fun doExecute(args: Args): String {
        // In a real implementation, this might call another API or service
        return "Generated code for ${args.task} in ${args.language}"
    }
}

fun main() = runBlocking {
    val apiToken = System.getenv("JETBRAINS_AI_API_TOKEN")
    val coroutineScope = CoroutineScope(Dispatchers.Default)

    val toolRegistry = ToolRegistry {
        stage {
            tool(GenerateCodeTool)
        }
    }

    val agent = simpleChatAgent(
        apiToken = apiToken,
        cs = coroutineScope,
        systemPrompt = "You are a code assistant. Use the generate_code tool to create code examples.",
        toolRegistry = toolRegistry
    )

    agent.run("I need help creating a function to sort a list in Kotlin")

    // Wait for the agent to complete
    delay(Long.MAX_VALUE)
}
```