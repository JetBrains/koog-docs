// This file was automatically generated from custom-nodes.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleCustomNodes07

import ai.koog.agents.core.dsl.builder.strategy

val strategy = strategy<String, String>("strategy_name") {


val loggingNode by node<String, String>("node_name") { input ->
    println("Processing input: $input")
    input // Return the input as the output
}
}
