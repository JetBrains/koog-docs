// This file was automatically generated from history-compression.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleHistoryCompression12

import ai.koog.agents.core.agent.session.AIAgentLLMWriteSession
import ai.koog.agents.core.dsl.extension.HistoryCompressionStrategy
import ai.koog.prompt.message.Message

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
