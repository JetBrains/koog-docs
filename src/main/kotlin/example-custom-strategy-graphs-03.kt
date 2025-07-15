// This file was automatically generated from custom-strategy-graphs.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleCustomStrategyGraphs03

import ai.koog.agents.core.dsl.builder.strategy

typealias Input = String
typealias Output = Int

typealias FirstInput = String
typealias FirstOutput = Int

typealias SecondInput = String
typealias SecondOutput = Int

val strategy = strategy<Input, Output>("strategy-name") {
    val firstSubgraph by subgraph<FirstInput, FirstOutput>("first") {
        // Define nodes and edges for this subgraph
    }
    val secondSubgraph by subgraph<SecondInput, SecondOutput>("second") {
        // Define nodes and edges for this subgraph
    }
}
