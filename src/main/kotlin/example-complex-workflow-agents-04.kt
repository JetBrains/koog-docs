// This file was automatically generated from complex-workflow-agents.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleComplexWorkflowAgents04

import ai.koog.agents.core.dsl.builder.strategy
class InputType

class OutputType

val transformedOutput = OutputType()
val strategy = strategy<InputType, OutputType>("Simple calculator") {

val processNode by node<InputType, OutputType> { input ->
    // Process the input and return an output
    // You can use llm.writeSession to interact with the LLM
    // You can call tools using callTool, callToolRaw, etc.
    transformedOutput
}
}
