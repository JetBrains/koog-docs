// This file was automatically generated from sessions.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleSessions04

import ai.koog.agents.core.dsl.builder.strategy


val strategy = strategy<Unit, Unit>("strategy-name") {
    val node by node<Unit, Unit> {

llm.writeSession {
    // Make a request with tools enabled
    val response = requestLLM()

    // Make a request without tools
    val responseWithoutTools = requestLLMWithoutTools()

    // Make a request that returns multiple responses
    val responses = requestLLMMultiple()
}
   }
}
