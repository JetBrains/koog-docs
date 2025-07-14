// This file was automatically generated from agent-memory.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleAgentMemory01

import ai.koog.agents.memory.model.Concept
import ai.koog.agents.memory.model.DefaultTimeProvider
import ai.koog.agents.memory.model.FactType
import ai.koog.agents.memory.model.SingleFact

// Storing favorite IDE theme (single value)
val themeFact = SingleFact(
    concept = Concept(
        "ide-theme", 
        "User's preferred IDE theme", 
        factType = FactType.SINGLE),
    value = "Dark Theme",
    timestamp = DefaultTimeProvider.getCurrentTimestamp()
)
