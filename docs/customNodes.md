# Custom Node Implementation Guide

This guide provides detailed instructions on how to implement your own custom nodes in the Kotlin AI platform. Custom nodes allow you to extend the functionality of agent workflows by creating reusable components that perform specific operations.

## Introduction

Nodes are the fundamental building blocks of agent workflows in the Kotlin AI platform. Each node represents a specific operation or transformation in the workflow, and they can be connected using edges to define the flow of execution.

Custom nodes allow you to encapsulate complex logic into reusable components that can be easily integrated into different agent workflows. This guide will walk you through the process of implementing your own custom nodes.

## Node Architecture Overview

Before diving into implementation details, it's important to understand the architecture of nodes in the Kotlin AI platform:

- **LocalAgentNode**: The abstract base class for all nodes. It defines the core structure and behavior of a node.
- **SimpleLocalAgentNode**: A concrete implementation of LocalAgentNode that executes a provided function.
- **LocalAgentNodeDelegate**: A delegate class that handles lazy initialization of nodes.
- **LocalAgentSubgraphBuilderBase**: Provides the `node` function for creating nodes in a DSL-like manner.

Nodes are connected using edges, which define the flow of execution between nodes. Each node has an execute method that takes an input and produces an output, which is then passed to the next node in the workflow.

## Implementing a Custom Node

### Basic Node Implementation

The simplest way to implement a custom node is to create an extension function on `LocalAgentSubgraphBuilderBase` that calls the `node` function:

```kotlin
fun <T> LocalAgentSubgraphBuilderBase<*, *>.myCustomNode(
    name: String? = null
): LocalAgentNodeDelegate<T, T> = node(name) { input ->
    // Custom logic here
    input // Return the input as output (pass-through)
}
```

This creates a pass-through node that performs some custom logic but returns the input as output without modification.

### Parameterized Nodes

You can create nodes that accept parameters to customize their behavior:

```kotlin
fun <T> LocalAgentSubgraphBuilderBase<*, *>.myParameterizedNode(
    name: String? = null,
    param1: String,
    param2: Int
): LocalAgentNodeDelegate<T, T> = node(name) { input ->
    // Use param1 and param2 in your custom logic
    input // Return the input as output
}
```

### Stateful Nodes

If your node needs to maintain state between executions, you can use closure variables:

```kotlin
fun <T> LocalAgentSubgraphBuilderBase<*, *>.myStatefulNode(
    name: String? = null
): LocalAgentNodeDelegate<T, T> {
    var counter = 0
    
    return node(name) { input ->
        counter++
        println("Node executed $counter times")
        input
    }
}
```

## Node Input and Output Types

Nodes can have different input and output types, which are specified as generic parameters:

```kotlin
fun LocalAgentSubgraphBuilderBase<*, *>.stringToIntNode(
    name: String? = null
): LocalAgentNodeDelegate<String, Int> = node(name) { input: String ->
    input.toInt() // Convert string to integer
}
```

The input and output types determine how the node can be connected to other nodes in the workflow. Nodes can only be connected if the output type of the source node is compatible with the input type of the target node.

## Best Practices

When implementing custom nodes, follow these best practices:

1. **Keep nodes focused**: Each node should perform a single, well-defined operation.
2. **Use descriptive names**: Node names should clearly indicate their purpose.
3. **Document parameters**: Provide clear documentation for all parameters.
4. **Handle errors gracefully**: Implement proper error handling to prevent workflow failures.
5. **Make nodes reusable**: Design nodes to be reusable across different workflows.
6. **Use type parameters**: Use generic type parameters when appropriate to make nodes more flexible.
7. **Provide default values**: When possible, provide sensible default values for parameters.

## Common Patterns

Here are some common patterns for implementing custom nodes:

### Pass-Through Nodes

Nodes that perform an operation but return the input as output:

```kotlin
fun <T> LocalAgentSubgraphBuilderBase<*, *>.loggingNode(
    name: String? = null
): LocalAgentNodeDelegate<T, T> = node(name) { input ->
    println("Processing input: $input")
    input // Return the input as output
}
```

### Transformation Nodes

Nodes that transform the input into a different output:

```kotlin
fun LocalAgentSubgraphBuilderBase<*, *>.upperCaseNode(
    name: String? = null
): LocalAgentNodeDelegate<String, String> = node(name) { input ->
    input.uppercase() // Transform input to uppercase
}
```

### LLM Interaction Nodes

Nodes that interact with the LLM:

```kotlin
fun LocalAgentSubgraphBuilderBase<*, *>.summarizeTextNode(
    name: String? = null
): LocalAgentNodeDelegate<String, String> = node(name) { input ->
    llm.writeSession {
        updatePrompt {
            user("Please summarize the following text: $input")
        }
        
        val response = requestLLMWithoutTools()
        response.content
    }
}
```

## Examples

### Simple Pass-Through Node

```kotlin
fun <T> LocalAgentSubgraphBuilderBase<*, *>.nodeDoNothing(
    name: String? = null
): LocalAgentNodeDelegate<T, T> = node(name) { input ->
    input // Simply return the input as output
}
```

### Data Transformation Node

```kotlin
fun LocalAgentSubgraphBuilderBase<*, *>.nodeJsonToMap(
    name: String? = null
): LocalAgentNodeDelegate<String, Map<String, Any>> = node(name) { jsonString ->
    Json.decodeFromString<Map<String, Any>>(jsonString)
}
```

### LLM Interaction Node

```kotlin
fun LocalAgentSubgraphBuilderBase<*, *>.nodeGenerateResponse(
    name: String? = null,
    prompt: String
): LocalAgentNodeDelegate<String, Message.Response> = node(name) { input ->
    llm.writeSession {
        updatePrompt {
            user("$prompt: $input")
        }
        
        requestLLM()
    }
}
```

### Tool Execution Node

```kotlin
fun LocalAgentSubgraphBuilderBase<*, *>.nodeExecuteCustomTool(
    name: String? = null,
    toolName: String
): LocalAgentNodeDelegate<String, String> = node(name) { input ->
    val toolCall = Message.Tool.Call(
        id = UUID.randomUUID().toString(),
        tool = toolName,
        args = mapOf("input" to input)
    )
    
    val result = environment.executeTool(toolCall)
    result.content
}
```