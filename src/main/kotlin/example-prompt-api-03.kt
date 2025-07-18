// This file was automatically generated from prompt-api.md by Knit tool. Do not edit.
package ai.koog.agents.example.examplePromptApi03

import ai.koog.agents.example.examplePromptApi01.prompt
import ai.koog.agents.example.examplePromptApi02.client
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {

// Execute the prompt
val response = client.execute(
    prompt = prompt,
    model = OpenAIModels.Chat.GPT4o  // You can choose different models
)
    }
}
