// This file was automatically generated from tracing.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleTracing02

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.feature.model.*
import ai.koog.agents.features.tracing.feature.Tracing
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.llm.OllamaModels

val agent = AIAgent(
   executor = simpleOllamaAIExecutor(),
   llmModel = OllamaModels.Meta.LLAMA_3_2,
) {
   install(Tracing) {

// Filter for LLM-related events only
messageFilter = { message -> 
    message is BeforeLLMCallEvent || message is AfterLLMCallEvent
}

// Filter for tool-related events only
messageFilter = { message -> 
    message is ToolCallEvent ||
           message is ToolCallResultEvent ||
           message is ToolValidationErrorEvent ||
           message is ToolCallFailureEvent
}

// Filter for node execution events only
messageFilter = { message -> 
    message is AIAgentNodeExecutionStartEvent || message is AIAgentNodeExecutionEndEvent
}
   }
}
