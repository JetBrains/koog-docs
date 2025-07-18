// This file was automatically generated from opentelemetry-support.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleOpentelemetrySupport05

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.features.opentelemetry.feature.OpenTelemetry
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import io.opentelemetry.exporter.logging.LoggingSpanExporter

const val apiKey = ""

val agent = AIAgent(
    executor = simpleOpenAIExecutor(apiKey),
    llmModel = OpenAIModels.Chat.GPT4o,
    systemPrompt = "You are a helpful assistant."
) {

install(OpenTelemetry) {
    // Add the logging exporter
    addSpanExporter(LoggingSpanExporter.create())
    // Add more exporters as needed
}
}
