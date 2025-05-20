# History Compression

## TLDR

For long-running conversations, the history can grow large and consume a lot of tokens. You can use the
`nodeLLMCompressHistory` node to compress the history.

## What is History Compression?

LLM-based AI agents maintain a message history that includes user messages, assistant responses, tool calls, and tool responses. This history naturally grows with each interaction as the agent progresses through its strategy. Each node in an AIAgent's strategy might add new messages or tool calls to the history.

History compression allows changing the list of all messages (including tool calls) to one (or more) messages that contain only important information necessary for further agent execution. This condensed representation preserves the essential context while reducing the overall size of the message history.

## Why Compress the History?

History compression addresses several key challenges in LLM-based agent systems:

1. **Context Window Limitations**: LLMs have finite context windows and perform much better with smaller, more focused contexts. Some LLMs won't work at all if the context exceeds their maximum token limit.

2. **Performance Improvement**: With a compressed history, the LLM analyzes fewer messages to make each decision, resulting in faster response times.

3. **Accuracy Enhancement**: By operating with only specific and relevant information without distracting messages, the LLM can maintain focus on the current task.

4. **Cost Efficiency**: You don't have to pay for extra tokens in irrelevant messages, reducing the overall cost of LLM API calls.

## When to Compress History

History compression is typically performed at specific points in an agent's execution:

1. **Between Logical Stages**: Compress history between different logical stages or steps of an agent's strategy.

2. **When Context Becomes Too Long**: Compress whenever the message history (i.e., context) in the current LLM prompt becomes too long.

## How to Compress History

There are two main ways to implement history compression in your agent:

### 1. In a Strategy Graph

When writing a strategy graph, use the `nodeLLMCompressHistory()` function:

```kotlin
val strategy = strategy("execute-with-history-compression") {
    val callLLM by nodeLLMRequest()
    val executeTool by nodeExecuteTool()
    val sendToolResult by nodeLLMSendToolResult()
    
    // Compresses the LLM history and keeps the current ReceivedToolResult for the next node
    val compressHistory by nodeLLMCompressHistory<ReceivedToolResult>() 

    edge(nodeStart forwardTo callLLM)
    edge(callLLM forwardTo nodeFinish onAssistantMessage { true })
    edge(callLLM forwardTo executeTool onToolCall { true })
    
    // Compressing history after executing any tool if the history is too long 
    edge(executeTool forwardTo compressHistory onCondition { historyIsTooLong() })
    edge(compressHistory forwardTo sendToolResult)
    // Otherwise, proceeding to the next LLM request
    edge(executeTool forwardTo sendToolResult onCondition { !historyIsTooLong() })
    
    edge(sendToolResult forwardTo executeTool onToolCall { true })
    edge(sendToolResult forwardTo nodeFinish onAssistantMessage { true })
}
```

```kotlin
// Let's define that the history is too long if there's more than 100 messages
private suspend fun AIAgentContextBase.historyIsTooLong(): Boolean = llm.readSession { prompt.messages.size > 100 }
```

Or you can decide to compress the history between the logical steps (subgraphs) of your strategy:

```kotlin
val strategy = strategy("execute-with-history-compression") {
    val collectInformation by subgraph<String, String> {
        // some steps to collect the information
    }
    val compressHistory by nodeLLMCompressHistory<String>()
    val makeTheDecision by subgraph<String, Decision> {
        // some steps to make the decision based on the current compressed history + the collected information
    }
    
    nodeStart then collectInformation then compressHistory then makeTheDecision
}
```

### 2. In a Custom Node

If you are implementing your custom node, use:

```kotlin
llm.writeSession {
    replaceHistoryWithTLDR()
}
```

## History Compression Strategies

You can pass an optional `strategy` parameter to `nodeLLMCompressHistory(strategy=...)` or to `replaceHistoryWithTLDR(strategy=...)` that affects the history compression process.

### WholeHistory (Default)

Compresses the entire history into one TLDR message summarizing what has been achieved so far.

```kotlin
val compressHistory by nodeLLMCompressHistory<ProcessedInput>(
    strategy = HistoryCompressionStrategy.WholeHistory
)
```

Or in a custom node:

```kotlin
llm.writeSession {
    replaceHistoryWithTLDR(strategy = HistoryCompressionStrategy.WholeHistory)
}
```

### FromLastNMessages

Compresses only the last `n` messages into a TLDR message and completely drops the earlier messages.

#### When to use

This is useful when only the latest achievements of the agent (or the latest discovered facts, the latest context) are relevant for solving the problem.

#### Examples

In the strategy graph:

```kotlin
val compressHistory by nodeLLMCompressHistory<ProcessedInput>(
    strategy = HistoryCompressionStrategy.FromLastNMessages(5)
)
```

Or in a custom node:

```kotlin
llm.writeSession {
    replaceHistoryWithTLDR(strategy = HistoryCompressionStrategy.FromLastNMessages(5))
}
```

### Chunked

Splits the whole message history into chunks of a fixed size and compresses each chunk independently into a TLDR message.

#### When to use

This is useful when you need not only the concise TLDR of what has been done so far but also want to keep track of the overall progress, and some older information might also be important.

#### Examples

In the strategy graph:

```kotlin
val compressHistory by nodeLLMCompressHistory<ProcessedInput>(
    strategy = HistoryCompressionStrategy.Chunked(10)
)
```

Or in a custom node:

```kotlin
llm.writeSession {
    replaceHistoryWithTLDR(strategy = HistoryCompressionStrategy.Chunked(10))
}
```

### RetrieveFactsFromHistory

Searches for specific facts relevant to the provided list of concepts in the history and retrieves them. It changes the whole history to just these facts and leaves them as context for future LLM requests.
#### When to use

This is useful when you have an idea of what exact facts will be relevant for the LLM to perform better on the task.

#### Examples

In the strategy graph:

```kotlin
val compressHistory by nodeLLMCompressHistory<ProcessedInput>(
    strategy = RetrieveFactsFromHistory(
        Concept(
            keyword = "user_preferences",
            // Description to the LLM -- what specifically to search for
            description = "User's preferences for the recommendation system, including the preferred conversation style, theme in the application, etc.",
            // LLM would search for multiple relevant facts related to this concept:
            factType = FactType.MULTIPLE
        ),
        Concept(
            keyword = "product_details",
            // Description to the LLM -- what specifically to search for
            description = "Brief details about products in the catalog the user has been checking",
            // LLM would search for multiple relevant facts related to this concept:
            factType = FactType.MULTIPLE
        ),
        Concept(
            keyword = "issue_solved",
            // Description to the LLM -- what specifically to search for
            description = "Was the initial user's issue resolved?",
            // LLM would search for a single answer to the question:
            factType = FactType.SINGLE
        )
    )
)
```

Or in a custom node:

```kotlin
llm.writeSession {
    replaceHistoryWithTLDR(
        strategy = RetrieveFactsFromHistory(
            Concept(
                keyword = "user_preferences", 
                // Description to the LLM -- what specifically to search for
                description = "User's preferences for the recommendation system, including the preferred conversation style, theme in the application, etc.",
                // LLM would search for multiple relevant facts related to this concept:
                factType = FactType.MULTIPLE
            ),
            Concept(
                keyword = "product_details",
                // Description to the LLM -- what specifically to search for
                description = "Brief details about products in the catalog the user has been checking",
                // LLM would search for multiple relevant facts related to this concept:
                factType = FactType.MULTIPLE
            ),
            Concept(
                keyword = "issue_solved",
                // Description to the LLM -- what specifically to search for
                description = "Was the initial user's issue resolved?",
                // LLM would search for a single answer to the question:
                factType = FactType.SINGLE
            )
        )
    )
}
```

## Implementing Your Own History Compression Strategy

You can create your own history compression strategy by extending the `HistoryCompressionStrategy` abstract class and implementing the `compress` method:

```kotlin
class MyCustomCompressionStrategy : HistoryCompressionStrategy() {
    override suspend fun compress(
        llmSession: AIAgentLLMWriteSession,
        preserveMemory: Boolean,
        memoryMessages: List<Message>
    ) {
        // 1. Process the current history in llmSession.prompt.messages
        // 2. Create new compressed messages
        // 3. Update the prompt with the compressed messages

        // Example implementation:
        val importantMessages = llmSession.prompt.messages.filter {
            // Your custom filtering logic
            it.content.contains("important")
        }.filterIsInstance<Message.Response>()
        
        // Note: you can also make LLM requests using the `llmSession` and ask the LLM to do some job for you using, for example, `llmSession.requestLLMWithoutTools()`
        // Or you can change the current model: `llmSession.model = AnthropicModels.Sonnet_3_7` and ask some other LLM model -- but don't forget to change it back after

        // Compose the prompt with the filtered messages
        composePromptWithRequiredMessages(
            llmSession,
            importantMessages,
            preserveMemory,
            memoryMessages
        )
    }
}
```

Then use your custom strategy:

```kotlin
val compressHistory by nodeLLMCompressHistory<ProcessedInput>(
    strategy = MyCustomCompressionStrategy()
)
```

Or in a custom node:

```kotlin
llm.writeSession {
    replaceHistoryWithTLDR(strategy = MyCustomCompressionStrategy())
}
```

## Preserving Memory

All history compression methods have a `preserveMemory` parameter (default: `true`) that determines whether memory-related messages should be preserved during compression. These are messages that contain facts retrieved from memory or indicate that the memory feature is not enabled.

```kotlin
val compressHistory by nodeLLMCompressHistory<ProcessedInput>(
    strategy = HistoryCompressionStrategy.WholeHistory,
    preserveMemory = true
)
```

Or in a custom node:

```kotlin
llm.writeSession {
    replaceHistoryWithTLDR(
        strategy = HistoryCompressionStrategy.WholeHistory,
        preserveMemory = true
    )
}
```

## Conclusion

History compression is a powerful technique for managing the context window of LLM-based agents. By using the appropriate compression strategy, you can improve performance, accuracy, and cost-efficiency while ensuring that the agent has access to the most relevant information for its current task.