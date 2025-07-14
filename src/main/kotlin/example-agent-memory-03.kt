// This file was automatically generated from agent-memory.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleAgentMemory03

import ai.koog.agents.memory.model.MemorySubject
import kotlinx.serialization.Serializable

object MemorySubjects {
    /**
     * Information specific to the local machine environment
     * Examples: Installed tools, SDKs, OS configuration, available commands
     */
    @Serializable
    data object Machine : MemorySubject() {
        override val name: String = "machine"
        override val promptDescription: String =
            "Technical environment (installed tools, package managers, packages, SDKs, OS, etc.)"
        override val priorityLevel: Int = 1
    }

    /**
     * Information specific to the user
     * Examples: Conversation preferences, issue history, contact information
     */
    @Serializable
    data object User : MemorySubject() {
        override val name: String = "user"
        override val promptDescription: String =
            "User information (conversation preferences, issue history, contact details, etc.)"
        override val priorityLevel: Int = 1
    }
}
