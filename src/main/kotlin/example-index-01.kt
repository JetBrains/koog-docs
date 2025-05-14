// This file was automatically generated from index.md by Knit tool. Do not edit.
package ai.grazie.code.agents.example.exampleIndex01

import ai.grazie.code.agents.local.simpleApi.simpleChatAgent
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val apiToken = "YOUR_JETBRAINS_AI_API_TOKEN"

    val agent = simpleChatAgent(
        apiToken = apiToken,
        cs = this,
        systemPrompt = "You are a helpful assistant. Answer user questions concisely."
    )

    agent.run("Hello, how can you help me?")
}
