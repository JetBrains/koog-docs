// This file was automatically generated from model-context-protocol.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleModelContextProtocol05

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.singleRunStrategy
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import kotlinx.coroutines.runBlocking
import ai.koog.agents.mcp.McpToolRegistryProvider
import ai.koog.agents.example.exampleModelContextProtocol04.existingMcpClient


val executor = simpleOllamaAIExecutor()
val strategy = singleRunStrategy()

fun main() {
    runBlocking {
        val toolRegistry = McpToolRegistryProvider.fromClient(
            mcpClient = existingMcpClient
        )

// Create an agent with the tools
val agent = AIAgent(
    executor = executor,
    strategy = strategy,
    llmModel = OpenAIModels.Chat.GPT4o,
    toolRegistry = toolRegistry
)

// Run the agent with a task that uses an MCP tool
val result = agent.run("Use the MCP tool to perform a task")
    }
}
