// This file was automatically generated from custom-subgraphs.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleCustomSubgraphs06

import ai.koog.agents.core.dsl.builder.strategy

val str = strategy<String, String>("my-strategy") {
    val node by node<Unit, Unit>("node_name") {

// Make a set of tools
this.llm.writeSession {
    tools = tools.filter { it.name in listOf("first_tool_name", "second_tool_name") }
}
    }
}
