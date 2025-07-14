// This file was automatically generated from agent-memory.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleAgentMemory10

import ai.koog.agents.memory.storage.EncryptedStorage
import ai.koog.rag.base.files.JVMFileSystemProvider
import ai.koog.agents.memory.storage.Aes256GCMEncryptor

// Simple encrypted storage setup
val secureStorage = EncryptedStorage(
    fs = JVMFileSystemProvider.ReadWrite,
    encryption = Aes256GCMEncryptor("your-secret-key")
)
