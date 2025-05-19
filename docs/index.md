# Overview

Koog is a Kotlin-based framework designed to create and run AI agents locally without external
services. It provides a pure Kotlin implementation for building intelligent agents that can interact with
tools, handle complex workflows, and communicate with users.

The framework offers two main approaches:

* [Simple API](simple-api-getting-started): A high-level, user-friendly interface that lets you quickly create chat agents and single-run agents with minimal configuration.
* [AI Agent](ai-agent-getting-started): A more flexible, full-featured framework for building custom agents with advanced capabilities.

## Key features

Key features of Koog include:

- A pure Kotlin implementation that lets you create and run AI agents entirely in Kotlin without relying on external service dependencies.
- A modular and composable feature system that lets you extend AI agent capabilities.
- The ability to create custom tools that give agents access to external systems and resources.
- Support for both conversational agents and single-query (one-shot) agents.
- The ability to intercept and modify agent behavior at different stages of operation.
- Optional persistent memory support for agents through a separate module.

# Available LLM providers and platforms

We support the following LLM providers and platforms whose LLMs you can use to power your agent capabilities:

- Google
- OpenAI
- Anthropic
- OpenRouter
- Ollama
- LightLLM

# Installation

```
// Please add installation instructions here
```

# Quickstart example

The [Simple API](simple-api-getting-started) provides the easiest way to get started with AI agents:
!!! note
Before you run the example, assign a corresponding API key as the `YOUR_API_TOKEN` environment variable. For details, see [Getting started](simple-api-getting-started.md).

```kotlin
fun main() = runBlocking {
    val apiToken = System.getenv("YOUR_API_TOKEN")

    val agent = simpleChatAgent(
        executor = simpleOpenAIExecutor(apiToken),
        systemPrompt = "You are a helpful assistant. Answer user questions concisely."
    )
    agent.run("Hello, how can you help me?")
}
```

