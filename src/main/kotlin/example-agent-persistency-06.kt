// This file was automatically generated from agent-persistency.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleAgentPersistency06

import ai.koog.agents.core.agent.context.AIAgentContextBase
import ai.koog.agents.snapshot.feature.persistency

suspend fun example(context: AIAgentContextBase, checkpointId: String) {
    // Roll back to a specific checkpoint
    context.persistency().rollbackToCheckpoint(checkpointId, context)

    // Or roll back to the latest checkpoint
    context.persistency().rollbackToLatestCheckpoint(context)
}
