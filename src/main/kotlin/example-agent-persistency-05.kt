// This file was automatically generated from agent-persistency.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleAgentPersistency05

import ai.koog.agents.core.agent.context.AIAgentContextBase
import ai.koog.agents.snapshot.feature.persistency

const val inputData = "some-input-data"

suspend fun example(context: AIAgentContextBase) {
    // Create a checkpoint with the current state
    val checkpoint = context.persistency().createCheckpoint(
        agentId = context.id,
        agentContext = context,
        nodeId = "current-node-id",
        lastInput = inputData
    )

    // The checkpoint ID can be stored for later use
    val checkpointId = checkpoint.checkpointId
}
