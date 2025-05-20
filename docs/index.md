# Overview

Koog is a Kotlin-based framework designed to create and run AI agents locally without external
services. It provides a pure Kotlin implementation for building intelligent agents that can interact with
tools, handle complex workflows, and communicate with users.

The framework offers two main approaches:

* [Simple API](simple-api-getting-started): A high-level, user-friendly interface that lets you quickly create chat agents and single-run agents with minimal configuration.
* [AI Agent](ai-agent-getting-started): A more flexible, full-featured framework for building custom agents with advanced capabilities.

## Key features

Key features of Koog include:

- **Pure Kotlin Implementation**: Build AI agents entirely in Kotlin with no external dependencies, giving you full control.
- **MCP Integration**: Connect to Model Control Protocol for enhanced model management.
- **Embedding Capabilities**: Use vector embeddings for semantic search and knowledge retrieval.
- **Custom Tool Creation**: Extend your agents with tools that access external systems and APIs.
- **Ready-to-Use Components**: Speed up development with pre-built solutions for common AI engineering challenges.
- **Intelligent History Compression**: Optimize token usage while maintaining conversation context using various pre-built strategies.
- **Powerful Streaming API**: Process responses in real-time with Markdown streams support and parallel tool calls.
- **Persistent Agent Memory**: Enable knowledge retention across sessions and even different agents.
- **Comprehensive Tracing**: Debug and monitor agent execution with detailed and configurable tracing.
- **Flexible Graph Workflows**: Design complex agent behaviors using intuitive graph-based workflows.
- **Modular Feature System**: Customize agent capabilities through a composable architecture.
- **Scalable Architecture**: Handle workloads from simple chatbots to enterprise applications.
- **Multiplatform**: Run agents on both JVM and JS targets with Kotlin Multiplatform.

# Available LLM providers and platforms

We support the following LLM providers and platforms whose LLMs you can use to power your agent capabilities:

- Google
- OpenAI
- Anthropic
- OpenRouter
- Ollama

# Installation

```
dependencies {
    implementation("ai.koog.agents:koog-agents:VERSION")
}
```

# Quickstart example

The [Simple API](simple-api-getting-started) provides the easiest way to get started with AI agents:

!!! note
    Before you run the example, assign a corresponding API key as the `YOUR_API_TOKEN` environment variable. For details, see [Getting started](simple-api-getting-started.md).

```kotlin
fun main() {
    val apiToken = System.getenv("YOUR_API_TOKEN")

    val agent = simpleChatAgent(
        executor = simpleOpenAIExecutor(apiToken),
        systemPrompt = "You are a helpful assistant. Answer user questions concisely.",
        llmModel = OpenAIModels.Chat.GPT4o
    )
    agent.run("Hello, how can you help me?")
}
```
