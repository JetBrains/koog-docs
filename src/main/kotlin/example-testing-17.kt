// This file was automatically generated from testing.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleTesting17

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.example.exampleTesting02.mockLLMApi
import ai.koog.agents.example.exampleTesting02.toolRegistry
import ai.koog.agents.testing.feature.testGraph
import ai.koog.prompt.executor.clients.openai.OpenAIModels


val llmModel = OpenAIModels.Chat.GPT4o

fun main() {
    AIAgent(
        // Constructor arguments
        executor = mockLLMApi,
        toolRegistry = toolRegistry,
        llmModel = llmModel
    ) {

testGraph<Unit, String>("test") {
    val mySubgraph = assertSubgraphByName<Unit, String>("mySubgraph")

    verifySubgraph(mySubgraph) {
        // Get references to nodes
        val nodeA = assertNodeByName<Unit, String>("nodeA")
        val nodeB = assertNodeByName<String, String>("nodeB")

        // Assert reachability
        assertReachable(nodeA, nodeB)

        // Assert edge connections
        assertEdges {
            nodeA.withOutput("result") goesTo nodeB
        }
    }
}
    }
}
