// This file was automatically generated from complex-workflow-agents.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleComplexWorkflowAgents11

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.feature.handler.AgentFinishedContext
import ai.koog.agents.core.feature.handler.AgentStartContext
import ai.koog.agents.example.exampleComplexWorkflowAgents01.promptExecutor
import ai.koog.agents.example.exampleComplexWorkflowAgents06.agentStrategy
import ai.koog.agents.example.exampleComplexWorkflowAgents07.agentConfig
import ai.koog.agents.example.exampleComplexWorkflowAgents09.toolRegistry
import ai.koog.agents.features.eventHandler.feature.EventHandler
import kotlinx.coroutines.runBlocking

val agent = AIAgent(
    promptExecutor = promptExecutor,
    toolRegistry = toolRegistry,
    strategy = agentStrategy,
    agentConfig = agentConfig,
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

fun main() {
    runBlocking {
        println("Enter two numbers to add (e.g., 'add 5 and 7' or '5 + 7'):")

        // Read the user input and send it to the agent
        val userInput = readlnOrNull() ?: ""
        val agentResult = agent.run(userInput)
        println("The agent returned: $agentResult")
    }
}
