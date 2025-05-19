# Defining Custom Subgraphs

## Overview

Subgraphs are modular components of an agent's execution strategy that encapsulate a specific part of the agent's behavior. They allow you to organize complex agent logic into manageable, reusable units. Each subgraph has its own start and finish nodes, and can contain various nodes for different operations like LLM requests, tool execution, and data transformation.

This guide explains how to define and use custom subgraphs in your agent strategies.

## What are Subgraphs?

A subgraph is a self-contained graph with:
- A start node that receives input
- A finish node that produces output
- Internal nodes that process data
- Edges that connect the nodes and define the execution flow
- Optional tool selection strategy that determines which tools are available within the subgraph

Subgraphs can be nested within a larger strategy and connected to other subgraphs, allowing for modular and reusable agent behavior.

## Creating a Subgraph

There are two main ways to create a subgraph:

### 1. With a Tool Selection Strategy

```kotlin
val mySubgraph by subgraph<String, String>(
    name = "mySubgraph",
    toolSelectionStrategy = ToolSelectionStrategy.ALL,
) {
    // Define nodes and edges here
    val callLLM by nodeLLMRequest(allowToolCalls = true)
    val executeTool by nodeExecuteTool()
    
    edge(nodeStart forwardTo callLLM)
    edge(callLLM forwardTo executeTool onToolCall { true })
    edge(executeTool forwardTo nodeFinish)
}
```

### 2. With Specific Tools

```kotlin
val mySubgraph by subgraph<String, String>(
    name = "mySubgraph",
    tools = listOf(CalculatorTool, SearchTool),
) {
    // Define nodes and edges here
    val callLLM by nodeLLMRequest(allowToolCalls = true)
    val executeTool by nodeExecuteTool()
    
    edge(nodeStart forwardTo callLLM)
    edge(callLLM forwardTo executeTool onToolCall { true })
    edge(executeTool forwardTo nodeFinish)
}
```

## Tool Selection Strategies

Subgraphs can use different strategies for selecting which tools are available:

1. **ALL**: Uses all tools available to the agent
   ```kotlin
   toolSelectionStrategy = ToolSelectionStrategy.ALL
   ```

2. **NONE**: Uses no tools
   ```kotlin
   toolSelectionStrategy = ToolSelectionStrategy.NONE
   ```

3. **Tools**: Uses a specific list of tools
   ```kotlin
   toolSelectionStrategy = ToolSelectionStrategy.Tools(listOf(tool1.descriptor, tool2.descriptor))
   ```

4. **AutoSelectForTask**: Dynamically selects tools based on a subtask description
   ```kotlin
   toolSelectionStrategy = ToolSelectionStrategy.AutoSelectForTask("Solve a math problem")
   ```

## Nodes in Subgraphs

Subgraphs can contain various types of nodes:

### LLM Request Nodes

```kotlin
val callLLM by nodeLLMRequest(
    name = "callLLM",
    allowToolCalls = true
)
```

### Tool Execution Nodes

```kotlin
val executeTool by nodeExecuteTool(
    name = "executeTool"
)
```

### Custom Processing Nodes

```kotlin
val processData by node<String, String> { input ->
    // Process the input
    "Processed: $input"
}
```

## Connecting Nodes with Edges

Nodes in a subgraph are connected with edges that define the execution flow:

```kotlin
// Simple forward edge
edge(nodeStart forwardTo callLLM)

// Edge with condition
edge(callLLM forwardTo executeTool onToolCall { true })

// Edge with transformation
edge(executeTool forwardTo nodeFinish transformed { it.content })
```

## Using Subgraphs in a Strategy

Subgraphs are defined within a strategy and can be connected to other subgraphs:

```kotlin
val strategy = strategy("myStrategy") {
    val firstSubgraph by subgraph<String, String>("first") {
        // Define first subgraph
    }
    
    val secondSubgraph by subgraph<String, String>("second") {
        // Define second subgraph
    }
    
    edge(nodeStart forwardTo firstSubgraph)
    edge(firstSubgraph forwardTo secondSubgraph)
    edge(secondSubgraph forwardTo nodeFinish)
}
```

## Complete Example

Here's a complete example of a strategy with multiple subgraphs:

```kotlin
val strategy = strategy("test") {
    val firstSubgraph by subgraph(
        "first",
        tools = listOf(CalculatorTool, SearchTool)
    ) {
        val callLLM by nodeLLMRequest(allowToolCalls = true)
        val executeTool by nodeExecuteTool()
        val sendToolResult by nodeLLMSendToolResult()
        
        edge(nodeStart forwardTo callLLM)
        edge(callLLM forwardTo executeTool onToolCall { true })
        edge(callLLM forwardTo nodeFinish onAssistantMessage { true })
        edge(executeTool forwardTo sendToolResult)
        edge(sendToolResult forwardTo callLLM)
    }
    
    val secondSubgraph by subgraph<String, String>("second") {
        val processData by node<String, String> { input ->
            "Processed: $input"
        }
        
        edge(nodeStart forwardTo processData)
        edge(processData forwardTo nodeFinish)
    }
    
    edge(nodeStart forwardTo firstSubgraph)
    edge(firstSubgraph forwardTo secondSubgraph)
    edge(secondSubgraph forwardTo nodeFinish)
}
```

## Best Practices

1. **Meaningful Names**: Give your subgraphs and nodes descriptive names that reflect their purpose.

2. **Single Responsibility**: Each subgraph should have a single, well-defined responsibility.

3. **Proper Tool Selection**: Use the appropriate tool selection strategy for each subgraph to ensure it has access to only the tools it needs.

4. **Error Handling**: Include error handling in your subgraphs to gracefully handle unexpected situations.

5. **Testing**: Test your subgraphs individually to ensure they behave as expected before integrating them into a larger strategy.

6. **Documentation**: Document the purpose and behavior of each subgraph for easier maintenance.

## Testing Subgraphs

You can test your subgraphs using the testing feature:

```kotlin
AIAgent(
    // constructor arguments
) {
    testGraph("test") {
        val mySubgraph = assertSubgraphByName<String, String>("mySubgraph")
        
        verifySubgraph(mySubgraph) {
            val start = startNode()
            val finish = finishNode()
            
            val callLLM = assertNodeByName<String, Message.Response>("callLLM")
            val executeTool = assertNodeByName<Message.Tool.Call, ReceivedToolResult>("executeTool")
            
            assertReachable(start, callLLM)
            assertReachable(callLLM, executeTool)
            assertReachable(executeTool, finish)
        }
    }
}
```

## Conclusion

Subgraphs are a powerful feature for organizing complex agent behavior into modular, reusable components. By using subgraphs effectively, you can create more maintainable and flexible agent strategies.