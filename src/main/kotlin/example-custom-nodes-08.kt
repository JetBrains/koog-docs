// This file was automatically generated from custom-nodes.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleCustomNodes08

import ai.koog.agents.core.dsl.builder.strategy

val strategy = strategy<String, String>("strategy_name") {

val upperCaseNode by node<String, String>("node_name") { input ->
    println("Processing input: $input")
    input.uppercase() // Transform the input to uppercase
}
}
