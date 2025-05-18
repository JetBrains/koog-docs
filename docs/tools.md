# Tools

Tools are functions that an agent can use to perform specific tasks or access external systems.

There are built-in tools available in the Simple API only and custom tools that can be used with both agents created using the Simple API and more sophisticated agents
created with the AI Agent. 

The process for enabling tools is the same for all agent types:

1. Add the tool to a tool registry. For details, see [Tool registry](#tool-registry)
2. Pass the tool registry to the agent. For details, see [Passing tools to an agent](#passing-tools-to-an-agent)

This page explains how to implement a tool and use it in the agent. To learn more about built-in tools, see [Available tools](simple-api-available-tools.md).

## Tool implementation

Each tool consists of the following components:

| Component        | Description                                                                                                                                                                                                                              |
|------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `Args`           | The serializable data class that defines arguments required for the custom tool.                                                                                                                                                         |
| `argsSerializer` | The overridden variable that defines how the arguments for the tool are serialized. See also SimpleTool.<!--[TODO] Link to API reference-->                                                                                              |
| `descriptor`     | The overridden variable that specifies tool metadata:<br/>- `name`<br/>- `description`<br/>- `requiredParameters` (empty by default), - `optionalParameters` (empty by default). See also SimpleTool.<!--[TODO] Link to API reference--> |
| `doExecute()`    | The overridden function that describes the main action performed by the tool. See also SimpleTool.<!--[TODO] Link to API reference-->                                                                                                    |


!!! tip
    Ensure your tools have clear descriptions and well-defined parameter names to make it easier for the LLM to understand and use them properly.

Here is an example of the custom tool implementation:

<!--- INCLUDE
import ai.grazie.code.agents.core.tools.SimpleTool
import ai.grazie.code.agents.core.tools.Tool
import ai.grazie.code.agents.core.tools.ToolDescriptor
import ai.grazie.code.agents.core.tools.ToolParameterDescriptor
import ai.grazie.code.agents.core.tools.ToolParameterType
import kotlinx.serialization.Serializable
-->
```kotlin
object CastToDoubleTool : SimpleTool<CastToDoubleTool.Args>() {
    @Serializable
    data class Args(val expression: String, val comment: String) : Tool.Args

    override val argsSerializer = Args.serializer()

    override val descriptor = ToolDescriptor(
        name = "cast to double",
        description = "casts the passed expression to double or returns 0.0 if the expression is not castable",
        requiredParameters = listOf(
            ToolParameterDescriptor(
                name = "expression", description = "An expression to case to double", type = ToolParameterType.String
            )
        ),
        optionalParameters = listOf(
            ToolParameterDescriptor(
                name = "comment",
                description = "A comment on how to process the expression",
                type = ToolParameterType.String
            )
        )
    )

    override suspend fun doExecute(args: Args): String {
        return "Result: ${castToDouble(args.expression)}, " + "the comment was: ${args.comment}"
    }

    private fun castToDouble(expression: String): Double {
        return expression.toDoubleOrNull() ?: 0.0
    }
}
```
<!--- KNIT example-custom-tool-01.kt -->

For more details, see API reference.<!--[TODO] Link to API reference-->

## Tool registry

Before you can use a tool in the agent, you need to add it to a tool registry.
The tool registry manages all tools available to the agent.
<!--It organizes them into logical stages, where each stage represents a group of tools that might be relevant for a particular phase or context of the agent operation.-->

The key features of the tool registry:

- Organizes tools.
- Supports merging of multiple tool registries.
- Provides methods to retrieve tools by name or type.
- Enables finding stages based on their name or the tools they contain.

To learn more, see ToolRegistry.<!--[TODO] Link to API reference-->

Here is an example of how to create the tool registry and add the implemented tool to it:

```kotlin
val toolRegistry = ToolRegistry {
    tool(CastToDoubleTool())
}
```
!!! note
    For built-in tools, you also need to create a tool registry.

To merge multiple tool registries, do the following:

```kotlin
val firstToolRegistry = ToolRegistry {
    tool(CastToDoubleTool())
}

val secondToolRegistry = ToolRegistry {
    tool(SampleTool())
}

val newRegistry = firstToolRegistry + secondToolRegistry
```

## Passing tools to an agent

For an agent to use a tool, you need to pass a tool registry containing the tool as an argument when creating the agent:

```kotlin
// Agent initialization
val agent = simpleChatAgent(
    apiToken = apiToken,
    systemPrompt = "You are a helpful assistant with strong mathematical skills.",
    // Pass your tool registry to the agent
    toolRegistry = toolRegistry
)
```

## Calling tools

There are several ways to call tools within your agent code. The recommended approach is to use the provided methods
in the agent context rather than calling tools directly, as this ensures proper handling of tool operation within the
agent environment.

!!! tip 
    Ensure you have implemented proper [error handling](agent-events.md) in your tools to prevent agent failure.

The `AIAgentStageContext` interface provides several methods for calling tools:

- Call a tool with the given arguments:
```kotlin
suspend inline fun <reified TArgs : Tool.Args, reified TResult : ToolResult> callTool(
    tool: Tool<TArgs, TResult>,
    args: TArgs
): TResult
```

- Call a tool by its name and the given arguments:
```kotlin
suspend inline fun <reified TArgs : Tool.Args> callTool(
    toolName: String,
    args: TArgs
): Tool.Result
```

- Call a tool by the provided tool class and arguments:
```kotlin
suspend inline fun <reified TArgs : Tool.Args, reified TResult : Tool.Result> callTool(
    toolClass: KClass<out Tool<TArgs, TResult>>,
    args: TArgs
): TResult
```

- Call a tool of the specified type with the given arguments:
```kotlin
suspend inline fun <reified ToolT : Tool<*, *>> callTool(
    args: Tool.Args
): Tool.Result
```

- Call a tool that returns a raw string result:
```kotlin
suspend inline fun <reified TArgs : Tool.Args> callToolRaw(
    toolName: String,
    args: TArgs
): String
```

For more details, see API reference.<!--[TODO] Link to API reference-->

Here is an example that demonstrates how to call a tool:

```kotlin
llm.writeSession {
    callToolRaw(toolName = "toolName", args = listOf())
}
```

Alternatively, you can use the following methods to call the tool:

```kotlin
// Call by name
callTool(BookTool.NAME, bookArgs)

// Call by class reference
callTool(BookTool::class, bookArgs)

// Call using reified type parameter
callTool<BookTool>(bookArgs)

// Call with raw string result
callToolRaw(BookTool.NAME, bookArgs)

// Find tool first, then execute
findTool(BookTool::class).execute(bookArgs)
```

### Parallel tool calls

You can also execute tool calls in parallel using the `toParallelToolCallsRaw` extension:

```kotlin
inline fun <reified TArgs : Tool.Args, reified TResult : Tool.Result> Flow<TArgs>.toParallelToolCalls(
    safeTool: SafeTool<TArgs, TResult>,
    concurrency: Int = 16
): Flow<TResult>
```

For example:

```kotlin
@Serializable
data class Book(
    val bookName: String,
    val author: String,
    val description: String
) : Tool.Args

/*...*/

val myNode by node<Unit, Unit> { _ ->
    llm.writeSession {
        flow {
            emit(Book("Book 1", "Author 1", "Description 1"))
        }.toParallelToolCallsRaw(BookTool::class).collect()
    }
}
```

## Calling tools from nodes

When building agent workflows with nodes, you can use specialized nodes to call tools:

* **nodeExecuteTool**: executes a single tool call and returns its result.

```kotlin
fun LocalAgentSubgraphBuilderBase<*, *>.nodeExecuteTool(
    name: String? = null
): LocalAgentNodeDelegate<Message.Tool.Call, Message.Tool.Result>
```

* **nodeExecuteMultipleTools**: executes multiple tool calls and returns their results.

```kotlin
fun LocalAgentSubgraphBuilderBase<*, *>.nodeExecuteMultipleTools(
    name: String? = null
): LocalAgentNodeDelegate<List<Message.Tool.Call>, List<Message.Tool.Result>>
```

* **nodeLLMSendToolResult**: sends a tool result to the LLM and gets a response.

```kotlin
fun LocalAgentSubgraphBuilderBase<*, *>.nodeLLMSendToolResult(
    name: String? = null
): LocalAgentNodeDelegate<Message.Tool.Result, Message.Response>
```

* **nodeLLMSendMultipleToolResults**: Sends multiple tool results to the LLM.

```kotlin
fun LocalAgentSubgraphBuilderBase<*, *>.nodeLLMSendMultipleToolResults(
    name: String? = null
): LocalAgentNodeDelegate<List<Message.Tool.Result>, List<Message.Response>>
```

### Node usage example

```kotlin
val processData by node<Unit, String> { _ ->
    llm.writeSession {
        callToolRaw(toolName = "toolName", args = listOf())
    }
    "Processing complete"
}

// Connect nodes
edge(nodeStart forwardTo processData)
edge(processData forwardTo nodeFinish)
```

Remember to always call tools through the agent environment context to ensure proper handling of events, feature pipelines, and testing capabilities.
