// This file was automatically generated from agent-memory.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleAgentMemory09

import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.example.exampleAgentMemory03.MemorySubjects
import ai.koog.agents.memory.feature.nodes.nodeSaveToMemoryAutoDetectFacts
import ai.koog.agents.memory.feature.withMemory
import ai.koog.agents.memory.model.Concept
import ai.koog.agents.memory.model.FactType

val strategy = strategy("example-agent") {
    // Node to automatically detect and save facts
    val detectFacts by nodeSaveToMemoryAutoDetectFacts<Unit>(
        subjects = listOf(MemorySubjects.User, MemorySubjects.Machine)
    )

    // Node to load specific facts
    val loadPreferences by node<Unit, Unit> {
        withMemory {
            loadFactsToAgent(
                concept = Concept("user-preference", "User's preferred programming language", FactType.SINGLE),
                subjects = listOf(MemorySubjects.User)
            )
        }
    }

    // Connect nodes in the strategy
    edge(nodeStart forwardTo detectFacts)
    edge(detectFacts forwardTo loadPreferences)
    edge(loadPreferences forwardTo nodeFinish)
}
