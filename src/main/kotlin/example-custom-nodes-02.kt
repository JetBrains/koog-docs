// This file was automatically generated from custom-nodes.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleCustomNodes02

import ai.koog.agents.core.dsl.builder.strategy

val str = strategy<String, Int>("my-strategy") {

val myNode by node<String, Int>("node_name") { input ->
    // Processing
    input.length
}
}
