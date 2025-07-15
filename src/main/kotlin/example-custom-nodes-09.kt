// This file was automatically generated from custom-nodes.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleCustomNodes09

import ai.koog.agents.core.dsl.builder.strategy

val strategy = strategy<String, String>("strategy_name") {

val summarizeTextNode by node<String, String>("node_name") { input ->
    llm.writeSession {
        updatePrompt {
            user("Please summarize the following text: $input")
        }

        val response = requestLLMWithoutTools()
        response.content
    }
}
}
