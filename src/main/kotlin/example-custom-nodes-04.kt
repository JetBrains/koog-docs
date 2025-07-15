// This file was automatically generated from custom-nodes.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleCustomNodes04

import ai.koog.agents.core.dsl.builder.AIAgentNodeDelegate
import ai.koog.agents.core.dsl.builder.AIAgentSubgraphBuilderBase
import ai.koog.agents.core.dsl.builder.strategy

val strategy = strategy<String, String>("strategy_name") {

    fun <T> AIAgentSubgraphBuilderBase<*, *>.myParameterizedNode(
    name: String? = null,
    param1: String,
    param2: Int
): AIAgentNodeDelegate<T, T> = node(name) { input ->
    // Use param1 and param2 in your custom logic
    input // Return the input as the output
}

val myCustomNode by myParameterizedNode<String>("node_name", param1 = "value1", param2 = 42)
}
