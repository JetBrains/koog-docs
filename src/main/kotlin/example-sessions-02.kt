// This file was automatically generated from sessions.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleSessions02

import ai.koog.agents.core.dsl.builder.strategy


val strategy = strategy<Unit, Unit>("strategy-name") {
    val node by node<Unit, Unit> {

llm.readSession {
    val messageCount = prompt.messages.size
    val availableTools = tools.map { it.name }
}
   }
}
