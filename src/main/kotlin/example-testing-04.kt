// This file was automatically generated from testing.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleTesting04

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.example.exampleTesting02.mockLLMApi
import ai.koog.agents.example.exampleTesting02.toolRegistry
import ai.koog.agents.testing.feature.withTesting
import ai.koog.prompt.executor.clients.openai.OpenAIModels

val llmModel = OpenAIModels.Chat.GPT4o

// Create the agent with testing enabled
fun main() {

// Create the agent with testing enabled
AIAgent(
    executor = mockLLMApi,
    toolRegistry = toolRegistry,
    llmModel = llmModel
) {
    // Enable testing mode
    withTesting()
}
}
