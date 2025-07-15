// This file was automatically generated from custom-subgraphs.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleCustomSubgraphs02

import ai.koog.agents.core.agent.entity.ToolSelectionStrategy
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.ext.tool.AskUser
import ai.koog.agents.ext.tool.SayToUser

typealias StrategyInput = Unit
typealias StrategyOutput = Unit

typealias Input = Unit
typealias Output = Unit

val firstTool = SayToUser
val secondTool = AskUser

val str = 

strategy<StrategyInput, StrategyOutput>("strategy-name") {
   val subgraphIdentifier by subgraph<Input, Output>(
       name = "subgraph-name", 
       tools = listOf(firstTool, secondTool)
   ) {
        // Define nodes and edges for this subgraph
    }
}
