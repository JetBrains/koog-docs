// This file was automatically generated from opentelemetry-support.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleOpentelemetrySupport02

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
    // Set your service configuration
    setServiceInfo("my-agent-service", "1.0.0")
    
    // Add the Logging exporter
    addSpanExporter(LoggingSpanExporter.create())
}
}
