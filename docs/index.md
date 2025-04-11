# Introduction to Kotlin AI Agents

Kotlin AI agents is a Kotlin-based framework for creating and running AI agents locally without requiring external services like IdeFormer. It provides a pure Kotlin implementation for building intelligent agents that can interact with tools, handle complex workflows, and communicate with users.

The module offers two main approaches:

1. **Simple API**: A high-level, user-friendly interface for quickly creating chat agents and single-run agents with minimal configuration.
2. **Kotlin AI Agent**: A more flexible, feature-rich framework for building custom agents with advanced capabilities.

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

## Quick Start
The SimpleAPI provides the easiest way to get started with AI agents:

#### Chat Agent Example

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

## Creating Custom Tools

You can extend agent capabilities by creating custom tools:

```kotlin
object CalculatorTool : SimpleTool<CalculatorTool.Args>() {
    @Serializable
    data class Args(val expression: String) : Tool.Args

    override val argsSerializer = Args.serializer()

    override val descriptor = ToolDescriptor(
        name = "calculator",
        description = "Evaluates a mathematical expression",
        requiredParameters = listOf(
            ToolParameterDescriptor(
                name = "expression",
                description = "Mathematical expression to evaluate",
                type = ToolParameterType.String
            )
        )
    )

    override suspend fun doExecute(args: Args): String {
        // Implement expression evaluation
        return "Result: ${evaluateExpression(args.expression)}"
    }

    private fun evaluateExpression(expression: String): Double {
        // Simple implementation for demonstration
        return expression.toDoubleOrNull() ?: 0.0
    }
}
```

## Best Practices

1. **System Prompts**: Provide clear and concise system prompts to guide the agent's behavior.
2. **Error Handling**: Implement proper error handling in custom tools to prevent agent failures.
3. **Tool Design**: Design tools with clear descriptions and parameter names to help the LLM understand how to use them.
4. **Resource Management**: Use appropriate coroutine scopes and cancel them when no longer needed to avoid resource leaks.
5. **API Token Security**: Never hardcode API tokens in your code. Use environment variables or secure configuration management.

## Comparison with IdeFormer

Unlike the IdeFormer approach which requires a Python-based microservice, Code Agents Local provides a pure Kotlin implementation for building AI agents. This makes it easier to integrate into Kotlin projects without additional service dependencies.

Key differences:
- **No External Service**: Runs entirely within your Kotlin application
- **Simplified Setup**: No need to download and manage external executables
- **Direct Integration**: Integrates directly with JetBrains AI API
- **Feature-Based Architecture**: Modular design for extending agent capabilities

Choose Code Agents Local when you want a lightweight, pure Kotlin solution for AI agents without the overhead of managing external services.