// This file was automatically generated from ranked-document-storage.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleRankedDocumentStorage10

import ai.koog.agents.example.exampleRankedDocumentStorage03.documentEmbedder
import ai.koog.rag.vector.InMemoryDocumentEmbeddingStorage
import java.nio.file.Path

typealias Document = Path

val inMemoryEmbeddingStorage = InMemoryDocumentEmbeddingStorage<Document>(
    embedder = documentEmbedder
)

