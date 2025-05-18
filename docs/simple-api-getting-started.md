# Getting started

The Simple API provides an easy way to create and run AI agents.
It offers an interface that lets you create chat agents and single-run agents with customizable tools and configurations:

- A chat agent maintains a conversation until the session ends. This agent uses tools instead of plain text responses.

- A single-run agent processes a single input and provides a response. This agent can return either a message or a tool result.
The tool result is returned if the tool registry is provided to the agent.

## Prerequisites

- You have a valid API key from the LLM provider used for implementing an AI agent. For a list of all available providers, see [Overview](index.md).

!!! tip
    Use environment variables or a secure configuration management system to store your API keys.
    Avoid hardcoding API keys directly in your source code.

## Add dependencies

To use the Simple API functionality, you need to add the following dependencies to your project:

```
// Please add installation instructions here
```
## Create an agent

### Create a chat agent

A chat agent maintains a conversation with the user until the session is explicitly terminated:

```kotlin
fun main() = runBlocking {
    val apiToken = System.getenv("OPEN_AI_API_KEY")

    val agent = simpleChatAgent(
        apiToken = apiToken,
        systemPrompt = "You are a helpful assistant. Answer user questions concisely."
    )
    agent.run("Hello, how can you help me?")
}
```
!!! note
      The `simpleChatAgent` always uses the `AskUser` and `ExitTool` built-in tools by default, whether custom tools are provided or not when creating the agent.
      If the custom tools are provided, they are combined with these built-in tools.

For details, see [Configuration options](simple-api-configuration.md) and [Available tools](simple-api-available-tools.md).

### Create a single-run agent

A single-run agent processes a single input and provides a response:

```kotlin
fun main() = runBlocking {
    val apiToken = System.getenv("OPEN_AI_API_KEY")

    val agent = simpleSingleRunAgent(
        apiToken = apiToken,
        systemPrompt = "You are a code assistant. Provide concise code examples."
    )

    agent.run("Write a Kotlin function to calculate factorial")
}
```

## Configure the agent

You can configure the agent by passing optional parameters, such as tools or an event handler.
For details, see [Configuration options](simple-api-configuration.md).

### Provide tools

The Simple API provides a set of built-in tools along with the ability to implement your own custom tools.

The following example demonstrates how to pass the built-in `SayToUser` tool to the chat agent:

```kotlin
fun main() = runBlocking {
    val apiToken = System.getenv("YOUR_API_TOKEN")

    val toolRegistry = SimpleToolRegistry {
        tools(
            listOf(SayToUser)
        )
    }

    val agent = simpleChatAgent(
        apiToken = apiToken,
        toolRegistry = toolRegistry,
        systemPrompt = "You are a helpful assistant. Answer user questions concisely."
    )
    agent.run("Hello, how can you help me?")
}
```

For more details about tools, see [Available tools](simple-api-available-tools.md).

### Handle events during agent runtime

Simple agents support custom event handlers.
While having an event handler is not required for creating an agent, it might be helpful for testing, debugging, or making hooks for chained agent interactions.

For more information on how to use the `EventHandler` class for monitoring your agent interactions, see [Agent events](agent-events.md).
