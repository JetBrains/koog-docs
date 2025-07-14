// This file was automatically generated from agent-memory.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleAgentMemory13

import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.example.exampleAgentMemory03.MemorySubjects
import ai.koog.agents.memory.feature.nodes.nodeSaveToMemoryAutoDetectFacts

fun main() {
    val strategy = strategy<Unit, Unit>("example-agent") {

val saveAutoDetect by nodeSaveToMemoryAutoDetectFacts<Unit>(
    subjects = listOf(MemorySubjects.User, MemorySubjects.Machine)
)
    }
}
