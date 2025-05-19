# AI Agents Testing Toolkit

## Overview

The Testing Feature provides a comprehensive framework for testing AI agent pipelines, and tool interactions in
the Kotlin Agentic Framework. It enables developers to create controlled test environments with mock LLM (Large Language
Model) executors, tool registries, and agent environments.

### Purpose

The primary purpose of this feature is to facilitate testing of agent-based AI features by:

- Mocking LLM responses to specific prompts
- Simulating tool calls and their results
- Verifying the correct flow of data through agent nodes
- Providing assertions for expected behaviors

## Configuration and initialization

### Setting up a test environment

To set up a test environment for an agent pipeline, follow the steps below:

1. Create a testing configuration:
   ```kotlin
   val testingConfig = Testing.createInitialConfig()
   ```

2. Configure the testing environment:
   ```kotlin
   testingConfig.apply {
       // Configure assertions, etc.
   }
   ```

3. Install the testing configuration:
   ```kotlin
   Testing.install(testingConfig, pipeline)
   ```

### Mocking LLM responses

To create a mock LLM executor, use one of the following code templates:

* Using the builder directly:
   ```kotlin
   val mockExecutor = MockLLMBuilder().apply {
       setDefaultResponse("Default response")
       "Custom response" onUserRequestContains "specific pattern"
   }.build()
   ```

* Using the utility function:
   ```kotlin
   val mockExecutor = getMockExecutor {
       setDefaultResponse("Default response")
       "Custom response" onUserRequestContains "specific pattern"

       // Mock tool behavior
       mockTool(myTool) alwaysReturns myResult
   }
   ```

* Using the helper function:
   ```kotlin
   val response = mockLLMAnswer("This is a response")
       .onRequestContains("specific question")
   ```

### Creating a mock environment

To create a mock environment for testing:

```kotlin
val mockExecutor = getMockExecutor { /* configuration */ }
val toolRegistry = ToolRegistry()
val mockEnvironment = MockEnvironment(toolRegistry, mockExecutor)
```

### Using the Testing API extensions

To use the graph testing extensions:

```kotlin
suspend fun testMyFeature() = withTesting {
    graph {
        // Configure graph testing
    }
}
```

## API documentation

### Class: `Testing`

**Description**: The main class for configuring and running tests for agent pipelines.

#### Methods

`createInitialConfig`

```kotlin
fun createInitialConfig(): Config
```

**Description**: Creates an initial configuration for testing.

- **Returns**: A new `Testing.Config` instance

`install`

```kotlin
fun install(config: Config, pipeline: AIAgentPipeline)
```

**Description**: Installs the testing configuration into an agent pipeline.

**Parameters**:

| Name     | Type            | Description                |
|----------|-----------------|----------------------------|
| config   | Testing.Config  | The testing configuration  |
| pipeline | AIAgentPipeline | The agent pipeline to test |

### Class: `Testing.Config`

**Description**: Configuration class for the Testing feature.

#### Methods

`assertEquals`

```kotlin
fun assertEquals(expected: Any?, actual: Any?, message: String)
```

**Description**: Asserts that two values are equal.

**Parameters**:

| Name     | Type     | Description           |
|----------|----------|-----------------------|
| expected | `Any?`   | The expected value    |
| actual   | `Any?`   | The actual value      |
| message  | `String` | The assertion message |

`assert`

```kotlin
fun assert(value: Boolean, message: String)
```

**Description**: Asserts that a condition is true

**Parameters**:

| Name    | Type      | Description            |
|---------|-----------|------------------------|
| value   | `Boolean` | The condition to check |
| message | `String`  | The assertion message  |

`handleAssertion`

```kotlin
fun handleAssertion(block: (AssertionResult) -> Unit)
```

**Description**: Sets a custom handler for assertion results.

**Parameters**:

| Name  | Type                        | Description          |
|-------|-----------------------------|----------------------|
| block | `(AssertionResult) -> Unit` | The handler function |


#### Extension functions

`Testing.Config.graph`

```kotlin
fun Testing.Config.graph(test: Testing.Config.() -> Unit)
```

**Description**: Enables graph testing with automatic assertion handling.

**Parameters**:

| Name | Type                        | Description                 |
|------|-----------------------------|-----------------------------|
| test | `Testing.Config.() -> Unit` | Test configuration function |

`FeatureContext.testGraph`

```kotlin
suspend fun FeatureContext.testGraph(strategyName: String, test: Testing.Config.() -> Unit)
```

**Description**: Extension function for FeatureContext to simplify graph testing.

**Parameters**:

| Name | Type                        | Description                 |
|------|-----------------------------|-----------------------------|
| test | `Testing.Config.() -> Unit` | Test configuration function |

### Module: `ai.koog.agents.testing.tools`

#### Class: `MockLLMBuilder`

**Description**: Builder for creating mock LLM executors with configurable responses.

##### Methods

`setDefaultResponse`

```kotlin
fun setDefaultResponse(response: String)
```

**Description**: Sets the default response for the mock executor.

**Parameters**:

| Name     | Type     | Description               |
|----------|----------|---------------------------|
| response | `String` | The default response text |

`setToolRegistry`

```kotlin
fun setToolRegistry(registry: ToolRegistry)
```

**Description**: Sets the tool registry for the mock executor.

**Parameters**:

| Name     | Type           | Description              |
|----------|----------------|--------------------------|
| registry | `ToolRegistry` | The tool registry to use |

`addToolAction`

```kotlin
fun <Args : Tool.Args, Result : Tool.Result> addToolAction(
    tool: Tool<Args, Result>,
    argsCondition: suspend (Args) -> Boolean = { true },
    action: suspend (Args) -> Result
)
```

**Description**: Adds a tool action to the mock executor.

**Parameters**:

| Name          | Type                        | Description                           |
|---------------|-----------------------------|---------------------------------------|
| tool          | `Tool<Args, Result>`        | The tool to mock                      |
| argsCondition | `suspend (Args) -> Boolean` | Condition for when to use this action |
| action        | `suspend (Args) -> Result`  | Function to produce the result        |

`build`

```kotlin
fun build(): CodePromptExecutor
```

**Description**: Builds and returns a mock LLM executor.

- **Returns**: A `CodePromptExecutor` implementation.

##### Extension functions

`String.onUserRequestContains`

```kotlin
infix fun String.onUserRequestContains(pattern: String): MockLLMBuilder
```

**Description**: Configures a response when the user request contains a pattern.

**Parameters**:

| Name    | Type     | Description          |
|---------|----------|----------------------|
| pattern | `String` | The pattern to match |

- **Returns**: A `MockLLMBuilder` instance.

`String.onUserRequestEquals`

```kotlin
infix fun String.onUserRequestEquals(pattern: String): MockLLMBuilder
```

**Description**: Configures a response when the user request exactly matches a pattern.

**Parameters**:

| Name    | Type     | Description          |
|---------|----------|----------------------|
| pattern | `String` | The pattern to match |

- **Returns**: A `MockLLMBuilder` instance.

`getMockExecutor`

```kotlin
fun getMockExecutor(
    toolRegistry: ToolRegistry? = null,
    init: MockLLMBuilder.() -> Unit
): CodePromptExecutor
```

**Description**: Creates a mock executor with the given configuration.

**Parameters**:

| Name         | Type                        | Description            |
|--------------|-----------------------------|------------------------|
| toolRegistry | `ToolRegistry?`             | Optional tool registry |
| init         | `MockLLMBuilder.() -> Unit` | Configuration function |

- **Returns**: A configured `CodePromptExecutor`.

`mockLLMAnswer`

```kotlin
fun mockLLMAnswer(response: String): DefaultResponseReceiver
```

**Description**: Creates a response receiver for configuring mock LLM answers.

**Parameters**:

| Name     | Type     | Description       |
|----------|----------|-------------------|
| response | `String` | The response text |

- **Returns**: A `DefaultResponseReceiver` for further configuration.

`MockEnvironment`

**Description**: A mock implementation of `AgentEnvironment` for testing.

```kotlin
class MockEnvironment(
    val toolRegistry: ToolRegistry,
    val promptExecutor: CodePromptExecutor,
    val baseEnvironment: AgentEnvironment? = null
) : AgentEnvironment
```

##### Methods

`executeTools`

```kotlin
override suspend fun executeTools(toolCalls: List<Message.Tool.Call>): List<Message.Tool.Result>
```

**Description**: Executes a list of tool calls.

**Parameters**:

| Name      | Type                      | Description               |
|-----------|---------------------------|---------------------------|
| toolCalls | `List<Message.Tool.Call>` | The tool calls to execute |

- **Returns**: A list of tool results

#### Class: `DummyTool`

**Description**: A simple tool implementation for testing purposes.

```kotlin
class DummyTool : SimpleTool<DummyTool.Args>()
```

##### Methods

`doExecute`

```kotlin
override suspend fun doExecute(args: Args): String
```

**Description**: Runs the tool with the given arguments.

**Parameters**:

| Name | Type   | Description        |
|------|--------|--------------------|
| args | `Args` | The tool arguments |

- **Returns**: Always returns "Dummy result".

## Internal helpers and utilities

### Class: `ToolCondition`

**Description**: Internal class for matching tool calls and producing results.

```kotlin
class ToolCondition<Args : Tool.Args, Result : Tool.Result>(
    val tool: Tool<Args, Result>,
    val argsCondition: suspend (Args) -> Boolean,
    val produceResult: suspend (Args) -> Result
)
```

##### Methods

`satisfies`

```kotlin
internal suspend fun satisfies(toolCall: Message.Tool.Call): Boolean
```

- Checks if this condition applies to a given tool call.

`invokeAndSerialize`

```kotlin
internal suspend fun invokeAndSerialize(toolCall: Message.Tool.Call): String
```

- Invokes the tool and serializes the result.

### Class: `MockLLMExecutor`

**Description**: Internal implementation of `CodePromptExecutor` used by the mock builder.

```kotlin
internal class MockLLMExecutor(
    private val partialMatches: Map<String, Message.Response>? = null,
    private val exactMatches: Map<String, Message.Response>? = null,
    private val conditional: Map<(String) -> Boolean, String>? = null,
    private val defaultResponse: String = "",
    private val toolRegistry: ToolRegistry? = null,
    private val logger: MPPLogger = LoggerFactory.create(MockLLMExecutor::class.simpleName!!),
    val toolActions: List<ToolCondition<*, *>> = emptyList()
) : CodePromptExecutor
```

##### Methods

`handlePrompt`

```kotlin
suspend fun handlePrompt(prompt: Prompt): Message.Response
```

- Processes a prompt and returns an appropriate response based on configured patterns.

### Class: `DummyAgentContext`

**Description**: A dummy implementation of `AIAgentContext` for testing.

### Class: `AIAgentContextMockBuilder`

**Description**: Builder for creating mock agent contexts.

### Class: `NodeReference`

**Description**: Reference to a node in the agent graph for testing purposes.

## Error handling and edge cases

The Testing feature includes several mechanisms for handling errors and edge cases:

### Assertion handling

- Custom assertion handlers can be registered using `handleAssertion()`.
- By default, assertions are mapped to Kotlin's test assertions.
- Failed assertions provide detailed error messages.

### Tool run errors

- `MockEnvironment` throws exceptions directly via `reportProblem()`.
- Tool run errors can be simulated by configuring tool actions to throw exceptions.

### LLM response fallbacks

- `MockLLMExecutor` uses a priority-based approach to find responses:
    1. Exact matches
    2. Partial matches
    3. Conditional matches
    4. Default response

### Edge cases

- Empty tool calls: handled gracefully by returning empty results.
- Missing tool registry: defaults to no tools available.
- Null responses: treated as empty strings.
- Circular graph references: detected and reported during graph traversal.

## Dependency graph

The Testing feature has the following component dependencies:

```
TestingFeature.kt
├── Api.kt (extends Testing.Config)
├── MockLLMBuilder.kt
│   └── MockLLMExecutor.kt
├── MockEnvironment.kt
│   ├── MockLLMExecutor.kt
│   └── ToolRegistry
└── DummyTool.kt
```

### External dependencies

- `ai.koog.agents.core.tools`: core tool interfaces and implementations
- `ai.koog.agents.local`: local agent implementation
- `ai.koog.prompt`: prompt execution framework
- `kotlin.test`: testing utilities

## Examples and quickstarts

### Basic example: Mocking LLM responses

```kotlin
// Create a mock executor
val mockExecutor = getMockExecutor {
    setDefaultResponse("I don't know how to help with that.")

    // Configure specific responses
    "Here's how to create a file" onUserRequestContains "create a file"
    "The answer is 42" onUserRequestEquals "What is the meaning of life?"
}

// Use the mock executor in your tests
val response = mockExecutor.execute(myPrompt)
assertEquals("Expected response", response)
```

### Advanced example: Testing agent graph flow

```kotlin
class MyAgentTest {
    @Test
    fun testAgentFlow() = runTest {
        // Set up the agent pipeline
        val pipeline = createPipeline()

        // Test the graph flow
        testGraph("test-strategy") {
            // Get references to nodes
            val startNode = startNode()
            val processNode = assertNodeByName<String, String>("process")
            val finishNode = finishNode()

            // Assert node connectivity
            assertReachable(startNode, processNode)
            assertReachable(processNode, finishNode)

            // Assert node outputs
            assertNodes {
                processNode.withInput("test input") outputs "test output"
            }
        }
    }
}
```

### Quickstart: Setting up a test environment

1. Add dependencies:
   ```kotlin
   // build.gradle.kts
   dependencies {
       testImplementation("ai.koog:agents-test:0.1.0-alpha.6")
   }
   ```

2. Create a test class:
   ```kotlin
   class MyTest {
       @Test
       fun testMyFeature() = runTest {
           // Create mock components
           val mockExecutor = getMockExecutor {
               setDefaultResponse("Default response")
           }

           // Set up the feature
           val feature = MyFeature(mockExecutor)

           // Test with the testing framework
           withTesting {
               // Your test assertions here
           }
       }
   }
   ```

## FAQ and troubleshooting

#### How do I mock a specific tool response?

Use the `mockTool` method in `MockLLMBuilder`:

```kotlin
val mockExecutor = getMockExecutor {
    mockTool(myTool) alwaysReturns myResult

    // Or with conditions
    mockTool(myTool) returns myResult onArguments myArgs
}
```

#### How can I test complex graph structures?

Use the subgraph assertions, `verifySubgraph`, and node references:

```kotlin
testGraph("test") {
    val mySubgraph = assertSubgraphByName<Unit, String>("mySubgraph")

    verifySubgraph(mySubgraph) {
        // Get references to nodes
        val nodeA = assertNodeByName("nodeA")
        val nodeB = assertNodeByName("nodeB")

        // Assert reachability
        assertReachable(nodeA, nodeB)

        // Assert edge connections
        assertEdges {
            nodeA.withOutput("result") goesTo nodeB
        }
    }
}
```

#### How do I simulate different LLM responses based on input?

Use pattern matching methods:

```kotlin
getMockExecutor {
    "Response A" onUserRequestContains "topic A"
    "Response B" onUserRequestContains "topic B"
    "Exact response" onUserRequestEquals "exact question"
    "Conditional response" onCondition { it.contains("keyword") && it.length > 10 }
}
```

### Troubleshooting

#### Mock executor always returns the default response

Check that your pattern matching is correct. Patterns are case-sensitive and must match exactly as
specified.

#### Tool calls are not being intercepted

Ensure that:

1. The tool registry is properly set up.
2. The tool names match exactly.
3. The tool actions are configured correctly.

#### Graph assertions are failing

1. Verify that node names are correct.
2. Check that the graph structure matches your expectations.
3. Use the `startNode()` and `finishNode()` methods to get the correct entry and exit points.
