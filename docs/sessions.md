# LLM Sessions and History Guide

This guide provides detailed information about LLM sessions, including how to work with read
and write sessions, manage conversation history, and make requests to language models.

## Introduction

LLM sessions are a fundamental concept that provides a structured way to interact with
language models (LLMs). They manage the conversation history, handle requests to the LLM, and provide a consistent
interface for executing tools and processing responses.

## Understanding LLM Sessions

### What are LLM Sessions?

An LLM session represents a context for interacting with a language model. It encapsulates:

- The conversation history (prompt)
- Available tools
- Methods for making requests to the LLM
- Methods for updating the conversation history
- Methods for executing tools

Sessions are managed by the `LocalAgentLLMContext` class, which provides methods for creating read and write sessions.

### Read vs. Write Sessions

The Code Engine platform provides two types of sessions:

1. **Write Sessions (`LocalAgentLLMWriteSession`)**: Allow modifying the prompt and tools, making LLM requests, and
   executing tools. Changes made in a write session are persisted back to the LLM context.

2. **Read Sessions (`LocalAgentLLMReadSession`)**: Provide read-only access to the prompt and tools. They are useful for
   inspecting the current state without making changes.

The key difference is that write sessions can modify the conversation history, while read sessions cannot.

### Session Lifecycle

Sessions have a defined lifecycle:

1. **Creation**: A session is created using `llm.writeSession { ... }` or `llm.readSession { ... }`.
2. **Active Phase**: The session is active while the lambda block is executing.
3. **Termination**: The session is automatically closed when the lambda block completes.

Sessions implement the `AutoCloseable` interface, ensuring they are properly cleaned up even if an exception occurs.

## Working with LLM Sessions

### Creating Sessions

Sessions are created using extension functions on the `LocalAgentLLMContext` class:

```kotlin
// Creating a write session
llm.writeSession {
    // Session code here
}

// Creating a read session
llm.readSession {
    // Session code here
}
```

These functions take a lambda block that executes within the context of the session. The session is automatically closed
when the block completes.

### Session Scope and Thread Safety

Sessions use a read-write lock to ensure thread safety:

- Multiple read sessions can be active simultaneously.
- Only one write session can be active at a time.
- A write session blocks all other sessions (both read and write).

This ensures that the conversation history is not corrupted by concurrent modifications.

### Accessing Session Properties

Within a session, you can access the prompt and tools:

```kotlin
llm.readSession {
    val messageCount = prompt.messages.size
    val availableTools = tools.map { it.name }
}
```

In a write session, you can also modify these properties:

```kotlin
llm.writeSession {
    // Modify the prompt
    updatePrompt {
        user("New user message")
    }

    // Modify the tools
    tools = newTools
}
```

## Making LLM Requests

### Basic Request Methods

The most common methods for making LLM requests are:

1. **`requestLLM()`**: Makes a request to the LLM with the current prompt and tools, returning a single response.

2. **`requestLLMWithoutTools()`**: Makes a request to the LLM with the current prompt but without any tools, returning a
   single response.

3. **`requestLLMMultiple()`**: Makes a request to the LLM with the current prompt and tools, returning multiple
   responses.

Example:

```kotlin
llm.writeSession {
    // Make a request with tools enabled
    val response = requestLLM()

    // Make a request without tools
    val responseWithoutTools = requestLLMWithoutTools()

    // Make a request that returns multiple responses
    val responses = requestLLMMultiple()
}
```

### When Requests Actually Happen

LLM requests are made when you explicitly call one of the request methods. The key points to understand are:

1. **Explicit Invocation**: Requests only happen when you call methods like `requestLLM()`, `requestLLMWithoutTools()`,
   etc.

2. **Immediate Execution**: When you call a request method, the request is made immediately, and the method blocks until
   a response is received.

3. **Automatic History Update**: In a write session, the response is automatically added to the conversation history.

4. **No Implicit Requests**: The system does not make implicit requests; you must explicitly call a request method.

### Request Methods with Tools

When making requests with tools enabled, the LLM may respond with a tool call instead of a text response. The request
methods handle this transparently:

```kotlin
llm.writeSession {
    val response = requestLLM()

    // The response might be a tool call or a text response
    if (response is Message.Tool.Call) {
        // Handle tool call
    } else {
        // Handle text response
    }
}
```

In practice, you typically don't need to check the response type manually, as the agent graph handles this routing
automatically.

### Structured and Streaming Requests

For more advanced use cases, the platform provides methods for structured and streaming requests:

1. **`requestLLMStructured()`**: Requests the LLM to provide a response in a specific structured format.

2. **`requestLLMStructuredOneShot()`**: Similar to `requestLLMStructured()` but without retries or corrections.

3. **`requestLLMStreaming()`**: Makes a streaming request to the LLM, returning a flow of response chunks.

Example:

```kotlin
llm.writeSession {
    // Make a structured request
    val structuredResponse = requestLLMStructured(myStructure)

    // Make a streaming request
    val responseStream = requestLLMStreaming()
    responseStream.collect { chunk ->
        // Process each chunk as it arrives
    }
}
```

## Managing Conversation History

### Updating the Prompt

In a write session, you can update the prompt (conversation history) using the `updatePrompt` method:

```kotlin
llm.writeSession {
    updatePrompt {
        // Add a system message
        system("You are a helpful assistant.")

        // Add a user message
        user("Hello, can you help me with a coding question?")

        // Add an assistant message
        assistant("Of course! What's your question?")

        // Add a tool result
        tool {
            result(myToolResult)
        }
    }
}
```

You can also completely rewrite the prompt using the `rewritePrompt` method:

```kotlin
llm.writeSession {
    rewritePrompt { oldPrompt ->
        // Create a new prompt based on the old one
        oldPrompt.copy(messages = filteredMessages)
    }
}
```

### How Responses Update History

When you make an LLM request in a write session, the response is automatically added to the conversation history:

```kotlin
llm.writeSession {
    // Add a user message
    updatePrompt {
        user("What's the capital of France?")
    }

    // Make a request - the response is automatically added to the history
    val response = requestLLM()

    // The prompt now includes both the user message and the LLM's response
}
```

This automatic history update is a key feature of write sessions, ensuring that the conversation flows naturally.

### History Compression

For long-running conversations, the history can grow large and consume a lot of tokens. The platform provides methods
for compressing history:

```kotlin
llm.writeSession {
    // Compress the history using a TLDR approach
    replaceHistoryWithTLDR(HistoryCompressionStrategy.WholeHistory, preserveMemory = true)
}
```

You can also use the `nodeLLMCompressHistory` node in a strategy graph to compress history at specific points.

## Tool Execution in Sessions

### Calling Tools

Write sessions provide several methods for calling tools:

1. **`callTool(tool, args)`**: Calls a tool by reference.

2. **`callTool(toolName, args)`**: Calls a tool by name.

3. **`callTool(toolClass, args)`**: Calls a tool by class.

4. **`callToolRaw(toolName, args)`**: Calls a tool by name and returns the raw string result.

Example:

```kotlin
llm.writeSession {
    // Call a tool by reference
    val result = callTool(myTool, myArgs)

    // Call a tool by name
    val result2 = callTool("myToolName", myArgs)

    // Call a tool by class
    val result3 = callTool(MyTool::class, myArgs)

    // Call a tool and get the raw result
    val rawResult = callToolRaw("myToolName", myArgs)
}
```

### Parallel Tool Execution

For executing multiple tools in parallel, write sessions provide extension functions on `Flow`:

```kotlin
llm.writeSession {
    // Execute tools in parallel
    parseDataToArgs(data).toParallelToolCalls(MyTool::class).collect { result ->
        // Process each result
    }

    // Execute tools in parallel and get raw results
    parseDataToArgs(data).toParallelToolCallsRaw(MyTool::class).collect { rawResult ->
        // Process each raw result
    }
}
```

This is useful for processing large amounts of data efficiently.

## Best Practices

When working with LLM sessions, follow these best practices:

1. **Use the Right Session Type**: Use write sessions when you need to modify the conversation history, and read
   sessions when you only need to read it.

2. **Keep Sessions Short**: Sessions should be focused on a specific task and closed as soon as possible to release
   resources.

3. **Handle Exceptions**: Make sure to handle exceptions within sessions to prevent resource leaks.

4. **Manage History Size**: For long-running conversations, use history compression to reduce token usage.

5. **Prefer High-Level Abstractions**: When possible, use the node-based API (e.g., `nodeLLMSendStageInput`,
   `nodeLLMRequest`) instead of directly working with sessions.

6. **Be Mindful of Thread Safety**: Remember that write sessions block other sessions, so keep write operations as short
   as possible.

7. **Use Structured Requests for Complex Data**: When you need the LLM to return structured data, use
   `requestLLMStructured` instead of parsing free-form text.

8. **Use Streaming for Long Responses**: For long responses, use `requestLLMStreaming` to process the response as it
   arrives.

## Troubleshooting

### Session Already Closed

If you see an error like "Cannot use session after it was closed", you're trying to use a session after its lambda block
has completed. Make sure all session operations are performed within the session block.

### History Too Large

If your history becomes too large and consumes too many tokens, use history compression techniques:

```kotlin
llm.writeSession {
    replaceHistoryWithTLDR(HistoryCompressionStrategy.FromLastNMessages(10), preserveMemory = true)
}
```

### Tool Not Found

If you see errors about tools not being found, check that:

- The tool is correctly registered in the tool registry
- You're using the correct tool name or class
- The stage has access to the tool
