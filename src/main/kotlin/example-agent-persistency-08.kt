// This file was automatically generated from agent-persistency.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleAgentPersistency08

import ai.koog.agents.snapshot.feature.AgentCheckpointData
import ai.koog.agents.snapshot.providers.PersistencyStorageProvider

class MyCustomStorageProvider : PersistencyStorageProvider {
    override suspend fun getCheckpoints(agentId: String): List<AgentCheckpointData> {
        TODO("Implementation")
    }

    override suspend fun saveCheckpoint(agentCheckpointData: AgentCheckpointData) {
        TODO("Implementation")
    }

    override suspend fun getLatestCheckpoint(agentId: String): AgentCheckpointData? {
        TODO("Implementation")
    }
}
