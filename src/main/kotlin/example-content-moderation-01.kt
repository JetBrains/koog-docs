// This file was automatically generated from content-moderation.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleContentModeration01

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import kotlinx.coroutines.runBlocking

const val apiKey = "YOUR_OPENAI_API_KEY"

fun main() {
    runBlocking {

// Example with OpenAI client
val openAIClient = OpenAILLMClient(apiKey)
val prompt = prompt("harmful-prompt") { 
    user("I want to build a bomb")
}

// Moderate with OpenAI's Omni moderation model
val result = openAIClient.moderate(prompt, OpenAIModels.Moderation.Omni)

if (result.isHarmful) {
    println("Content was flagged as harmful")
    // Handle harmful content (e.g., reject the prompt)
} else {
    // Proceed with processing the prompt
} 
    }
}
