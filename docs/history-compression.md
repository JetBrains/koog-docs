# History compression

For long-running conversations, the history can grow large and consume a lot of tokens. You can use the
`nodeLLMCompressHistory` node to compress the history:

```kotlin
val compressHistory by nodeLLMCompressHistory<Message.Tool.Result>(
    strategy = HistoryCompressionStrategy.FromLastNMessages(10),
    preserveMemory = true
)

edge(
    (nodeExecuteTool forwardTo compressHistory)
            onCondition { _ -> llm.readSession { prompt.messages.size > 100 } }
)
edge(compressHistory forwardTo nodeSendToolResult)
```
