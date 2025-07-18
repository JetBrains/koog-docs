// This file was automatically generated from agent-persistency.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleAgentPersistency05

import ai.koog.agents.core.agent.context.AIAgentContextBase
import ai.koog.agents.snapshot.feature.persistency
import kotlin.reflect.typeOf

const val inputData = "some-input-data"
val inputType = typeOf<String>()

suspend fun example(context: AIAgentContextBase) {
    // Create a checkpoint with the current state
    val checkpoint = context.persistency().createCheckpoint(
        agentContext = context,
        nodeId = "current-node-id",
        lastInput = inputData,
        lastInputType = inputType,
        checkpointId = context.id,
    )

    // The checkpoint ID can be stored for later use
    val checkpointId = checkpoint?.checkpointId
}
