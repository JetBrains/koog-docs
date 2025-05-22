# Advanced implementation

This section explains the advanced API designed for scenarios that require enhanced flexibility and customized behavior.
With this approach, you have full control over every aspect of a tool, including its parameters,
metadata, execution logic, and how it is registered and invoked.

This level of control is ideal for creating sophisticated tools that extend basic use cases,  enabling seamless integration into agent sessions and workflows.

This page describes how to implement a tool, manage tools through registries, call them, and use within node-based agent architectures.

## Tool implementation

Each tool consists of the following components:

| Component        | Description                                                                                                                                                                                                                                                                                        |
|------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `Args`           | The serializable data class that defines arguments required for the custom tool.                                                                                                                                                                                                                   |
| `argsSerializer` | The overridden variable that defines how the arguments for the tool are serialized. See also [SimpleTool](https://api.koog.ai/agents/agents-tools/ai.koog.agents.core.tools/-simple-tool/index.html).                                                                                              |
| `descriptor`     | The overridden variable that specifies tool metadata:<br/>- `name`<br/>- `description`<br/>- `requiredParameters` (empty by default), - `optionalParameters` (empty by default). See also [SimpleTool](https://api.koog.ai/agents/agents-tools/ai.koog.agents.core.tools/-simple-tool/index.html). |
| `doExecute()`    | The overridden function that describes the main action performed by the tool. See also [SimpleTool](https://api.koog.ai/agents/agents-tools/ai.koog.agents.core.tools/-simple-tool/index.html).                                                                                                    |


!!! tip
    Ensure your tools have clear descriptions and well-defined parameter names to make it easier for the LLM to understand and use them properly.

Here is an example of the custom tool implementation:

<!--- INCLUDE
import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.Tool
import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.agents.core.tools.ToolParameterDescriptor
import ai.koog.agents.core.tools.ToolParameterType
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

For more details, see [API reference](https://api.koog.ai/agents/agents-tools/ai.koog.agents.core.tools/-simple-tool/index.html).

## Tool registry

Before you can use a tool in the agent, you need to add it to a tool registry.
The tool registry manages all tools available to the agent.

The key features of the tool registry:

- Organizes tools.
- Supports merging of multiple tool registries.
- Provides methods to retrieve tools by name or type.

To learn more, see [ToolRegistry](https://api.koog.ai/agents/agents-tools/ai.koog.agents.core.tools/-tool-registry/index.html).

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
val agent = simpleSingleRunAgent(
    executor = simpleOpenAIExecutor(System.getenv("OPENAI_API_KEY")),
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

The tools are called within a specific session context represented by `AIAgentLLMWriteSession`.
It provides several methods for calling tools so that you can:

- Call a tool with the given arguments.
- Call a tool by its name and the given arguments.
- Call a tool by the provided tool class and arguments.
- Call a tool of the specified type with the given arguments.
- Call a tool that returns a raw string result.

For more details, see [API reference](https://api.koog.ai/agents/agents-core/ai.koog.agents.core.agent.session/-a-i-agent-l-l-m-write-session/index.html).

### Parallel tool calls

You can also call tools in parallel using the `toParallelToolCallsRaw` extension. For example:

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

When building agent workflows with nodes, you can use special nodes to call tools:

* **nodeExecuteTool**: calls a single tool call and returns its result. For details, see [API reference](https://api.koog.ai/agents/agents-core/ai.koog.agents.core.dsl.extension/node-execute-tool.html).
 
* **nodeExecuteSingleTool** that calls a specific tool with the provided arguments. For details, see [API reference](https://api.koog.ai/agents/agents-core/ai.koog.agents.core.dsl.extension/node-execute-single-tool.html).

* **nodeExecuteMultipleTools** that calls multiple tool calls and returns their results. For details, see [API reference](https://api.koog.ai/agents/agents-core/ai.koog.agents.core.dsl.extension/node-execute-multiple-tools.html).

* **nodeLLMSendToolResult** that sends a tool result to the LLM and gets a response. For details, see [API reference](https://api.koog.ai/agents/agents-core/ai.koog.agents.core.dsl.extension/node-l-l-m-send-tool-result.html).

* **nodeLLMSendMultipleToolResults** that sends multiple tool results to the LLM. For details, see [API reference](https://api.koog.ai/agents/agents-core/ai.koog.agents.core.dsl.extension/node-l-l-m-send-multiple-tool-results.html).
