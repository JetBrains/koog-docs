// This file was automatically generated from tracing.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleTracing10

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.feature.model.AIAgentNodeExecutionStartEvent
import ai.koog.agents.core.feature.model.AfterLLMCallEvent
import ai.koog.agents.features.common.message.FeatureMessage
import ai.koog.agents.features.common.message.FeatureMessageProcessor
import ai.koog.agents.features.tracing.feature.Tracing
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.llm.OllamaModels
import kotlinx.coroutines.runBlocking

fun main() {
   runBlocking {
      // Creating an agent
      val agent = AIAgent(
         executor = simpleOllamaAIExecutor(),
         llmModel = OllamaModels.Meta.LLAMA_3_2,
      ) {

class CustomTraceProcessor : FeatureMessageProcessor() {
    override suspend fun processMessage(message: FeatureMessage) {
        // Custom processing logic
        when (message) {
            is AIAgentNodeExecutionStartEvent -> {
                // Process node start event
            }

            is AfterLLMCallEvent -> {
                // Process LLM call end event
           }
            // Handle other event types 
        }
    }

    override suspend fun close() {
        // Close connections of established
    }
}

// Use your custom processor
install(Tracing) {
    addMessageProcessor(CustomTraceProcessor())
}
        }
    }
}
