// This file was automatically generated from opentelemetry-support.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleOpentelemetrySupport08

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.features.opentelemetry.feature.OpenTelemetry
import ai.koog.agents.utils.use
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import io.opentelemetry.exporter.logging.LoggingSpanExporter
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import kotlinx.coroutines.runBlocking

const val openAIApiKey = "open-ai-api-key"

fun main() {
    runBlocking {
        val agent = AIAgent(
            executor = simpleOpenAIExecutor(openAIApiKey),
            llmModel = OpenAIModels.Reasoning.GPT4oMini,
            systemPrompt = "You are a code assistant. Provide concise code examples."
        ) {
            install(OpenTelemetry) {
                // Add a console logger for local debugging
                addSpanExporter(LoggingSpanExporter.create())

                // Send traces to OpenTelemetry collector
                addSpanExporter(
                    OtlpGrpcSpanExporter.builder()
                        .setEndpoint("http://localhost:4317")
                        .build()
                )
            }
        }

        agent.use { agent ->
            println("Running the agent with OpenTelemetry tracing...")

            val result = agent.run("Tell me a joke about programming")

            println("Agent run completed with result: '$result'." +
                    "\nCheck Jaeger UI at http://localhost:16686 to view traces")
        }
    }
}
