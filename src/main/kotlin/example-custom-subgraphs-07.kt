// This file was automatically generated from custom-subgraphs.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleCustomSubgraphs07

import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.ext.tool.AskUser
import ai.koog.agents.ext.tool.SayToUser

typealias A = Unit
typealias B = Unit
typealias C = Unit

val firstTool = AskUser
val secondTool = SayToUser

val str =

strategy("complex-workflow") {
   val inputProcessing by subgraph<String, A>(
   ) {
      // Process the initial input
   }

   val reasoning by subgraph<A, B>(
   ) {
      // Perform reasoning based on the processed input
   }

   val toolRun by subgraph<B, C>(
      // Optional subset of tools from the tool registry
      tools = listOf(firstTool, secondTool)
   ) {
      // Run tools based on the reasoning
   }

   val responseGeneration by subgraph<C, String>(
   ) {
      // Generate a response based on the tool results
   }

   nodeStart then inputProcessing then reasoning then toolRun then responseGeneration then nodeFinish

}
