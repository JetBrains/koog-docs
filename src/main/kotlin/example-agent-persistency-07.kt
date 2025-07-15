// This file was automatically generated from agent-persistency.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleAgentPersistency07

import ai.koog.agents.core.agent.context.AIAgentContextBase
import ai.koog.agents.snapshot.feature.persistency
import ai.koog.agents.snapshot.feature.withPersistency

const val inputData = "some-input-data"

suspend fun example(context: AIAgentContextBase) {
    // Access the checkpoint feature
    val checkpointFeature = context.persistency()

    // Or perform an action with the checkpoint feature
    context.withPersistency(context) { ctx ->
        // 'this' is the checkpoint feature
        createCheckpoint(ctx.id, ctx, "node-id", inputData)
    }
}
