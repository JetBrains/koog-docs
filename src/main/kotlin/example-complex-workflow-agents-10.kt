// This file was automatically generated from complex-workflow-agents.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleComplexWorkflowAgents10

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.feature.handler.AgentFinishedContext
import ai.koog.agents.core.feature.handler.AgentStartContext
import ai.koog.agents.features.eventHandler.feature.EventHandler
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.llm.OllamaModels

val agent = AIAgent(
    executor = simpleOllamaAIExecutor(),
    llmModel = OllamaModels.Meta.LLAMA_3_2,

// install the EventHandler feature
installFeatures = {
    install(EventHandler) {
        onBeforeAgentStarted { eventContext: AgentStartContext<*> ->
            println("Starting strategy: ${eventContext.strategy.name}")
        }
        onAgentFinished { eventContext: AgentFinishedContext ->
            println("Result: ${eventContext.result}")
        }
    }
}
)
