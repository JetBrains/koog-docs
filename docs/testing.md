# AI Agents testing

## Overview

The Testing feature provides a comprehensive framework for testing AI agent pipelines, subgraphs, and tool interactions 
in the Koog framework. It enables developers to create controlled test environments with mock LLM (Large 
Language Model) executors, tool registries, and agent environments.

### Purpose

The primary purpose of this feature is to facilitate testing of agent-based AI features by:

- Mocking LLM responses to specific prompts
- Simulating tool calls and their results
- Testing agent pipeline subgraphs and their structures
- Verifying the correct flow of data through agent nodes
- Providing assertions for expected behaviors

## Configuration and initialization

### Setting up a test environment

Before setting up a test environment, make sure that you have added the following dependencies:
   ```kotlin
   // build.gradle.kts
   dependencies {
       testImplementation("ai.jetbrains.code.agents:agents-test:$version")
       testImplementation("kotlin.testing")
   }
   ```

To set up a test environment for an agent pipeline, follow the steps below:

1. Create a testing configuration:
   ```kotlin
   val testingConfig = Testing.createInitialConfig()
   ```

2. Configure the testing environment:
   ```kotlin
   testingConfig.apply {
       // Configure assertions, subgraphs, etc.
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

To use the `graph` testing extensions:

```kotlin
suspend fun testMyFeature() = withTesting {
    graph {
        // Configure graph testing
        val mySubgraph by subgraph<*, *>("my-subgraph") {
            // Subgraph-specific assertions
        }
    }
}
```

## Error handling and edge cases

The Testing feature includes several mechanisms for handling errors and edge cases.

### Assertion handling

- Custom assertion handlers can be registered using [handleAssertion()](#)`.
- By default, assertions are mapped to Kotlin's test assertions.
- Failed assertions provide detailed error messages.

### Tool run errors

- `MockEnvironment` throws exceptions directly via [reportProblem()](#).
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

[//]: # (TODO: Check if this is OK)
```kotlin
class MyAgentTest {
    @Test
    fun testAgentFlow() = runTest {
        // Set up the agent pipeline
        val pipeline = createPipeline()

        // Test the graph flow
        testGraph {
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
```

### Quickstart: Setting up a test environment

[//]: # (TODO: Check whether this is still a dependency)
1. Add dependencies:
   ```kotlin
   // build.gradle.kts
   dependencies {
       testImplementation("ai.jetbrains.code:code-agents-test:1.0.0")
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

## API documentation

For a complete API reference related to embeddings, see the reference documentation for the following packages:

- [ai.jetbrains.code.agents.testing.feature](#): Provides comprehensive testing utilities for AI agents, with mocking
capabilities and validation tools for agent behavior.
- [ai.jetbrains.code.agents.testing.tools](#): Provides a framework for defining, describing, and executing tools that 
can be used by AI agents to interact with the environment.

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

[//]: # (TODO: Check whether this example is rewritten properly)
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
