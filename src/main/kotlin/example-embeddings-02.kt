// This file was automatically generated from embeddings.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleEmbeddings02

import ai.koog.embeddings.local.LLMEmbedder
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels

suspend fun openAIEmbed(text: String) {
    // Get the OpenAI API token from the OPENAI_KEY environment variable
    val token = System.getenv("OPENAI_KEY") ?: error("Environment variable OPENAI_KEY is not set")
    // Create an OpenAILLMClient instance
    val client = OpenAILLMClient(token)
    // Create an embedder
    val embedder = LLMEmbedder(client, OpenAIModels.Embeddings.TextEmbeddingAda002)
    // Create embeddings
    val embedding = embedder.embed(text)
    // Print embeddings to the output
    println(embedding)
}
