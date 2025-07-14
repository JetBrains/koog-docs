// This file was automatically generated from agent-memory.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleAgentMemory02

import ai.koog.agents.memory.model.Concept
import ai.koog.agents.memory.model.DefaultTimeProvider
import ai.koog.agents.memory.model.FactType
import ai.koog.agents.memory.model.MultipleFacts

// Storing programming languages (multiple values)
val languagesFact = MultipleFacts(
    concept = Concept(
        "programming-languages",
        "Languages the user knows",
        factType = FactType.MULTIPLE
    ),
    values = listOf("Kotlin", "Java", "Python"),
    timestamp = DefaultTimeProvider.getCurrentTimestamp()
)
