// This file was automatically generated from custom-nodes.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleCustomNodes05

import ai.koog.agents.core.dsl.builder.AIAgentNodeDelegate
import ai.koog.agents.core.dsl.builder.AIAgentSubgraphBuilderBase

fun <T> AIAgentSubgraphBuilderBase<*, *>.myStatefulNode(
    name: String? = null
): AIAgentNodeDelegate<T, T> {
    var counter = 0

    return node(name) { input ->
        counter++
        println("Node executed $counter times")
        input
    }
}
