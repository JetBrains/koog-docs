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

### Setting up test dependencies

Before setting up a test environment, make sure that you have added the following dependencies:
   ```kotlin
   // build.gradle.kts
   dependencies {
       testImplementation("ai.jetbrains.code.agents:agents-test:$version")
       testImplementation("kotlin.testing")
   }
   ```

### Mocking LLM Responses

The most basic form of testing involves mocking LLM responses to ensure deterministic behavior. This is done using the `MockLLMBuilder` and related utilities.

```kotlin
// Create a mock LLM executor
val mockLLMApi = getMockExecutor(toolRegistry, eventHandler) {
    // Mock a simple text response
    mockLLMAnswer("Hello!") onRequestContains "Hello"

    // Mock a default response
    mockLLMAnswer("I don't know how to answer that.").asDefaultResponse
}
```

### Mocking Tool Calls

You can mock the LLM to call specific tools based on input patterns:

```kotlin
// Mock a tool call response
mockLLMToolCall(CreateTool, CreateTool.Args("solve")) onRequestEquals "Solve task"

// Mock tool behavior - simplest form without lambda
mockTool(PositiveToneTool) alwaysReturns "The text has a positive tone."

// Using lambda when you need to perform extra actions
mockTool(NegativeToneTool) alwaysTells {
    // Perform some extra action
    println("Negative tone tool called")

    // Return the result
    "The text has a negative tone."
}

// Mock tool behavior based on specific arguments
mockTool(AnalyzeTool) returns AnalyzeTool.Result("Detailed analysis") onArguments AnalyzeTool.Args("analyze deeply")

// Mock tool behavior with conditional argument matching
mockTool(SearchTool) returns SearchTool.Result("Found results") onArgumentsMatching { args ->
    args.query.contains("important")
}
```

The examples above demonstrate different ways to mock tools, from simple to more complex:

1. `alwaysReturns` - Simplest form, directly returns a value without a lambda
2. `alwaysTells` - Uses a lambda when you need to perform additional actions
3. `returns...onArguments` - Returns specific results for exact argument matches
4. `returns...onArgumentsMatching` - Returns results based on custom argument conditions

### Enabling Testing Mode

To enable testing mode on an agent, use the `withTesting()` function within the AIAgent constructor block:

```kotlin
// Create the agent with testing enabled
AIAgent(
    promptExecutor = mockLLMApi,
    toolRegistry = toolRegistry,
    strategy = strategy,
    eventHandler = eventHandler,
    agentConfig = agentConfig,
) {
    // Enable testing mode
    withTesting()
}
```

## Advanced Testing

### Testing Graph Structure

Before diving into detailed node behavior and edge connections, it's important to verify the overall structure of your agent's graph. This includes checking that all required nodes exist and are properly connected in the expected stages.

The `Testing` feature provides a comprehensive way to test your agent's graph structure. This approach is particularly valuable for complex agents with multiple stages and interconnected nodes.

#### Basic Structure Testing

Start by validating the fundamental structure of your agent's graph:

```kotlin
AIAgent(
    // constructor arguments
    toolRegistry = toolRegistry,
    strategy = strategy,
    eventHandler = eventHandler,
    agentConfig = agentConfig,
    promptExecutor = mockLLMApi,
) {
    testGraph {
        // Assert the order of stages
        assertStagesOrder("first", "second")

        stage("first") {
            val start = startNode()
            val finish = finishNode()

            // Assert nodes by name
            val askLLM = assertNodeByName<String, Message.Response>("callLLM")
            val callTool = assertNodeByName<ToolCall.Signature, ToolCall.Result>("executeTool")

            // Assert node reachability
            assertReachable(start, askLLM)
            assertReachable(askLLM, callTool)
        }
    }
}
```


### Testing Node Behavior

Node behavior testing allows you to verify that nodes in your agent's graph produce the expected outputs for given inputs. This is crucial for ensuring that your agent's logic works correctly under different scenarios.

#### Basic Node Testing

Start with simple input/output validations for individual nodes:

```kotlin
assertNodes {
    // Test basic text responses
    askLLM withInput "Hello" outputs Message.Assistant("Hello!")

    // Test tool call responses
    askLLM withInput "Solve task" outputs toolCallMessage(CreateTool, CreateTool.Args("solve"))
}
```

The example above shows how to test that:
1. When the LLM node receives "Hello" as input, it responds with a simple text message
2. When it receives "Solve task", it responds with a tool call

#### Testing Tool Execution Nodes

You can also test nodes that execute tools:

```kotlin
assertNodes {
    // Test tool execution with specific arguments
    callTool withInput toolCallSignature(
        SolveTool,
        SolveTool.Args("solve")
    ) outputs toolResult(SolveTool, "solved")
}
```

This verifies that when the tool execution node receives a specific tool call signature, it produces the expected tool result.

#### Advanced Node Testing

For more complex scenarios, you can test nodes with structured inputs and outputs:

```kotlin
assertNodes {
    // Test with different inputs to the same node
    askLLM withInput "Simple query" outputs Message.Assistant("Simple response")

    // Test with complex parameters
    askLLM withInput "Complex query with parameters" outputs toolCallMessage(
        AnalyzeTool, 
        AnalyzeTool.Args(query = "parameters", depth = 3)
    )
}
```

You can also test complex tool call scenarios with detailed result structures:

```kotlin
assertNodes {
    // Test complex tool call with structured result
    callTool withInput toolCallSignature(
        AnalyzeTool,
        AnalyzeTool.Args(query = "complex", depth = 5)
    ) outputs toolResult(AnalyzeTool, AnalyzeTool.Result(
        analysis = "Detailed analysis",
        confidence = 0.95,
        metadata = mapOf("source" to "database", "timestamp" to "2023-06-15")
    ))
}
```

These advanced tests help ensure that your nodes handle complex data structures correctly, which is essential for sophisticated agent behaviors.

### Testing Edge Connections

Edge connections testing allows you to verify that your agent's graph correctly routes outputs from one node to the appropriate next node. This ensures that your agent follows the intended workflow paths based on different outputs.

#### Basic Edge Testing

Start with simple edge connection tests:

```kotlin
assertEdges {
    // Test text message routing
    askLLM withOutput Message.Assistant("Hello!") goesTo giveFeedback

    // Test tool call routing
    askLLM withOutput toolCallMessage(CreateTool, CreateTool.Args("solve")) goesTo callTool
}
```

This example verifies that:
1. When the LLM node outputs a simple text message, the flow is directed to the `giveFeedback` node
2. When it outputs a tool call, the flow is directed to the `callTool` node

#### Testing Conditional Routing

You can test more complex routing logic based on the content of outputs:

```kotlin
assertEdges {
    // Different text responses can route to different nodes
    askLLM withOutput Message.Assistant("Need more information") goesTo askForInfo
    askLLM withOutput Message.Assistant("Ready to proceed") goesTo processRequest
}
```

#### Advanced Edge Testing

For sophisticated agents, you can test conditional routing based on structured data in tool results:

```kotlin
assertEdges {
    // Test routing based on tool result content
    callTool withOutput toolResult(
        AnalyzeTool, 
        AnalyzeTool.Result(analysis = "Needs more processing", confidence = 0.5)
    ) goesTo processResult
}
```

You can also test complex decision paths based on different result properties:

```kotlin
assertEdges {
    // Route to different nodes based on confidence level
    callTool withOutput toolResult(
        AnalyzeTool, 
        AnalyzeTool.Result(analysis = "Complete", confidence = 0.9)
    ) goesTo finish

    callTool withOutput toolResult(
        AnalyzeTool, 
        AnalyzeTool.Result(analysis = "Uncertain", confidence = 0.3)
    ) goesTo verifyResult
}
```

These advanced edge tests help ensure that your agent makes the correct decisions based on the content and structure of node outputs, which is essential for creating intelligent, context-aware workflows.

## Complete Testing Example

Here's a user story that demonstrates a complete testing scenario:

Imagine you're developing a tone analysis agent that analyzes the tone of text and provides feedback. The agent has a single stage with tools for detecting positive, negative, and neutral tones.

Here's how you might test this agent:

```kotlin
@Test
fun testToneAgent() = runTest {
    // Create a list to track tool calls
    var toolCalls = mutableListOf<String>()
    var result: String? = null

    // Create a tool registry
    val toneStageName = "tone_analysis"
    val toolRegistry = ToolRegistry {
        stage(toneStageName) {
            // Special tool, required with this type of agent
            tool(SayToUser)

            with(ToneTools) {
                tools()
            }
        }
    }

    // Create an event handler
    val eventHandler = EventHandler {
        onToolCall { stage, tool, args ->
            println("[DEBUG_LOG] Tool called: stage ${stage.name}, tool ${tool.name}, args $args")
            toolCalls.add(tool.name)
        }

        handleError {
            println("[DEBUG_LOG] An error occurred: ${it.message}\n${it.stackTraceToString()}")
            true
        }

        handleResult {
            println("[DEBUG_LOG] Result: $it")
            result = it
        }
    }

    val positiveText = "I love this product!"
    val negativeText = "Awful service, hate the app."
    val defaultText = "I don't know how to answer this question."

    val positiveResponse = "The text has a positive tone."
    val negativeResponse = "The text has a negative tone."
    val neutralResponse = "The text has a neutral tone."

    val mockLLMApi = getMockExecutor(toolRegistry, eventHandler) {
        // Set up LLM responses for different input texts
        mockLLMToolCall(NeutralToneTool, ToneTool.Args(defaultText)) onRequestEquals defaultText
        mockLLMToolCall(PositiveToneTool, ToneTool.Args(positiveText)) onRequestEquals positiveText
        mockLLMToolCall(NegativeToneTool, ToneTool.Args(negativeText)) onRequestEquals negativeText

        // Mock the behaviour that LLM responds just tool responses once the tools returned smth.
        mockLLMAnswer(positiveResponse) onRequestContains positiveResponse
        mockLLMAnswer(negativeResponse) onRequestContains negativeResponse
        mockLLMAnswer(neutralResponse) onRequestContains neutralResponse

        mockLLMAnswer(defaultText).asDefaultResponse

        // Tool mocks:
        mockTool(PositiveToneTool) alwaysTells {
            toolCalls += "Positive tone tool called"
            positiveResponse
        }
        mockTool(NegativeToneTool) alwaysTells {
            toolCalls += "Negative tone tool called"
            negativeResponse
        }
        mockTool(NeutralToneTool) alwaysTells {
            toolCalls += "Neutral tone tool called"
            neutralResponse
        }
    }

    // Create strategy
    val strategy = toneStrategy("tone_analysis", toolRegistry, toneStageName)

    // Create agent config
    val agentConfig = AIAgentConfig(
        prompt = prompt("test-agent") {
            system(
                """
                You are an question answering agent with access to the tone analysis tools.
                You need to answer 1 question with the best of your ability.
                Be as concise as possible in your answers.
                DO NOT ANSWER ANY QUESTIONS THAT ARE BESIDES PERFORMING TONE ANALYSIS!
                DO NOT HALLUCINATE!
            """.trimIndent()
            )
        },
        model = mockk<LLModel>(relaxed = true),
        maxAgentIterations = 10
    )

    // Create the agent with testing enabled
    val agent = AIAgent(
        promptExecutor = mockLLMApi,
        toolRegistry = toolRegistry,
        strategy = strategy,
        eventHandler = eventHandler,
        agentConfig = agentConfig,
    ) {
        withTesting()
    }

    // Test positive text
    agent.run(positiveText)
    assertEquals("The text has a positive tone.", result, "Positive tone result should match")
    assertEquals(1, toolCalls.size, "One tool is expected to be called")

    // Test negative text
    agent.run(negativeText)
    assertEquals("The text has a negative tone.", result, "Negative tone result should match")
    assertEquals(2, toolCalls.size, "Two tools are expected to be called")

    //Test neutral text
    agent.run(defaultText)
    assertEquals("The text has a neutral tone.", result, "Neutral tone result should match")
    assertEquals(3, toolCalls.size, "Three tools are expected to be called")
}
```

For more complex agents with multiple stages, you can also test the graph structure:

```kotlin
@Test
fun testMultiStageAgentStructure() = runBlocking {
    val strategy = strategy("test") {
        stage("first") {
            val callLLM by nodeLLMRequest(allowToolCalls = false)
            val executeTool by nodeExecuteTool()
            val sendToolResult by nodeLLMSendToolResult()
            val giveFeedback by node<String, String> { input ->
                llm.writeSession {
                    updatePrompt {
                        user("Call tools! Don't chat!")
                    }
                }
                input
            }

            edge(nodeStart forwardTo callLLM transformed { stageInput })
            edge(callLLM forwardTo executeTool onToolCall { true })
            edge(callLLM forwardTo giveFeedback onAssistantMessage { true })
            edge(giveFeedback forwardTo giveFeedback onAssistantMessage { true })
            edge(giveFeedback forwardTo executeTool onToolCall { true })
            edge(executeTool forwardTo nodeFinish transformed { it.content })
        }

        stage("second") {
            edge(nodeStart forwardTo nodeFinish transformed { stageInput })
        }
    }

    val toolRegistry = ToolRegistry {
        stage("first") {
            tool(DummyTool)
            tool(CreateTool)
            tool(SolveTool)
        }
        stage("second") {
            tool(DummyTool)
        }
    }

    val mockLLMApi = getMockExecutor(toolRegistry) {
        mockLLMAnswer("Hello!") onRequestContains "Hello"
        mockLLMToolCall(CreateTool, CreateTool.Args("solve")) onRequestEquals "Solve task"
    }

    val basePrompt = prompt("test") {}

    AIAgent(
        toolRegistry = toolRegistry,
        strategy = strategy,
        eventHandler = EventHandler {},
        agentConfig = AIAgentConfig(prompt = basePrompt, model = OpenAIModels.Chat.GPT4o, maxAgentIterations = 100),
        promptExecutor = mockLLMApi,
    ) {
        testGraph {
            assertStagesOrder("first", "second")

            stage("first") {
                val start = startNode()
                val finish = finishNode()

                val askLLM = assertNodeByName<String, Message.Response>("callLLM")
                val callTool = assertNodeByName<ToolCall.Signature, ToolCall.Result>("executeTool")
                val giveFeedback = assertNodeByName<Any?, Any?>("giveFeedback")

                assertReachable(start, askLLM)
                assertReachable(askLLM, callTool)

                assertNodes {
                    askLLM withInput "Hello" outputs Message.Assistant("Hello!")
                    askLLM withInput "Solve task" outputs toolCallMessage(CreateTool, CreateTool.Args("solve"))

                    callTool withInput toolCallSignature(
                        SolveTool,
                        SolveTool.Args("solve")
                    ) outputs toolResult(SolveTool, "solved")

                    callTool withInput toolCallSignature(
                        CreateTool,
                        CreateTool.Args("solve")
                    ) outputs toolResult(CreateTool, "created")
                }

                assertEdges {
                    askLLM withOutput Message.Assistant("Hello!") goesTo giveFeedback
                    askLLM withOutput toolCallMessage(CreateTool, CreateTool.Args("solve")) goesTo callTool
                }
            }

            stage("second") {
                // Empty stage for demonstration
            }
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
    mockLLMAnswer("Response A") onRequestContains "topic A"
    mockLLMAnswer("Response B") onRequestContains "topic B"
    mockLLMAnswer("Exact response") onRequestEquals "exact question"
    mockLLMAnswer("Conditional response") onCondition { it.contains("keyword") && it.length > 10 }
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
