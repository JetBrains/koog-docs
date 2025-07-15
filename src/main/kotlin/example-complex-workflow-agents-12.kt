// This file was automatically generated from complex-workflow-agents.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleComplexWorkflowAgents12

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.*
import ai.koog.agents.core.feature.handler.AgentFinishedContext
import ai.koog.agents.core.feature.handler.AgentStartContext
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import ai.koog.agents.core.tools.reflect.tools
import ai.koog.agents.features.eventHandler.feature.EventHandler
import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import kotlinx.coroutines.runBlocking

// Use the OpenAI executor with an API key from an environment variable
val promptExecutor = simpleOpenAIExecutor(System.getenv("OPENAI_API_KEY"))

// Create a simple strategy
val agentStrategy = strategy("Simple calculator") {
    // Define nodes for the strategy
    val nodeSendInput by nodeLLMRequest()
    val nodeExecuteTool by nodeExecuteTool()
    val nodeSendToolResult by nodeLLMSendToolResult()

    // Define edges between nodes
    // Start -> Send input
    edge(nodeStart forwardTo nodeSendInput)

    // Send input -> Finish
    edge(
        (nodeSendInput forwardTo nodeFinish)
                transformed { it }
                onAssistantMessage { true }
    )

    // Send input -> Execute tool
    edge(
        (nodeSendInput forwardTo nodeExecuteTool)
                onToolCall { true }
    )

    // Execute tool -> Send the tool result
    edge(nodeExecuteTool forwardTo nodeSendToolResult)

    // Send the tool result -> finish
    edge(
        (nodeSendToolResult forwardTo nodeFinish)
                transformed { it }
                onAssistantMessage { true }
    )
}

// Configure the agent
val agentConfig = AIAgentConfig(
    prompt = Prompt.build("simple-calculator") {
        system(
            """
                You are a simple calculator assistant.
                You can add two numbers together using the calculator tool.
                When the user provides input, extract the numbers they want to add.
                The input might be in various formats like "add 5 and 7", "5 + 7", or just "5 7".
                Extract the two numbers and use the calculator tool to add them.
                Always respond with a clear, friendly message showing the calculation and result.
                """.trimIndent()
        )
    },
    model = OpenAIModels.Chat.GPT4o,
    maxAgentIterations = 10
)

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
    tools(CalculatorTools())
}

// Create the agent
val agent = AIAgent(
    promptExecutor = promptExecutor,
    toolRegistry = toolRegistry,
    strategy = agentStrategy,
    agentConfig = agentConfig,
    installFeatures = {
        install(EventHandler) {
            onBeforeAgentStarted { eventContext: AgentStartContext<*> ->
                println("Starting strategy: ${eventContext.strategy.name}")
            }
            onAgentFinished { eventContext: AgentFinishedContext ->
                println("Result: ${eventContext.result}")
            }
        }
    }
)

fun main() {
    runBlocking {
        println("Enter two numbers to add (e.g., 'add 5 and 7' or '5 + 7'):")

        // Read the user input and send it to the agent
        val userInput = readlnOrNull() ?: ""
        val agentResult = agent.run(userInput)
        println("The agent returned: $agentResult")
    }
}
