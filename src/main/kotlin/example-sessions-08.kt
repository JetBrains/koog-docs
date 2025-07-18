// This file was automatically generated from sessions.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleSessions08

import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.prompt.message.Message

val filteredMessages = emptyList<Message>()

val strategy = strategy<Unit, Unit>("strategy-name") {
    val node by node<Unit, Unit> {

llm.writeSession {
    rewritePrompt { oldPrompt ->
        // Create a new prompt based on the old one
        oldPrompt.copy(messages = filteredMessages)
    }
}
   }
}
