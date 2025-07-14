// This file was automatically generated from advanced-tool-implementation.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleAdvancedToolImplementation01

import ai.koog.agents.core.tools.Tool
import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.agents.core.tools.ToolParameterDescriptor
import ai.koog.agents.core.tools.ToolParameterType
import ai.koog.agents.core.tools.ToolResult
import kotlinx.serialization.Serializable

// Implement a simple calculator tool that adds two digits
object CalculatorTool : Tool<CalculatorTool.Args, ToolResult.Number>() {
    
    // Arguments for the calculator tool
    @Serializable
    data class Args(
        val digit1: Int,
        val digit2: Int
    ) : Tool.Args {
        init {
            require(digit1 in 0..9) { "digit1 must be a single digit (0-9)" }
            require(digit2 in 0..9) { "digit2 must be a single digit (0-9)" }
        }
    }

    // Serializer for the Args class
    override val argsSerializer = Args.serializer()

    // Tool descriptor
    override val descriptor: ToolDescriptor = ToolDescriptor(
        name = "calculator",
        description = "A simple calculator that can add two digits (0-9).",
        requiredParameters = listOf(
            ToolParameterDescriptor(
                name = "digit1",
                description = "The first digit to add (0-9)",
                type = ToolParameterType.Integer
            ),
            ToolParameterDescriptor(
                name = "digit2",
                description = "The second digit to add (0-9)",
                type = ToolParameterType.Integer
            )
        )
    )

    // Function to add two digits
    override suspend fun execute(args: Args): ToolResult.Number {
        val sum = args.digit1 + args.digit2
        return ToolResult.Number(sum)
    }
}
