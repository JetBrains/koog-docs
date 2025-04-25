# Testing Feature Documentation

## Overview

The Testing Feature provides a comprehensive framework for testing AI agent pipelines, stages, and tool interactions in the Code Engine project. It enables developers to create controlled test environments with mock LLM (Large Language Model) executors, tool registries, and agent environments.

### Purpose

The primary purpose of this feature is to facilitate testing of agent-based AI features by:

- Mocking LLM responses to specific prompts
- Simulating tool calls and their results
- Testing agent pipeline stages and their graph structures
- Verifying the correct flow of data through agent nodes
- Providing assertions for expected behaviors

## Public API Documentation
### Class: `Testing`

**Description**: The main class for configuring and running tests for agent pipelines and stages.

**Key Methods**:

```kotlin
fun createInitialConfig(): Config
```
**Description**: Creates an initial configuration for testing
- **Returns**: A new `Testing.Config` instance

```kotlin
fun install(config: Config, pipeline: AIAgentPipeline)
```
**Description**: Installs the testing configuration into an agent pipeline

**Parameters**:

| Name     | Type            | Description                |
|----------|-----------------|----------------------------|
| config   | Testing.Config  | The testing configuration  |
| pipeline | AIAgentPipeline | The agent pipeline to test |


#### Class: `Testing.Config`

**Description**: Configuration class for the Testing feature.

**Key Methods**:

```kotlin
fun assertEquals(expected: Any?, actual: Any?, message: String)
```
**Description**: Asserts that two values are equal

**Parameters**:

| Name     | Type     | Description           |
|----------|----------|-----------------------|
| expected | `Any?`   | The expected value    |
| actual   | `Any?`   | The actual value      |
| message  | `String` | The assertion message |


```kotlin
fun assert(value: Boolean, message: String)
```
**Description**: Asserts that a condition is true
**Parameters**:

| Name    | Type      | Description            |
|---------|-----------|------------------------|
| value   | `Boolean` | The condition to check |
| message | `String`  | The assertion message  |

```kotlin
fun handleAssertion(block: (AssertionResult) -> Unit)
```
**Description**: Sets a custom handler for assertion results
**Parameters**:

| Name | Type | Description |
|------|------|-------------|
| block | `(AssertionResult) -> Unit` | The handler function |

```kotlin
fun assertStagesOrder(vararg stages: String)
```
**Description**: Asserts the order of stages in the pipeline
**Parameters**:

| Name | Type | Description |
|------|------|-------------|
| stages | `vararg String` | The expected stage names in order |

```kotlin
fun stage(name: String, function: StageAssertionsBuilder.() -> Unit)
```
**Description**: Configures assertions for a specific stage
**Parameters**:

| Name | Type | Description |
|------|------|-------------|
| name | `String` | The name of the stage |
| function | `StageAssertionsBuilder.() -> Unit` | Configuration function |

#### Extension Functions

```kotlin
fun Testing.Config.graph(test: Testing.Config.() -> Unit)
```
**Description**: Enables graph testing with automatic assertion handling
**Parameters**:

| Name | Type | Description |
|------|------|-------------|
| test | `Testing.Config.() -> Unit` | Test configuration function |

```kotlin
suspend fun FeatureContext.testGraph(test: Testing.Config.() -> Unit)
```
**Description**: Extension function for FeatureContext to simplify graph testing
**Parameters**:

| Name | Type | Description |
|------|------|-------------|
| test | `Testing.Config.() -> Unit` | Test configuration function |

### Module: `ai.grazie.code.agents.testing.tools`

#### Class: `MockLLMBuilder`

**Description**: Builder for creating mock LLM executors with configurable responses.

**Key Methods**:

```kotlin
fun setDefaultResponse(response: String)
```
**Description**: Sets the default response for the mock executor
**Parameters**:

| Name | Type | Description |
|------|------|-------------|
| response | `String` | The default response text |

```kotlin
fun setToolRegistry(registry: ToolRegistry)
```
**Description**: Sets the tool registry for the mock executor
**Parameters**:

| Name | Type | Description |
|------|------|-------------|
| registry | `ToolRegistry` | The tool registry to use |

```kotlin
fun <Args : Tool.Args, Result : Tool.Result> addToolAction(
    tool: Tool<Args, Result>,
    argsCondition: suspend (Args) -> Boolean = { true },
    action: suspend (Args) -> Result
)
```
**Description**: Adds a tool action to the mock executor
**Parameters**:

| Name | Type | Description |
|------|------|-------------|
| tool | `Tool<Args, Result>` | The tool to mock |
| argsCondition | `suspend (Args) -> Boolean` | Condition for when to use this action |
| action | `suspend (Args) -> Result` | Function to produce the result |

```kotlin
fun build(): CodePromptExecutor
```
**Description**: Builds and returns a mock LLM executor
- **Returns**: A `CodePromptExecutor` implementation

#### Extension Functions

```kotlin
infix fun String.onUserRequestContains(pattern: String): MockLLMBuilder
```
**Description**: Configures a response when the user request contains a pattern
**Parameters**:

| Name | Type | Description |
|------|------|-------------|
| pattern | `String` | The pattern to match |
- **Returns**: The `MockLLMBuilder` instance

```kotlin
infix fun String.onUserRequestEquals(pattern: String): MockLLMBuilder
```
**Description**: Configures a response when the user request exactly matches a pattern
**Parameters**:

| Name | Type | Description |
|------|------|-------------|
| pattern | `String` | The pattern to match |
- **Returns**: The `MockLLMBuilder` instance

#### Function: `getMockExecutor`

```kotlin
fun getMockExecutor(
    toolRegistry: ToolRegistry? = null,
    eventHandler: EventHandler? = null,
    init: MockLLMBuilder.() -> Unit
): CodePromptExecutor
```
**Description**: Creates a mock executor with the given configuration
**Parameters**:

| Name | Type | Description |
|------|------|-------------|
| toolRegistry | `ToolRegistry?` | Optional tool registry |
| eventHandler | `EventHandler?` | Optional event handler |
| init | `MockLLMBuilder.() -> Unit` | Configuration function |

- **Returns**: A configured `CodePromptExecutor`

#### Function: `mockLLMAnswer`

```kotlin
fun mockLLMAnswer(response: String): DefaultResponseReceiver
```
**Description**: Creates a response receiver for configuring mock LLM answers
**Parameters**:

| Name | Type | Description |
|------|------|-------------|
| response | `String` | The response text |
- **Returns**: A `DefaultResponseReceiver` for further configuration

#### Class: `MockEnvironment`

**Description**: A mock implementation of `AgentEnvironment` for testing.

```kotlin
class MockEnvironment(
    val toolRegistry: ToolRegistry,
    val promptExecutor: CodePromptExecutor,
    val baseEnvironment: AgentEnvironment? = null
) : AgentEnvironment
```

**Key Methods**:

```kotlin
override suspend fun executeTools(toolCalls: List<Message.Tool.Call>): List<Message.Tool.Result>
```
**Description**: Executes a list of tool calls
**Parameters**:

| Name | Type | Description |
|------|------|-------------|
| toolCalls | `List<Message.Tool.Call>` | The tool calls to execute |
- **Returns**: A list of tool results

#### Class: `DummyTool`

**Description**: A simple tool implementation for testing purposes.

```kotlin
class DummyTool : SimpleTool<DummyTool.Args>()
```

**Key Methods**:

```kotlin
override suspend fun doExecute(args: Args): String
```
**Description**: Executes the tool with the given arguments
**Parameters**:

| Name | Type | Description |
|------|------|-------------|
| args | `Args` | The tool arguments |
- **Returns**: Always returns "Dummy result"

## 3. Internal Helpers & Utilities

### Class: `ToolCondition`

**Description**: Internal class for matching tool calls and producing results.

```kotlin
class ToolCondition<Args : Tool.Args, Result : Tool.Result>(
    val tool: Tool<Args, Result>,
    val argsCondition: suspend (Args) -> Boolean,
    val produceResult: suspend (Args) -> Result
)
```

**Key Methods**:

```kotlin
internal suspend fun satisfies(toolCall: Message.Tool.Call): Boolean
```
- Checks if this condition applies to a given tool call

```kotlin
internal suspend fun invokeAndSerialize(toolCall: Message.Tool.Call): String
```
- Invokes the tool and serializes the result

### Class: `MockLLMExecutor`

**Description**: Internal implementation of `CodePromptExecutor` used by the mock builder.

```kotlin
internal class MockLLMExecutor(
    private val partialMatches: Map<String, Message.Response>? = null,
    private val exactMatches: Map<String, Message.Response>? = null,
    private val conditional: Map<(String) -> Boolean, String>? = null,
    private val defaultResponse: String = "",
    private val eventHandler: EventHandler? = null,
    private val toolRegistry: ToolRegistry? = null,
    private val logger: MPPLogger = LoggerFactory.create(MockLLMExecutor::class.simpleName!!),
    val toolActions: List<ToolCondition<*, *>> = emptyList()
) : CodePromptExecutor
```

**Key Methods**:

```kotlin
suspend fun handlePrompt(prompt: Prompt): Message.Response
```
- Processes a prompt and returns an appropriate response based on configured patterns

### Class: `DummyAgentStageContext`

**Description**: A dummy implementation of `LocalAgentStageContext` for testing.

### Class: `LocalAgentStageContextMockBuilder`

**Description**: Builder for creating mock stage contexts.

### Class: `NodeReference`

**Description**: Reference to a node in the agent graph for testing purposes.

## 4. Configuration & Initialization

### Setting Up a Test Environment

To set up a test environment for an agent pipeline:

1. **Create a Testing Configuration**:
   ```kotlin
   val testingConfig = Testing.createInitialConfig()
   ```

2. **Configure the Testing Environment**:
   ```kotlin
   testingConfig.apply {
       // Configure assertions, stages, etc.
   }
   ```

3. **Install the Testing Configuration**:
   ```kotlin
   Testing.install(testingConfig, pipeline)
   ```

### Mocking LLM Responses

To create a mock LLM executor:

1. **Using the Builder Directly**:
   ```kotlin
   val mockExecutor = MockLLMBuilder().apply {
       setDefaultResponse("Default response")
       "Custom response" onUserRequestContains "specific pattern"
   }.build()
   ```

2. **Using the Utility Function**:
   ```kotlin
   val mockExecutor = getMockExecutor {
       setDefaultResponse("Default response")
       "Custom response" onUserRequestContains "specific pattern"

       // Mock tool behavior
       mockTool(myTool) alwaysReturns myResult
   }
   ```

3. **Using the Helper Function**:
   ```kotlin
   val response = mockLLMAnswer("This is a response")
       .onRequestContains("specific question")
   ```

### Creating a Mock Environment

To create a mock environment for testing:

```kotlin
val mockExecutor = getMockExecutor { /* configuration */ }
val toolRegistry = ToolRegistry()
val mockEnvironment = MockEnvironment(toolRegistry, mockExecutor)
```

### Using the Testing API Extensions

To use the graph testing extensions:

```kotlin
suspend fun testMyFeature() = withTesting {
    graph {
        // Configure graph testing
        stage("myStage") {
            // Stage-specific assertions
        }
    }
}
```

## 5. Error Handling & Edge Cases

The Testing Feature includes several mechanisms for handling errors and edge cases:

### Assertion Handling

- Custom assertion handlers can be registered using `handleAssertion()`
- By default, assertions are mapped to Kotlin's test assertions
- Failed assertions provide detailed error messages

### Tool Execution Errors

- The `MockEnvironment` throws exceptions directly via `reportProblem()`
- Tool execution errors can be simulated by configuring tool actions to throw exceptions

### LLM Response Fallbacks

- The `MockLLMExecutor` uses a priority-based approach to find responses:
  1. Exact matches
  2. Partial matches
  3. Conditional matches
  4. Default response

### Edge Cases

- **Empty Tool Calls**: Handled gracefully by returning empty results
- **Missing Tool Registry**: Defaults to no tools available
- **Null Responses**: Treated as empty strings
- **Circular Graph References**: Detected and reported during graph traversal

## 6. Dependency Graph

The Testing Feature has the following component dependencies:

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

### External Dependencies

- `ai.grazie.code.agents.core.tools`: Core tool interfaces and implementations
- `ai.grazie.code.agents.local`: Local agent implementation
- `ai.jetbrains.code.prompt`: Prompt execution framework
- `kotlin.test`: Testing utilities

## 7. Examples & Quickstarts

### Basic Example: Mocking LLM Responses

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

### Advanced Example: Testing Agent Graph Flow

```kotlin
class MyAgentTest {
    @Test
    fun testAgentFlow() = runTest {
        // Set up the agent pipeline
        val pipeline = createPipeline()

        // Test the graph flow
        testGraph {
            // Assert the order of stages
            assertStagesOrder("input", "processing", "output")

            // Configure stage assertions
            stage("processing") {
                // Get references to nodes
                val startNode = startNode()
                val processNode = assertNodeByName("process")
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
}
```

### Quickstart: Setting Up a Test Environment

1. **Add Dependencies**:
   ```kotlin
   // build.gradle.kts
   dependencies {
       testImplementation("ai.grazie.code:code-agents-test:1.0.0")
   }
   ```

2. **Create a Test Class**:
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

## 8. FAQ / Troubleshooting

### Common Questions

#### Q: How do I mock a specific tool response?

A: Use the `mockTool` method in the `MockLLMBuilder`:

```kotlin
val mockExecutor = getMockExecutor {
    mockTool(myTool) alwaysReturns myResult

    // Or with conditions
    mockTool(myTool) returns myResult onArguments myArgs
}
```

#### Q: How can I test complex graph structures?

A: Use the stage assertions and node references:

```kotlin
testGraph {
    stage("myStage") {
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

#### Q: How do I simulate different LLM responses based on input?

A: Use the pattern matching methods:

```kotlin
getMockExecutor {
    "Response A" onUserRequestContains "topic A"
    "Response B" onUserRequestContains "topic B"
    "Exact response" onUserRequestEquals "exact question"
    "Conditional response" onCondition { it.contains("keyword") && it.length > 10 }
}
```

### Troubleshooting

#### Issue: Mock executor always returns the default response

**Solution**: Check that your pattern matching is correct. Patterns are case-sensitive and must match exactly as specified.

#### Issue: Tool calls are not being intercepted

**Solution**: Ensure that:
1. The tool registry is properly set up
2. The tool names match exactly
3. The tool actions are configured correctly

#### Issue: Graph assertions are failing

**Solution**: 
1. Verify that node names are correct
2. Check that the graph structure matches your expectations
3. Use the `startNode()` and `finishNode()` methods to get the correct entry and exit points
