// This file was automatically generated from prompt-api.md by Knit tool. Do not edit.
package ai.koog.agents.example.examplePromptApi01

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.params.LLMParams

val prompt = prompt("prompt_name", LLMParams()) {
    // Add a system message to set the context
    system("You are a helpful assistant.")

    // Add a user message
    user("Tell me about Kotlin")

    // You can also add assistant messages for few-shot examples
    assistant("Kotlin is a modern programming language...")

    // Add another user message
    user("What are its key features?")
}
