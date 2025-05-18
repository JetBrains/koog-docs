# Predefined nodes and components

Nodes are the fundamental building blocks of agent workflows in the Koog framework.
Each node represents a specific operation or transformation in the workflow, and they can be connected using edges to define the flow of execution.

In general, they let you encapsulate complex logic into reusable components that can be easily integrated into
different agent workflows. This guide will walk you through the existing nodes that can be used in your agent
strategies.

For more detailed reference documentation, see API reference.<!--[TODO] Link to API reference-->

## Utility nodes

### nodeDoNothing

A simple pass-through node that performs no actions. The input is directly passed as the output without any processing. For details, see API reference.<!--[TODO] Link to API reference-->

You can use this node for the following purposes:
- Create a placeholder node in your graph.
- Create a connection point without modifying the data.
- To debug and test your workflow.

Here is an example:

```kotlin
val passthrough by nodeDoNothing<String>("passthrough")

edge(someNode forwardTo passthrough)
edge(passthrough forwardTo anotherNode)
```

## LLM nodes

### nodeUpdatePrompt

A node that updates the prompt without asking the LLM for a response. This is useful for modifying the conversation
context before making an actual LLM request. For details, see API reference.<!--[TODO] Link to API reference-->

You can use this node for the following purposes:

- Add system instructions to the prompt.
- Insert user messages into the conversation.
- Prepare the context for subsequent LLM requests.

Here is an example:

```kotlin
val setupContext by nodeUpdatePrompt("setupContext") {
    system("You are a helpful assistant specialized in Kotlin programming.")
    user("I need help with Kotlin coroutines.")
}
```

### nodeLLMSendStageInput

An LLM node that updates the prompt with the user stage input and triggers an LLM request within a writing session. This
node is commonly used as the first step in an agent workflow to process the initial user input. For details, see API reference.<!--[TODO] Link to API reference-->

You can use this node for the following purposes:

- Process an initial user query in a conversation.
- Start a new interaction with the LLM.
- Handle a user input at the beginning of a workflow.

Here is an example:

```kotlin
val sendInput by nodeLLMSendStageInput("sendInput")
edge(nodeStart forwardTo sendInput)
```

### nodeLLMSendStageInputMultiple

An LLM node that sends the current stage input to the LLM and gets multiple responses. This is useful when you need
to generate multiple alternative responses to the same input. For details, see API reference.<!--[TODO] Link to API reference-->

You can use this node for the following purposes:

- Generate multiple alternative responses to a user query.
- Create diverse suggestions or solutions.
- Implement a response ranking or selection mechanism.

Here is an example:

```kotlin
val generateAlternatives by nodeLLMSendStageInputMultiple("generateAlternatives")
edge(nodeStart forwardTo generateAlternatives)
```

### nodeLLMRequest

An LLM node that processes user messages and returns a response from the LLM. The node configuration determines whether
tool calls are allowed during the processing of the message. For details, see API reference.<!--[TODO] Link to API reference-->

You can use this node for the following purposes:

- Process user messages in the middle of a conversation.
- Generate responses to specific questions or prompts.
- Control whether the LLM can use tools in its response.

Here is an example:

```kotlin
val processQuery by nodeLLMRequest("processQuery", allowToolCalls = true)
edge(someNode forwardTo processQuery)
```

### nodeLLMRequestMultiple

An LLM node that sends a user message to the LLM and gets a response with tools enabled, potentially receiving multiple
tool calls. This is useful when you expect the LLM to make multiple tool calls in response to a single message. For details, see API reference.<!--[TODO] Link to API reference-->

You can use this node for the following purposes:

- Handling complex queries that require multiple tool calls.
- Generating multiple responses to a single input.
- Implementing a workflow that requires multiple parallel actions.

Here is an example:

```kotlin
val processComplexQuery by nodeLLMRequestMultiple("processComplexQuery")
edge(someNode forwardTo processComplexQuery)
```

### nodeLLMCompressHistory
 
An LLM node that rewrites message history, leaving only user messages and resulting summaries. This is useful for
managing long conversations by compressing the history to reduce token usage. The following compression strategies are available:

- **WholeHistory**: compresses the entire conversation history into a summary.
- **FromLastNMessages**: retains only the last N messages and compresses them.
- **Chunked**: splits the conversation into chunks of a specified size and compresses each chunk.

For details, see API reference.<!--[TODO] Link to API reference-->

You can use this node for the following purposes:

- Manage long conversations to reduce token usage.
- Summarize conversation history to maintain context.
- Implement memory management in long-running agents.


Here is an example:

```kotlin
val compressHistory by nodeLLMCompressHistory<String>(
    "compressHistory",
    strategy = HistoryCompressionStrategy.FromLastNMessages(10),
    preserveMemory = true
)
edge(someNode forwardTo compressHistory)
```

## Tool nodes

### nodeExecuteTool

A node that executes a single tool call and returns its result. This node is used to handle tool calls made by the LLM. For details, see API reference.<!--[TODO] Link to API reference-->

You can use this node for the following purposes:

- Execute tools requested by the LLM.
- Handle specific actions in response to LLM decisions.
- Integrate external functionality into the agent workflow.

Here is an example:

```kotlin
val executeToolCall by nodeExecuteTool("executeToolCall")
edge(llmNode forwardTo executeToolCall onToolCall { true })
```

### nodeLLMSendToolResult

An LLM node that processes a tool call result and generates a response. The tool result is incorporated into
the prompt, and a request is made to the LLM for a response. For details, see API reference.<!--[TODO] Link to API reference-->

You can use this node for the following purposes:

- Process the results of tool executions.
- Generate responses based on tool outputs.
- Continue a conversation after tool execution.

Here is an example:

```kotlin
val processToolResult by nodeLLMSendToolResult("processToolResult")
edge(executeToolCall forwardTo processToolResult)
```

### nodeExecuteMultipleTools

A node that executes multiple tool calls and returns their results. This is useful when you need to execute multiple
tools in parallel. For details, see API reference.<!--[TODO] Link to API reference-->

You can use this node for the following purposes:

- Execute multiple tools in parallel.
- Handle complex workflows that require multiple tool executions.
- Optimize performance by batching tool calls.

Here is an example:

```kotlin
val executeMultipleTools by nodeExecuteMultipleTools("executeMultipleTools")
edge(llmNode forwardTo executeMultipleTools)
```

### nodeLLMSendMultipleToolResults

A node that sends multiple tool execution results to the LLM and gets multiple responses. This is useful when you need
to process the results of multiple tool executions. For details, see API reference.<!--[TODO] Link to API reference-->

You can use this node for the following purposes:

- Process the results of multiple tool executions.
- Generate multiple responses based on tool outputs.
- Implement complex workflows with multiple parallel actions.
  
Here is an example:

```kotlin
val processMultipleToolResults by nodeLLMSendMultipleToolResults("processMultipleToolResults")
edge(executeMultipleTools forwardTo processMultipleToolResults)
```

## Predefined components

The framework provides predefined strategies that combine various nodes.
The nodes are connected using edges to define the flow of operations, with conditions that specify when to follow each edge.

You can integrate these strategies into your agent workflows.

### Chat agent strategy

This predefined strategy runs a chat interaction process. It lets an agent interact with a user in a chat manner.

```kotlin
public fun chatAgentStrategy(): AIAgentStrategy = strategy("chat") {
    val nodeCallLLM by nodeLLMRequest("sendInput")
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
  
    edge(nodeStart forwardTo nodeCallLLM)
  
    edge(nodeCallLLM forwardTo nodeExecuteTool onToolCall { true })
    edge(nodeCallLLM forwardTo giveFeedbackToCallTools onAssistantMessage { true })
    edge(giveFeedbackToCallTools forwardTo giveFeedbackToCallTools onAssistantMessage { true })
    edge(giveFeedbackToCallTools forwardTo nodeExecuteTool onToolCall { true })
    edge(nodeExecuteTool forwardTo nodeSendToolResult)
    edge(nodeSendToolResult forwardTo nodeFinish onAssistantMessage { true })
    edge(nodeSendToolResult forwardTo nodeExecuteTool onToolCall { true })
    edge(nodeExecuteTool forwardTo nodeFinish onToolCall { tc -> tc.tool == "__exit__" } transformed { "Chat finished" })
}

```

### Single run strategy

This predefined strategy handles an agent workflow in a single iteration. 
You can use this strategy when you need to run straightforward processes that do not require complex logic.

```kotlin
public fun singleRunStrategy(): AIAgentStrategy = strategy("single_run") {
    val nodeCallLLM by nodeLLMRequest("sendInput")
    val nodeExecuteTool by nodeExecuteTool("nodeExecuteTool")
    val nodeSendToolResult by nodeLLMSendToolResult("nodeSendToolResult")
  
    edge(nodeStart forwardTo nodeCallLLM)
    edge(nodeCallLLM forwardTo nodeExecuteTool onToolCall { true })
    edge(nodeCallLLM forwardTo nodeFinish onAssistantMessage { true })
    edge(nodeExecuteTool forwardTo nodeSendToolResult)
    edge(nodeSendToolResult forwardTo nodeFinish onAssistantMessage { true })
    edge(nodeSendToolResult forwardTo nodeExecuteTool onToolCall { true })
}
```

