# Custom node implementation

This page provides detailed instructions on how to implement your own custom nodes in the Koog framework. 
Custom nodes let you extend the functionality of agent workflows by creating reusable components that perform specific
operations.

To learn more about what graph nodes are, their usage, and existing default nodes, see [Graph nodes](nodes-and-components.md).

## Node architecture overview

Before diving into implementation details, it is important to understand the architecture of nodes in the Kotlin Agentic
Framework:

- **AIAgentNode**: the abstract base class for all nodes. It defines the core structure and behavior of a node.
- **AIAgentNodeDelegate**: a delegate class that handles lazy initialization of nodes.
- **AIAgentSubgraphBuilderBase**: provides the `node` function for creating nodes in a DSL-like manner.

Nodes are connected using edges, which define the flow of execution between nodes.
Each node has an `execute` method that takes an input and produces an output, which is then passed to the next node in 
the workflow.

## Implementing a custom node

Custom node implementations range from simple implementations that perform a basic logic on the input data and return
an output, to more complex node implementations that accept parameters and maintain state between runs.

### Basic node implementation

The simplest way to implement a custom node is to create an extension function on `AIAgentSubgraphBuilderBase` that
calls the `node` function:

```kotlin
fun <T> AIAgentSubgraphBuilderBase<*, *>.myCustomNode(
    name: String? = null
): AIAgentNodeDelegate<T, T> = node(name) { input ->
    // Custom logic here
    input // Return the input as output (pass-through)
}
```

This creates a pass-through node that performs some custom logic but returns the input as the output without modification.

### Parameterized nodes

You can create nodes that accept parameters to customize their behavior:

```kotlin
fun <T> AIAgentSubgraphBuilderBase<*, *>.myParameterizedNode(
    name: String? = null,
    param1: String,
    param2: Int
): AIAgentNodeDelegate<T, T> = node(name) { input ->
    // Use param1 and param2 in your custom logic
    input // Return the input as the output
}
```

### Stateful nodes

If your node needs to maintain state between runs, you can use closure variables:

```kotlin
fun <T> AIAgentSubgraphBuilderBase<*, *>.myStatefulNode(
    name: String? = null
): AIAgentNodeDelegate<T, T> {
    var counter = 0

    return node(name) { input ->
        counter++
        println("Node executed $counter times")
        input
    }
}
```

## Node input and output types

Nodes can have different input and output types, which are specified as generic parameters:

```kotlin
fun AIAgentSubgraphBuilderBase<*, *>.stringToIntNode(
    name: String? = null
): AIAgentNodeDelegate<String, Int> = node(name) { input: String ->
    input.toInt() // Convert string to integer
}
```

!!! note
    The input and output types determine how the node can be connected to other nodes in the workflow. Nodes can only be connected if the output type of the source node is compatible with the input type of the target node.

## Best practices

When implementing custom nodes, follow these best practices:

1. **Keep nodes focused**: each node should perform a single, well-defined operation.
2. **Use descriptive names**: node names should clearly indicate their purpose.
3. **Document parameters**: provide clear documentation for all parameters.
4. **Handle errors gracefully**: implement proper error handling to prevent workflow failures.
5. **Make nodes reusable**: design nodes to be reusable across different workflows.
6. **Use type parameters**: use generic type parameters when appropriate to make nodes more flexible.
7. **Provide default values**: when possible, provide sensible default values for parameters.

## Common patterns

The following sections provide some common patterns for implementing custom nodes.

### Pass-through nodes

Nodes that perform an operation but return the input as the output:

```kotlin
fun <T> AIAgentSubgraphBuilderBase<*, *>.loggingNode(
    name: String? = null
): AIAgentNodeDelegate<T, T> = node(name) { input ->
    println("Processing input: $input")
    input // Return the input as output
}
```

### Transformation nodes

Nodes that transform the input into a different output:

```kotlin
fun AIAgentSubgraphBuilderBase<*, *>.upperCaseNode(
    name: String? = null
): AIAgentNodeDelegate<String, String> = node(name) { input ->
    input.uppercase() // Transform input to uppercase
}
```

### LLM interaction nodes

Nodes that interact with the LLM:

```kotlin
fun AIAgentSubgraphBuilderBase<*, *>.summarizeTextNode(
    name: String? = null
): AIAgentNodeDelegate<String, String> = node(name) { input ->
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

The following sections provide specific examples for common usage patterns in custom nodes.

### Simple pass-through node

```kotlin
fun <T> AIAgentSubgraphBuilderBase<*, *>.nodeDoNothing(
    name: String? = null
): AIAgentNodeDelegate<T, T> = node(name) { input ->
    input // Return the input as the output
}
```

### Data transformation node

```kotlin
fun AIAgentSubgraphBuilderBase<*, *>.nodeJsonToMap(
    name: String? = null
): AIAgentNodeDelegate<String, Map<String, Any>> = node(name) { jsonString ->
    Json.decodeFromString<Map<String, Any>>(jsonString) // Decode and deserialize the given JSON string
}
```

### LLM interaction node

```kotlin
fun AIAgentSubgraphBuilderBase<*, *>.nodeGenerateResponse(
    name: String? = null,
    prompt: String
): AIAgentNodeDelegate<String, Message.Response> = node(name) { input ->
    llm.writeSession {
        updatePrompt {
            user("$prompt: $input") // Update the user message in the prompt
        }

        requestLLM()
    }
}
```

### Tool run node

```kotlin
fun AIAgentSubgraphBuilderBase<*, *>.nodeExecuteCustomTool(
    name: String? = null,
    toolName: String
): AIAgentNodeDelegate<String, String> = node(name) { input ->
    val toolCall = Message.Tool.Call( 
        id = UUID.randomUUID().toString(),
        tool = toolName,
        args = mapOf("input" to input) // Use the input as tool arguments
    )

    val result = environment.executeTool(toolCall)
    result.content
}
```