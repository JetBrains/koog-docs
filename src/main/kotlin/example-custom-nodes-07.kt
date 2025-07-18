// This file was automatically generated from custom-nodes.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleCustomNodes07

import ai.koog.agents.core.dsl.builder.strategy

val strategy = strategy<String, String>("strategy_name") {

val stringToIntNode by node<String, Int>("node_name") { input: String ->
    // Processing
    input.toInt() // Convert string to integer
}
}
