// This file was automatically generated from streaming-api.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleStreamingApi01

import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.prompt.structure.markdown.MarkdownStructuredDataDefinition

val strategy = strategy<String, String>("strategy_name") {
    val node by node<Unit, Unit> {

fun markdownBookDefinition(): MarkdownStructuredDataDefinition {
    return MarkdownStructuredDataDefinition("name", schema = { /*...*/ })
}

val mdDefinition = markdownBookDefinition()

llm.writeSession {
    val stream = requestLLMStreaming(mdDefinition)
    // Access the raw string chunks directly
    stream.collect { chunk ->
        // Process each chunk of text as it arrives
        println("Received chunk: $chunk") // The chunks together will be structured as a text following the mdDefinition schema
    }
}
   }
}
