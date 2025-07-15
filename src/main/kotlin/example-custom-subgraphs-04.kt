// This file was automatically generated from custom-subgraphs.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleCustomSubgraphs04

import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.ext.tool.AskUser

val str = strategy<String, String>("my-strategy") {

val mySubgraph by subgraph<String, String>(
   tools = listOf(AskUser)
 ) {
    // Subgraph definition
 }
}
