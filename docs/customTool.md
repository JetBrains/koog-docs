# Creating Custom Tools

You can extend agent capabilities by creating custom tools. These tools can be used as with the agents created via the
`Simple API`, as for more sophisticated agents created with the `KotlinAIAgent`. 

### Each custom tool consists of:

- Serializable `Args` data class, naming the **arguments** that should be passed to your custom tool, and an overridden
  `argsSerializer`;
- overridden `descriptor` variable consisting of the `name`, `description`, `requiredParameters` (empty by default), and
  `optionalParameters` (empty by default) for your tool;
- overridden function `doExecute()` that describes **the main action underneath your tool's execution**.

Example of a custom tool:

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
        name = "comment", description = "A comment on how to process the expression", type = ToolParameterType.String
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