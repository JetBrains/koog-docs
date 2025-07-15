// This file was automatically generated from custom-strategy-graphs.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleCustomStrategyGraphs01

import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy

val strategy = strategy<String, String>("strategy_name") {
        val sourceNode by node<String, String> { input -> input }
        val targetNode by node<String, String> { input -> input }

edge(sourceNode forwardTo targetNode)
}
