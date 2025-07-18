// This file was automatically generated from parallel-node-execution.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleParallelNodeExecution02

import ai.koog.agents.core.dsl.builder.strategy

typealias Input = String
typealias Output = Int

val strategy = strategy<String, String>("strategy_name") {
   val nodeCalcTokens by node<Input, Output>() { 1 }
   val nodeCalcSymbols by node<Input, Output>() { 2 }
   val nodeCalcWords by node<Input, Output>() { 3 }

val calc by parallel<String, Int>(
   nodeCalcTokens, nodeCalcSymbols, nodeCalcWords,
) {
   selectByMax { it }
}
}
