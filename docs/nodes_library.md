# Graph Nodes

This document provides detailed information about the node functions available in Kotlin AI agent system. These nodes are the building blocks for creating agent workflows by connecting them in a graph structure.

## Table of Contents

1. [Introduction](#introduction)
2. [Utility Nodes](#utility-nodes)
   - [nodeDoNothing](#nodedonothing)
3. [LLM Nodes](#llm-nodes)
   - [nodeUpdatePrompt](#nodeupdateprompt)
   - [nodeLLMSendStageInput](#nodellmsendstageinput)
   - [nodeLLMSendStageInputMultiple](#nodellmsendstageinputmultiple)
   - [nodeLLMRequest](#nodellmrequest)
   - [nodeLLMRequestMultiple](#nodellmrequestmultiple)
   - [nodeLLMCompressHistory](#nodellmcompresshistory)
4. [Tool Nodes](#tool-nodes)
   - [nodeExecuteTool](#nodeexecutetool)
   - [nodeLLMSendToolResult](#nodellmsendtoolresult)
   - [nodeExecuteMultipleTools](#nodeexecutemultipletools)
   - [nodeLLMSendMultipleToolResults](#nodellmsendmultipletoolresults)
5. [Subgraph Components](#subgraph-components)
   - [subgraphWithTask](#subgraphwithtask)
   - [subgraphWithVerification](#subgraphwithverification)
6. [Usage Examples](#usage-examples)

## Introduction

LocalAgentNodes are fundamental components used to build agent workflows in the Kotlin AI platform. Each node represents a specific operation or transformation in the workflow, and they can be connected using edges to define the flow of execution.

## Utility Nodes

### nodeDoNothing

```kotlin
fun <T> LocalAgentSubgraphBuilderBase<*, *>.nodeDoNothing(name: String? = null): LocalAgentNodeDelegate<T, T>
```

**Description:**  
A simple pass-through node that performs no actions. The input is directly passed as the output without any processing.

**Parameters:**
- `name` (optional): A custom name for the node. If not provided, the property name of the delegate will be used.

**Return Value:**  
A delegate for the created node, representing a no-operation transformation where the input is returned as output.

**Use Cases:**
- When you need a placeholder node in your graph
- When you want to create a connection point without modifying the data
- For debugging or testing purposes

**Example:**
```kotlin
val passthrough by nodeDoNothing<String>("passthrough")
edge(someNode forwardTo passthrough)
edge(passthrough forwardTo anotherNode)
```

## LLM Nodes

### nodeUpdatePrompt

```kotlin
fun LocalAgentSubgraphBuilderBase<*, *>.nodeUpdatePrompt(
   name: String? = null,
   body: PromptBuilder.() -> Unit
): LocalAgentNodeDelegate<Unit, Unit>
```

**Description:**  
A node that updates the prompt without asking the LLM for a response. This is useful for modifying the conversation context before making an actual LLM request.

**Parameters:**
- `name` (optional): A custom name for the node. If not provided, the property name of the delegate will be used.
- `body`: A lambda block specifying the logic to update the prompt using the `PromptBuilder`.

**Return Value:**  
A delegate that represents the created node, which takes no input and produces no output.

**Use Cases:**
- Adding system instructions to the prompt
- Inserting user messages into the conversation
- Preparing the context for subsequent LLM requests

**Example:**
```kotlin
val setupContext by nodeUpdatePrompt("setupContext") {
   system("You are a helpful assistant specialized in Kotlin programming.")
   user("I need help with Kotlin coroutines.")
}
```

### nodeLLMSendStageInput

```kotlin
fun LocalAgentSubgraphBuilderBase<*, *>.nodeLLMSendStageInput(
   name: String? = null
): LocalAgentNodeDelegate<Unit, Message.Response>
```

**Description:**  
An LLM node that updates the prompt with the user's stage input and triggers an LLM request within a write session. This node is commonly used as the first step in an agent workflow to process the initial user input.

**Parameters:**
- `name` (optional): A custom name for the node. If not provided, the property name of the delegate will be used.

**Return Value:**  
A delegate representing the defined node, which takes no input (Unit) and produces a `Message.Response` from the LLM.

**Use Cases:**
- Processing the initial user query in a conversation
- Starting a new interaction with the LLM
- Handling user input at the beginning of a workflow

**Example:**
```kotlin
val sendInput by nodeLLMSendStageInput("sendInput")
edge(nodeStart forwardTo sendInput)
```

### nodeLLMSendStageInputMultiple

```kotlin
fun LocalAgentSubgraphBuilderBase<*, *>.nodeLLMSendStageInputMultiple(
   name: String? = null
): LocalAgentNodeDelegate<Unit, List<Message.Response>>
```

**Description:**  
Creates a node that sends the current stage input to the LLM and gets multiple responses. This is useful when you need to generate multiple alternative responses to the same input.

**Parameters:**
- `name` (optional): A custom name for the node. If not provided, the property name of the delegate will be used.

**Return Value:**  
A delegate representing the defined node, which takes no input (Unit) and produces a list of `Message.Response` objects from the LLM.

**Use Cases:**
- Generating multiple alternative responses to a user query
- Creating diverse suggestions or solutions
- Implementing a response ranking or selection mechanism

**Example:**
```kotlin
val generateAlternatives by nodeLLMSendStageInputMultiple("generateAlternatives")
edge(nodeStart forwardTo generateAlternatives)
```

### nodeLLMRequest

```kotlin
fun LocalAgentSubgraphBuilderBase<*, *>.nodeLLMRequest(
   name: String? = null,
   allowToolCalls: Boolean = true
): LocalAgentNodeDelegate<String, Message.Response>
```

**Description:**  
An LLM node that processes user messages and returns a response from the LLM. The node configuration determines whether tool calls are allowed during the processing of the message.

**Parameters:**
- `name` (optional): A custom name for the node. If not provided, the property name of the delegate will be used.
- `allowToolCalls`: A flag indicating whether tool calls are permitted during the execution of the LLM process. Defaults to `true`.

**Return Value:**  
A delegate that delegates the execution of an LLM call, processing an input message and returning a `Message.Response`.

**Use Cases:**
- Processing user messages in the middle of a conversation
- Generating responses to specific questions or prompts
- Controlling whether the LLM can use tools in its response

**Example:**
```kotlin
val processQuery by nodeLLMRequest("processQuery", allowToolCalls = true)
edge(someNode forwardTo processQuery)
```

### nodeLLMRequestMultiple

```kotlin
fun LocalAgentSubgraphBuilderBase<*, *>.nodeLLMRequestMultiple(
   name: String? = null
): LocalAgentNodeDelegate<String, List<Message.Response>>
```

**Description:**  
An LLM node that sends a user message to the LLM and gets a response with tools enabled, potentially receiving multiple tool calls. This is useful when you expect the LLM to make multiple tool calls in response to a single message.

**Parameters:**
- `name` (optional): A custom name for the node. If not provided, the property name of the delegate will be used.

**Return Value:**  
A delegate representing the defined node, which takes a string input and produces a list of `Message.Response` objects from the LLM.

**Use Cases:**
- Handling complex queries that require multiple tool calls
- Generating multiple responses to a single input
- Implementing a workflow that requires multiple parallel actions

**Example:**
```kotlin
val processComplexQuery by nodeLLMRequestMultiple("processComplexQuery")
edge(someNode forwardTo processComplexQuery)
```

### nodeLLMCompressHistory

```kotlin
fun <T> LocalAgentSubgraphBuilderBase<*, *>.nodeLLMCompressHistory(
   name: String? = null,
   strategy: HistoryCompressionStrategy = HistoryCompressionStrategy.WholeHistory,
   preserveMemory: Boolean = true
): LocalAgentNodeDelegate<T, T>
```

**Description:**  
An LLM node that rewrites message history, leaving only user messages and resulting TLDR summaries. This is useful for managing long conversations by compressing the history to reduce token usage.

**Parameters:**
- `name` (optional): A custom name for the node. If not provided, the property name of the delegate will be used.
- `strategy`: The strategy to use for compressing history. Defaults to `HistoryCompressionStrategy.WholeHistory`.
- `preserveMemory`: A flag indicating whether to preserve memory messages during compression. Defaults to `true`.

**Return Value:**  
A delegate representing the defined node, which takes an input of type T and returns the same input (pass-through).

**Compression Strategies:**
- `WholeHistory`: Compresses the entire conversation history into a TLDR summary
- `FromLastNMessages(n)`: Retains only the last N messages and compresses them
- `Chunked(chunkSize)`: Splits the conversation into chunks of a specified size and compresses each chunk

**Use Cases:**
- Managing long conversations to reduce token usage
- Summarizing conversation history to maintain context
- Implementing memory management in long-running agents

**Example:**
```kotlin
val compressHistory by nodeLLMCompressHistory<String>(
   "compressHistory",
   strategy = HistoryCompressionStrategy.FromLastNMessages(10),
   preserveMemory = true
)
edge(someNode forwardTo compressHistory)
```

## Tool Nodes

### nodeExecuteTool

```kotlin
fun LocalAgentSubgraphBuilderBase<*, *>.nodeExecuteTool(
   name: String? = null
): LocalAgentNodeDelegate<Message.Tool.Call, Message.Tool.Result>
```

**Description:**  
A node that executes a single tool call and returns its result. This node is used to handle tool calls made by the LLM.

**Parameters:**
- `name` (optional): A custom name for the node. If not provided, the property name of the delegate will be used.

**Return Value:**  
A delegate representing the defined node, which takes a `Message.Tool.Call` as input and produces a `Message.Tool.Result`.

**Use Cases:**
- Executing tools requested by the LLM
- Handling specific actions in response to LLM decisions
- Integrating external functionality into the agent workflow

**Example:**
```kotlin
val executeToolCall by nodeExecuteTool("executeToolCall")
edge(llmNode forwardTo executeToolCall onToolCall { true })
```

### nodeLLMSendToolResult

```kotlin
fun LocalAgentSubgraphBuilderBase<*, *>.nodeLLMSendToolResult(
   name: String? = null
): LocalAgentNodeDelegate<Message.Tool.Result, Message.Response>
```

**Description:**  
An LLM node that processes a `ToolCall.Result` and generates a `Message.Response`. The tool result is incorporated into the prompt, and a request is made to the LLM for a response.

**Parameters:**
- `name` (optional): A custom name for the node. If not provided, the property name of the delegate will be used.

**Return Value:**  
A delegate representing the node, handling the transformation from `ToolCall.Result` to `Message.Response`.

**Use Cases:**
- Processing the results of tool executions
- Generating responses based on tool outputs
- Continuing the conversation after tool execution

**Example:**
```kotlin
val processToolResult by nodeLLMSendToolResult("processToolResult")
edge(executeToolCall forwardTo processToolResult)
```

### nodeExecuteMultipleTools

```kotlin
fun LocalAgentSubgraphBuilderBase<*, *>.nodeExecuteMultipleTools(
   name: String? = null
): LocalAgentNodeDelegate<List<Message.Tool.Call>, List<Message.Tool.Result>>
```

**Description:**  
A node that executes multiple tool calls and returns their results. This is useful when you need to execute multiple tools in parallel.

**Parameters:**
- `name` (optional): A custom name for the node. If not provided, the property name of the delegate will be used.

**Return Value:**  
A delegate representing the defined node, which takes a list of `Message.Tool.Call` objects as input and produces a list of `Message.Tool.Result` objects.

**Use Cases:**
- Executing multiple tools in parallel
- Handling complex workflows that require multiple tool executions
- Optimizing performance by batching tool calls

**Example:**
```kotlin
val executeMultipleTools by nodeExecuteMultipleTools("executeMultipleTools")
edge(llmNode forwardTo executeMultipleTools)
```

### nodeLLMSendMultipleToolResults

```kotlin
fun LocalAgentSubgraphBuilderBase<*, *>.nodeLLMSendMultipleToolResults(
   name: String? = null
): LocalAgentNodeDelegate<List<Message.Tool.Result>, List<Message.Response>>
```

**Description:**  
A node that sends multiple tool execution results to the LLM and gets multiple responses. This is useful when you need to process the results of multiple tool executions.

**Parameters:**
- `name` (optional): A custom name for the node. If not provided, the property name of the delegate will be used.

**Return Value:**  
A delegate representing the defined node, which takes a list of `Message.Tool.Result` objects as input and produces a list of `Message.Response` objects.

**Use Cases:**
- Processing the results of multiple tool executions
- Generating multiple responses based on tool outputs
- Implementing complex workflows with multiple parallel actions

**Example:**
```kotlin
val processMultipleToolResults by nodeLLMSendMultipleToolResults("processMultipleToolResults")
edge(executeMultipleTools forwardTo processMultipleToolResults)
```

## Subgraph Components

Subgraph components are higher-level abstractions that automatically create predefined graph structures for common task patterns. They simplify the creation of complex agent workflows by handling the underlying node and edge creation automatically.

### subgraphWithTask

```kotlin
fun <ProvidedResult : SubgraphResult, Input> LocalAgentSubgraphBuilderBase<*, *>.subgraphWithTask(
   toolSelectionStrategy: ToolSelectionStrategy,
   finishTool: ProvideSubgraphResult<ProvidedResult>,
   model: LLModel? = null,
   params: LLMParams? = null,
   shouldTLDRHistory: Boolean = true,
   defineTask: suspend LocalAgentStageContext.(input: Input) -> String
): LocalAgentSubgraphDelegate<Input, ProvidedResult>
```

**Description:**  
Creates a subgraph that performs a specific task defined by `defineTask`, using the tools defined by `toolSelectionStrategy`. When the LLM believes the task is finished, it calls `finishTool`, generating a structured result. This function automatically creates all the necessary graph structure to perform the task without having to manually define nodes and edges.

**Parameters:**
- `toolSelectionStrategy`: Strategy to select tools available to the LLM during this task
- `finishTool`: The tool which LLM must call to complete the task
- `model` (optional): LLM used for this task. If not provided, uses the default model
- `params` (optional): Specific LLM parameters for this task. If not provided, uses default parameters
- `shouldTLDRHistory` (optional): Whether to compress the history when starting to execute this task. Defaults to `true`
- `defineTask`: A block which defines the task. It may just return a system prompt for the task, but may also alter agent context, prompt, storage, etc.

**Return Value:**  
A delegate representing the defined subgraph, which takes an input of type `Input` and produces a result of type `ProvidedResult`.

**Use Cases:**
- Creating task-focused agent workflows without manually defining complex graph structures
- Implementing structured task execution with specific tools
- Generating structured results from LLM task execution

**Example:**
```kotlin
val generateCode by subgraphWithTask<Unit>(
   tools = listOf(readFileTool, writeFileTool, listDirectoryTool),
   model = JetBrainsAIModels.OpenAI.GPT4o,
) { _ ->
   """
    You are a code generation assistant.
    Your task is to create a simple "Hello, World!" program in Java.
    Create the necessary files and directories.
    """.trimIndent()
}
```

**Simplified Variants:**

There are several simplified variants of `subgraphWithTask` for common use cases:

1. With string result (using `StringSubgraphResult`):
```kotlin
fun <Input> LocalAgentSubgraphBuilderBase<*, *>.subgraphWithTask(
   // tools or toolSelectionStrategy
   model: LLModel? = null,
   params: LLMParams? = null,
   shouldTLDRHistory: Boolean = true,
   defineTask: suspend LocalAgentStageContext.(input: Input) -> String
): LocalAgentSubgraphDelegate<Input, StringSubgraphResult>
```

Note: You can provide either a list of tools or a toolSelectionStrategy to specify which tools are available to the LLM during task execution.

### subgraphWithVerification

```kotlin
fun <Input> LocalAgentSubgraphBuilderBase<*, *>.subgraphWithVerification(
   // tools or toolSelectionStrategy
   model: LLModel? = null,
   params: LLMParams? = null,
   shouldTLDRHistory: Boolean = true,
   defineTask: suspend LocalAgentStageContext.(input: Input) -> String
): LocalAgentSubgraphDelegate<Input, VerifiedSubgraphResult>
```

**Description:**  
A specialized version of `subgraphWithTask` that produces a `VerifiedSubgraphResult`, indicating whether a task was completed successfully and providing detailed feedback. This eliminates the need to manually implement verification logic in your graph.

**Parameters:**
- `tools` or `toolSelectionStrategy`: Specify which tools are available to the LLM during this task
- `model` (optional): LLM used for this task. If not provided, uses the default model
- `params` (optional): Specific LLM parameters for this task. If not provided, uses default parameters
- `shouldTLDRHistory` (optional): Whether to compress the history when starting to execute this task. Defaults to `true`
- `defineTask`: A block which defines the task. It may just return a system prompt for the task, but may also alter agent context, prompt, storage, etc.

**Return Value:**  
A delegate representing the defined subgraph, which takes an input of type `Input` and produces a `VerifiedSubgraphResult` containing a boolean indicating success and a message with details.

**Use Cases:**
- Implementing verification workflows that check if a task was completed correctly
- Creating quality assurance processes in agent workflows
- Generating structured verification results with success/failure status and detailed feedback

**Example:**
```kotlin
val verifyProject by subgraphWithVerification(
   tools = listOf(runTestsTool, checkBuildTool, listFilesTool),
   model = JetBrainsAIModels.OpenAI.GPT4o,
) { _ ->
   """
    You are a project verification assistant.
    Your task is to verify that the project builds correctly and all tests pass.
    Run the necessary commands to check the project's status.
    """.trimIndent()
}
```

**Simplified Variant:**

With a list of tools instead of a tool selection strategy:
```kotlin
fun <Input> LocalAgentSubgraphBuilderBase<*, *>.subgraphWithVerification(
    tools: List<Tool<*, *>>,
    model: LLModel? = null,
    params: LLMParams? = null,
    shouldTLDRHistory: Boolean = true,
    defineTask: suspend LocalAgentStageContext.(input: Input) -> String
): LocalAgentSubgraphDelegate<Input, VerifiedSubgraphResult>
```

## Usage Examples

### Chat Agent Strategy

The following example shows how to use nodes to create a chat agent strategy:

```kotlin
fun chatAgentStrategy(): LocalAgentStrategy = simpleStrategy("chat") {
   val sendInput by nodeLLMSendStageInput("sendInput")
   val nodeExecuteTool by nodeExecuteTool("nodeExecuteTool")
   val nodeSendToolResult by nodeLLMSendToolResult("nodeSendToolResult")

   val giveFeedbackToCallTools by node<String, Message.Response> { input ->
      llm.writeSession {
         updatePrompt {
            user("Don't chat with plain text! Call one of the available tools, instead: ${tools.joinToString(", ") { it.name }}")
         }

         requestLLM()
      }
   }

   edge(nodeStart forwardTo sendInput)

   edge(sendInput forwardTo nodeExecuteTool onToolCall { true })
   edge(sendInput forwardTo giveFeedbackToCallTools onAssistantMessage { true })
   edge(giveFeedbackToCallTools forwardTo giveFeedbackToCallTools onAssistantMessage { true })
   edge(giveFeedbackToCallTools forwardTo nodeExecuteTool onToolCall { true })
   edge(nodeExecuteTool forwardTo nodeSendToolResult)
   edge(nodeSendToolResult forwardTo nodeFinish onAssistantMessage { true })
   edge(nodeSendToolResult forwardTo nodeExecuteTool onToolCall { true })
   edge(nodeExecuteTool forwardTo nodeFinish onToolCall { tc -> tc.tool == "__exit__" } transformed { "Chat finished" })
}
```

### Single Run Strategy

The following example shows how to use nodes to create a single run (one-shot) strategy:

```kotlin
fun singleRunStrategy(): LocalAgentStrategy = simpleStrategy("single_run") {
   val sendInput by nodeLLMSendStageInput("sendInput")
   val nodeExecuteTool by nodeExecuteTool("nodeExecuteTool")
   val nodeSendToolResult by nodeLLMSendToolResult("nodeSendToolResult")

   edge(nodeStart forwardTo sendInput)
   edge(sendInput forwardTo nodeExecuteTool onToolCall { true })
   edge(sendInput forwardTo nodeFinish onAssistantMessage { true })
   edge(nodeExecuteTool forwardTo nodeSendToolResult)
   edge(nodeSendToolResult forwardTo nodeFinish onAssistantMessage { true })
   edge(nodeSendToolResult forwardTo nodeExecuteTool onToolCall { true })
}
```

These examples demonstrate how to combine different nodes to create complete agent workflows. The nodes are connected using edges to define the flow of execution, with conditions specifying when to follow each edge.
