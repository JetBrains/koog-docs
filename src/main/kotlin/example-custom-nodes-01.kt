// This file was automatically generated from custom-nodes.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleCustomNodes01

import ai.koog.agents.core.dsl.builder.strategy

typealias Input = String
typealias Output = Int

val returnValue = 42

val str = strategy<Input, Output>("my-strategy") {

val myNode by node<Input, Output>("node_name") { input ->
    // Processing
    returnValue
}
}
