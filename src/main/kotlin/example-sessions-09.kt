// This file was automatically generated from sessions.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleSessions09

import ai.koog.agents.core.dsl.builder.strategy


val strategy = strategy<Unit, Unit>("strategy-name") {
    val node by node<Unit, Unit> {

llm.writeSession {
    // Add a user message
    updatePrompt {
        user("What's the capital of France?")
    }

    // Make a request – the response is automatically added to the history
    val response = requestLLM()

    // The prompt now includes both the user message and the model's response
}
   }
}
