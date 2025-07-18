// This file was automatically generated from parallel-node-execution.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleParallelNodeExecution01

import ai.koog.agents.core.dsl.builder.strategy

typealias Input = Unit
typealias Output = String

val strategy = strategy<String, String>("strategy_name") {
   val firstNode by node<Input, Output>() { "first" }
   val secondNode by node<Input, Output>() { "second" }
   val thirdNode by node<Input, Output>() { "third" }

val nodeName by parallel<Input, Output>(
   firstNode, secondNode, thirdNode /* Add more nodes if needed */
) {
   // Merge strategy goes here, for example: 
   selectByMax { it.length }
}
}
