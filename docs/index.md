# Introduction to Kotlin AI Agents

Kotlin AI agents is a Kotlin-based framework for creating and running AI agents locally without requiring external
services. It provides a pure Kotlin implementation for building intelligent agents that can interact with
tools, handle complex workflows, and communicate with users.

The module offers two main approaches:

1. **[Simple API](quickstartSimpleApi)**: A high-level, user-friendly interface for quickly creating chat agents and
   single-run agents with minimal configuration.
2. **[Kotlin AI Agent](quickstartKotlinAgent)**: A more flexible, feature-rich framework for building custom agents with advanced capabilities.

## Key Features

- **Pure Kotlin Implementation**: Build and run AI agents entirely in Kotlin without external service dependencies
- **Modular Feature System**: Extend agent capabilities through a composable feature system
- **Tool Integration**: Create and use custom tools to give agents access to external systems and resources
- **Conversation Management**: Support for both conversational agents and one-shot query agents
- **Pipeline Interceptors**: Intercept and modify agent behavior at various stages of execution
- **Memory Support**: Optional persistent memory capabilities for agents (via separate module)

## Installation

Add the Code Agents Local dependency to your project:

```kotlin
dependencies {
    implementation("ai.grazie:code-agents-local:VERSION")
}
```

## Quick Start With a Simple API

The [SimpleAPI](quickstartSimpleApi) provides the easiest way to get started with AI agents:

#### Chat Agent Example

```kotlin
fun main() = runBlocking {
    val apiToken = "YOUR_JETBRAINS_AI_API_TOKEN"

    val agent = simpleChatAgent(
        apiToken = apiToken,
        cs = this,
        systemPrompt = "You are a helpful assistant. Answer user questions concisely."
    )

    agent.run("Hello, how can you help me?")
}
```

# Glossary

- **Agent**: A Kotlin-based AI entity that can interact with tools, handle complex workflows, and communicate with
  users. The framework offers two main approaches: Simple API and Kotlin AI Agent.

- **Concept** (Memory): A category of information with associated metadata in the Memory Feature, including a keyword,
  description, and fact type.

- **Context**: The environment in which LLM interactions occur, providing access to the conversation history and
  tools.

- **Edge**: A connection between nodes in an agent graph that defines the flow of execution, often with conditions
  specifying when to follow each edge.

- **Event Handler**: A component that processes events generated during agent execution, such as tool calls, LLM
  responses, and errors.

- **Fact** (Memory): An individual piece of information stored in memory, which can be a SingleFact (single value) or
  MultipleFacts (multiple values).

- **Feature**: A component that extends and enhances the functionality of AI agents.

- **Graph**: A structure of nodes connected bu edges that defines the execution flow of an agent strategy.

- **History Compression**: The process of reducing the size of conversation history to manage token usage, implemented
  through strategies like WholeHistory, FromLastNMessages, or Chunked.

- **LLM (Language Learning Model)**: The underlying AI model that powers agent capabilities.

- **Memory**: A feature that enables AI agents to store, retrieve, and utilize information across
  conversations.

- **Memory Scope**: The context in which facts are relevant (Agent, Feature, Product, Organization, CrossProduct).

- **Memory Subject**: Entities that facts can be associated with (USER, MACHINE, PROJECT, ORGANIZATION).

- **Message**: A unit of communication in the agent system and the name of the corresponding class representing data
  passed from User, Assistant, or System.

- **Node**: A fundamental building block of agent workflows, representing a specific operation or transformation.

- **Prompt**: The conversation history provided to the LLM, consisting of messages from the user, assistant, and system.

- **Session**: A context for interacting with a language model, encapsulating conversation history, available tools,
  and methods for making requests.

- **Stage**: A self-contained unit of processing within an agent strategy, with its own set of tools, context, and
  responsibilities. The information about the stage execution can be encapsulated within a stage or passed between
  stages (using the Memory Feature).

- **Strategy**: A defined workflow for an agent, consisting of stages that are executed sequentially. The strategy can
  be visualized as a directed graph with nodes representing stages and edges representing transitions between them.

- **System Prompt**: Instructions provided to the agent to guide its behavior and define its role. They can be also used
  to provide context for the agent.

- **Tool**: A function that an agent can use to perform specific tasks or access external systems. The agent knows the
  list of available tools and their arguments but is unaware of the implementation details.

- **Tool Call**: A request from the LLM to execute a specific tool with provided arguments. It works like a function
  call in program code.

- **Tool Descriptor**: Metadata about a tool, including its name, description, and parameter information.

- **Tool Registry**: A list of tools that are available to an agent, organized by stage. The registry is a way of
  letting the agent know which tools are available.

- **Tool Result**: The output produced by executing a tool. E.g. if the tool is a method, the result is the return
  value.