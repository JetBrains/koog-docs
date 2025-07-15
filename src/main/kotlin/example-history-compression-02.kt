// This file was automatically generated from history-compression.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleHistoryCompression02

import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.nodeLLMCompressHistory

val strategy = strategy<String, String>("execute-with-history-compression") {
    val collectInformation by subgraph<String, String> {
        // Some steps to collect the information
    }
    val compressHistory by nodeLLMCompressHistory<String>()
    val makeTheDecision by subgraph<String, String> {
        // Some steps to make the decision based on the current compressed history and collected information
    }
    
    nodeStart then collectInformation then compressHistory then makeTheDecision
}
