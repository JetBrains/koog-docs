# Creating Tools

You can extend agent capabilities by creating custom tools. These tools can be used as with the agents created via the
`Simple API`, as for more sophisticated agents created with the `KotlinAIAgent`.

**Tip**: Design tools with clear descriptions and parameter names to help the LLM understand how to use them.

### Each custom tool consists of:

- Serializable `Args` data class, naming the **arguments** that should be passed to your custom tool, and an overridden
  `argsSerializer`;
- overridden `descriptor` variable consisting of the `name`, `description`, `requiredParameters` (empty by default), and
  `optionalParameters` (empty by default) for your tool;
- overridden function `doExecute()` that describes **the main action underneath your tool's execution**.

Example of a custom tool:

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

Please note that, **to use the custom tool, you must**:

1. Add it to the tool registry by creating a new `ToolRegistry` instance including your tool;
2. Pass the created tool registry to the agent in the corresponding field:

```kotlin
val toolRegistry = ToolRegistry {
    stage {
        // Your custom tool(s)
        tool(CastToDoubleTool)
    }
}

// Agent initialization
val agent = simpleChatAgent(
    apiToken = apiToken,
    cs = coroutineScope,
    systemPrompt = "You are a helpful assistant with mathematical capabilities.",
    // Passing your tool registry to the agent
    toolRegistry = toolRegistry
)
```

## Calling Tools

There are several ways to call tools within your agent's code. The recommended approach is to use the provided methods
in the agent context rather than calling tools directly, as this ensures proper handling of tool execution within the
agent environment.

**Tip**: Implement proper [error handling](agent-events.md) in custom tools to prevent agent failures.

### Using callTool Methods

The `LocalAgentStageContext` provides several overloaded `callTool` methods from `writeSession` for executing tools:

**Call by tool name and args**:

```kotlin
suspend inline fun <reified TArgs : Tool.Args> callTool(
    toolName: String,
    args: TArgs
): Tool.Result
```

**Call by tool class and args**:

```kotlin
suspend inline fun <reified TArgs : Tool.Args, reified TResult : Tool.Result> callTool(
    toolClass: KClass<out Tool<TArgs, TResult>>,
    args: TArgs
): TResult
```

**Call by reified type parameter**:

```kotlin
suspend inline fun <reified ToolT : Tool<*, *>> callTool(
    args: Tool.Args
): Tool.Result
```

**Call with raw string result**:

```kotlin
suspend inline fun <reified TArgs : Tool.Args> callToolRaw(
    toolName: String,
    args: TArgs
): String
```

### Example Usage

Here's an example of different ways to call a tool:

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

### Parallel Tool Calls

You can also execute tool calls in parallel using the `toParallelToolCallsRaw` extension:

```kotlin
inline fun <reified TArgs : Tool.Args, reified TResult : Tool.Result> Flow<TArgs>.toParallelToolCalls(
    safeTool: SafeTool<TArgs, TResult>,
    concurrency: Int = 16
): Flow<TResult>
```

Example:

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

## Calling Tools from Nodes

When building agent workflows with nodes, you can use specialized nodes for tool execution:

**nodeExecuteTool**: Executes a single tool call and returns its result

```kotlin
fun LocalAgentSubgraphBuilderBase<*, *>.nodeExecuteTool(
    name: String? = null
): LocalAgentNodeDelegate<Message.Tool.Call, Message.Tool.Result>
```

**nodeExecuteMultipleTools**: Executes multiple tool calls and returns their results

```kotlin
fun LocalAgentSubgraphBuilderBase<*, *>.nodeExecuteMultipleTools(
    name: String? = null
): LocalAgentNodeDelegate<List<Message.Tool.Call>, List<Message.Tool.Result>>
```

**nodeLLMSendToolResult**: Sends a tool result to the LLM and gets a response

```kotlin
fun LocalAgentSubgraphBuilderBase<*, *>.nodeLLMSendToolResult(
    name: String? = null
): LocalAgentNodeDelegate<Message.Tool.Result, Message.Response>
```

**nodeLLMSendMultipleToolResults**: Sends multiple tool results to the LLM

```kotlin
fun LocalAgentSubgraphBuilderBase<*, *>.nodeLLMSendMultipleToolResults(
    name: String? = null
): LocalAgentNodeDelegate<List<Message.Tool.Result>, List<Message.Response>>
```

### Example Node Usage

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

Remember that tools should always be called through the agent environment context to ensure proper handling of events,
feature pipelines, and testing capabilities.
