// This file was automatically generated from history-compression.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleHistoryCompression14

import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.replaceHistoryWithTLDR
import ai.koog.agents.example.exampleHistoryCompression12.MyCustomCompressionStrategy

typealias ProcessedInput = String

val strategy = strategy<String, String>("strategy_name") {
val node by node<Unit, Unit> {

llm.writeSession {
    replaceHistoryWithTLDR(strategy = MyCustomCompressionStrategy())
}
    }
}
