// This file was automatically generated from ranked-document-storage.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleRankedDocumentStorage13

import ai.koog.agents.example.exampleRankedDocumentStorage08.embedder
import ai.koog.rag.vector.JVMTextFileDocumentEmbeddingStorage
import java.nio.file.Path

val jvmTextFileEmbeddingStorage = JVMTextFileDocumentEmbeddingStorage(
   embedder = embedder,
   root = Path.of("/path/to/storage")
)
