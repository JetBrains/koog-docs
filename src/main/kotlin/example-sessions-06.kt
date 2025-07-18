// This file was automatically generated from sessions.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleSessions06

import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.example.exampleParallelNodeExecution07.JokeRating
import ai.koog.prompt.structure.json.JsonStructuredData

val myStructure = JsonStructuredData.createJsonStructure<JokeRating>()

val strategy = strategy<Unit, Unit>("strategy-name") {
    val node by node<Unit, Unit> {

llm.writeSession {
    // Make a structured request
    val structuredResponse = requestLLMStructured(myStructure)

    // Make a streaming request
    val responseStream = requestLLMStreaming()
    responseStream.collect { chunk ->
        // Process each chunk as it arrives
    }
}
   }
}
