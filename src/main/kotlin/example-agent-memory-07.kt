// This file was automatically generated from agent-memory.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleAgentMemory07

import ai.koog.agents.example.exampleAgentMemory03.MemorySubjects
import ai.koog.agents.example.exampleAgentMemory06.memoryProvider
import ai.koog.agents.memory.model.Concept
import ai.koog.agents.memory.model.DefaultTimeProvider
import ai.koog.agents.memory.model.FactType
import ai.koog.agents.memory.model.MemoryScope
import ai.koog.agents.memory.model.SingleFact

suspend fun main() {

memoryProvider.save(
    fact = SingleFact(
        concept = Concept("greeting", "User's name", FactType.SINGLE),
        value = "John",
        timestamp = DefaultTimeProvider.getCurrentTimestamp()
    ),
    subject = MemorySubjects.User,
    scope = MemoryScope.Product("my-app"),
)
}
