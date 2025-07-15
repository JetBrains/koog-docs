// This file was automatically generated from agent-persistency.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleAgentPersistency09

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.snapshot.feature.AgentCheckpointData
import ai.koog.agents.snapshot.feature.Persistency
import ai.koog.agents.snapshot.providers.PersistencyStorageProvider
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.llm.OllamaModels

class MyCustomStorageProvider : PersistencyStorageProvider {
    override suspend fun getCheckpoints(agentId: String): List<AgentCheckpointData> {
        TODO("Not yet implemented")
    }

    override suspend fun getLatestCheckpoint(agentId: String): AgentCheckpointData? {
        TODO("Not yet implemented")
    }

    override suspend fun saveCheckpoint(agentCheckpointData: AgentCheckpointData) {
        TODO("Not yet implemented")
    }
}

val agent = AIAgent(
    executor = simpleOllamaAIExecutor(),
    llmModel = OllamaModels.Meta.LLAMA_3_2,
) {

install(Persistency) {
    storage = MyCustomStorageProvider()
}
} 
