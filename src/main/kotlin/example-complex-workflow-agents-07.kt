// This file was automatically generated from complex-workflow-agents.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleComplexWorkflowAgents07

import ai.koog.agents.core.agent.config.AIAgentConfig

val agentConfig = AIAgentConfig.withSystemPrompt(
    prompt = """
        You are a simple calculator assistant.
        You can add two numbers together using the calculator tool.
        When the user provides input, extract the numbers they want to add.
        The input might be in various formats like "add 5 and 7", "5 + 7", or just "5 7".
        Extract the two numbers and use the calculator tool to add them.
        Always respond with a clear, friendly message showing the calculation and result.
        """.trimIndent()
)
