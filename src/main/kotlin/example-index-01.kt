// This file was automatically generated from index.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleIndex01

import ai.koog.agents.ext.agent.simpleChatAgent
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val apiKey = "YOUR_OPENAI_API_KEY"

    val agent = simpleChatAgent(
        executor = simpleOpenAIExecutor(apiKey),
        llmModel = OpenAIModels.Chat.GPT4_1,
        systemPrompt = "You are a helpful assistant. Answer user questions concisely."
    )

    agent.run("Hello, how can you help me?")
}
