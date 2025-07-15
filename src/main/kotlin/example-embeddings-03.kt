// This file was automatically generated from embeddings.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleEmbeddings03

import ai.koog.embeddings.base.Embedder

suspend fun compareCodeToText(embedder: Embedder) { // Embedder type
    // Code snippet
    val code = """
        fun factorial(n: Int): Int {
            return if (n <= 1) 1 else n * factorial(n - 1)
        }
    """.trimIndent()

    // Text descriptions
    val description1 = "A recursive function that calculates the factorial of a number"
    val description2 = "A function that sorts an array of integers"

    // Generate embeddings
    val codeEmbedding = embedder.embed(code)
    val desc1Embedding = embedder.embed(description1)
    val desc2Embedding = embedder.embed(description2)

    // Calculate differences (lower value means more similar)
    val diff1 = embedder.diff(codeEmbedding, desc1Embedding)
    val diff2 = embedder.diff(codeEmbedding, desc2Embedding)

    println("Difference between code and description 1: $diff1")
    println("Difference between code and description 2: $diff2")

    // The code should be more similar to description1 than description2
    if (diff1 < diff2) {
        println("The code is more similar to: '$description1'")
    } else {
        println("The code is more similar to: '$description2'")
    }
}
