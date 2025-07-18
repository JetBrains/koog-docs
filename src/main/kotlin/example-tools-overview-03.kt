// This file was automatically generated from tools-overview.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleToolsOverview03

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.example.exampleToolsOverview01.toolRegistry
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor

// Agent initialization
val agent = AIAgent(
    executor = simpleOpenAIExecutor(System.getenv("OPENAI_API_KEY")),
    systemPrompt = "You are a helpful assistant with strong mathematical skills.",
    llmModel = OpenAIModels.Chat.GPT4o,
    // Pass your tool registry to the agent
    toolRegistry = toolRegistry
)
