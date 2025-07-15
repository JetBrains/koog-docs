// This file was automatically generated from annotation-based-tools.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleAnnotationBasedTools09

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.agents.example.exampleAnnotationBasedTools06.MyFirstToolSet
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import kotlinx.coroutines.runBlocking

const val apiToken = ""

fun main() {
    runBlocking {
        // Create your tool set
        val weatherTools = MyFirstToolSet()

        // Create an agent with your tools

        val agent = AIAgent(
            executor = simpleOpenAIExecutor(apiToken),
            systemPrompt = "Provide weather information for a given location.",
            llmModel = OpenAIModels.Chat.GPT4o,
            toolRegistry = ToolRegistry {
                tools(weatherTools.asTools())
            }
        )

        // The agent can now use your weather tools
        agent.run("What's the weather like in New York?")
    }
}
