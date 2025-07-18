// This file was automatically generated from prompt-api.md by Knit tool. Do not edit.
package ai.koog.agents.example.examplePromptApi10

import ai.koog.agents.example.examplePromptApi08.prompt
import ai.koog.agents.example.examplePromptApi09.promptExecutor
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.model.PromptExecutorExt.execute
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {

// Execute a prompt
val response = promptExecutor.execute(
    prompt = prompt,
    model = OpenAIModels.Chat.GPT4o
)
    }
}
