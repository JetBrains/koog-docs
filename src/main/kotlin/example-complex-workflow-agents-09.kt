// This file was automatically generated from complex-workflow-agents.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleComplexWorkflowAgents09

import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import ai.koog.agents.core.tools.reflect.tools

// Implement a simple calculator tool that can add two numbers
@LLMDescription("Tools for performing basic arithmetic operations")
class CalculatorTools : ToolSet {
    @Tool
    @LLMDescription("Add two numbers together and return their sum")
    fun add(
        @LLMDescription("First number to add (integer value)")
        num1: Int,

        @LLMDescription("Second number to add (integer value)")
        num2: Int
    ): String {
        val sum = num1 + num2
        return "The sum of $num1 and $num2 is: $sum"
    }
}

// Add the tool to the tool registry
val toolRegistry = ToolRegistry {
    tools(CalculatorTools().asTools())
}
