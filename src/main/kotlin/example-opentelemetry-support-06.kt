// This file was automatically generated from opentelemetry-support.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleOpentelemetrySupport06

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.features.opentelemetry.feature.OpenTelemetry
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter
import java.util.concurrent.TimeUnit

const val apiKey = ""
const val AUTH_STRING = ""


val agent = AIAgent(
    executor = simpleOpenAIExecutor(apiKey),
    llmModel = OpenAIModels.Chat.GPT4o,
    systemPrompt = "You are a helpful assistant."
) {

install(OpenTelemetry) {
   // Add OpenTelemetry HTTP exporter 
   addSpanExporter(
      OtlpHttpSpanExporter.builder()
         // Set the maximum time to wait for the collector to process an exported batch of spans 
         .setTimeout(30, TimeUnit.SECONDS)
         // Set the OpenTelemetry endpoint to connect to
         .setEndpoint("http://localhost:3000/api/public/otel/v1/traces")
         // Add the authorization header
         .addHeader("Authorization", "Basic $AUTH_STRING")
         .build()
   )
}
}
