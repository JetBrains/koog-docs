// This file was automatically generated from sessions.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleSessions05

import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.prompt.message.Message

val strategy = strategy<Unit, Unit>("strategy-name") {
    val node by node<Unit, Unit> {

llm.writeSession {
    val response = requestLLM()

    // The response might be a tool call or a text response
    if (response is Message.Tool.Call) {
        // Handle tool call
    } else {
        // Handle text response
    }
}
   }
}
