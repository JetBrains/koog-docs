// This file was automatically generated from agent-persistency.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleAgentPersistency02

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.snapshot.feature.Persistency
import ai.koog.agents.snapshot.providers.InMemoryPersistencyStorageProvider
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.llm.OllamaModels

val executor = simpleOllamaAIExecutor()

val agent = AIAgent(
    executor = executor,
    llmModel = OllamaModels.Meta.LLAMA_3_2,
) {
    install(Persistency) {
        // Use in-memory storage for snapshots
        storage = InMemoryPersistencyStorageProvider("in-memory-storage")
        // Enable automatic persistency
        enableAutomaticPersistency = true
    }
}
