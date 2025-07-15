// This file was automatically generated from agent-events.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleEvents01

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.features.eventHandler.feature.handleEvents
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.llm.OllamaModels

val agent = AIAgent(
    executor = simpleOllamaAIExecutor(),
    llmModel = OllamaModels.Meta.LLAMA_3_2,
) {

handleEvents {
    // Handle tool calls
    onToolCall { eventContext ->
        println("Tool called: ${eventContext.tool} with args ${eventContext.toolArgs}")
    }
    // Handle event triggered when the agent completes its execution
    onAgentFinished { eventContext ->
        println("Agent finished with result: ${eventContext.result}")
    }

    // Other event handlers
}
} 
