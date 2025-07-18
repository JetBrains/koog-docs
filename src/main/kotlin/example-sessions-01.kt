// This file was automatically generated from sessions.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleSessions01

import ai.koog.agents.core.dsl.builder.strategy


val strategy = strategy<Unit, Unit>("strategy-name") {
    val node by node<Unit, Unit> {

// Creating a write session
llm.writeSession {
    // Session code here
}

// Creating a read session
llm.readSession {
    // Session code here
}
   }
}
