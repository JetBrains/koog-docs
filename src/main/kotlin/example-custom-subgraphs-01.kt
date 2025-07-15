// This file was automatically generated from custom-subgraphs.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleCustomSubgraphs01

import ai.koog.agents.core.agent.entity.ToolSelectionStrategy
import ai.koog.agents.core.dsl.builder.strategy

typealias StrategyInput = Unit
typealias StrategyOutput = Unit

typealias Input = Unit
typealias Output = Unit

val str = 

strategy<StrategyInput, StrategyOutput>("strategy-name") {
    val subgraphIdentifier by subgraph<Input, Output>(
        name = "subgraph-name",
        toolSelectionStrategy = ToolSelectionStrategy.ALL
    ) {
        // Define nodes and edges for this subgraph
    }
}
