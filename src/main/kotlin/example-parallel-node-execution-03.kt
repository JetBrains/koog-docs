// This file was automatically generated from parallel-node-execution.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleParallelNodeExecution03

import ai.koog.agents.core.dsl.builder.strategy

typealias Input = String
typealias Output = String

val strategy = strategy<String, String>("strategy_name") {
   val nodeOpenAI by node<Input, Output>() { "openai" }
   val nodeAnthropicSonnet by node<Input, Output>() { "sonnet" }
   val nodeAnthropicOpus by node<Input, Output>() { "opus" }

val nodeSelectJoke by parallel<String, String>(
   nodeOpenAI, nodeAnthropicSonnet, nodeAnthropicOpus,
) {
   selectBy { it.contains("programmer") }
}
}
