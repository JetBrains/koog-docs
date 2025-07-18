// This file was automatically generated from structured-data.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleStructuredData09

import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.example.exampleStructuredData06.weatherForecastStructure
import ai.koog.prompt.executor.clients.openai.OpenAIModels

val strategy = strategy<Unit, Unit>("strategy-name") {
    val node by node<Unit, Unit> {

val structuredResponse = llm.writeSession {
    this.requestLLMStructured(
        structure = weatherForecastStructure,
        fixingModel = OpenAIModels.Chat.GPT4o,
    )
}
    }
}
