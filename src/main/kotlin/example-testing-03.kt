// This file was automatically generated from testing.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleTesting03

import ai.koog.agents.core.tools.*
import ai.koog.agents.ext.tool.AskUser
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.agents.testing.tools.getMockExecutor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

public object CreateTool : Tool<CreateTool.Args, CreateTool.Result>() {
/**
* Represents the arguments for the [AskUser] tool
*
* @property message The message to be used as an argument for the tool's execution.
*/
@Serializable
public data class Args(val message: String) : ToolArgs

    @Serializable
    public data class Result(val message: String) : ToolResult {
        override fun toStringDefault() = message
    }

    override val argsSerializer: KSerializer<Args> = Args.serializer()

    override val descriptor: ToolDescriptor = ToolDescriptor(
        name = "message",
        description = "Service tool, used by the agent to talk with user",
        requiredParameters = listOf(
            ToolParameterDescriptor(
                name = "message", description = "Message from the agent", type = ToolParameterType.String
            )
        )
    )

    override suspend fun execute(args: Args): Result {
        return Result(args.message)
    }
}

public object SearchTool : Tool<SearchTool.Args, SearchTool.Result>() {
/**
* Represents the arguments for the [AskUser] tool
*
* @property message The message to be used as an argument for the tool's execution.
*/
@Serializable
public data class Args(val query: String) : ToolArgs

    @Serializable
    public data class Result(val message: String) : ToolResult {
        override fun toStringDefault() = message
    }

    override val argsSerializer: KSerializer<Args> = Args.serializer()

    override val descriptor: ToolDescriptor = ToolDescriptor(
        name = "message",
        description = "Service tool, used by the agent to talk with user",
        requiredParameters = listOf(
            ToolParameterDescriptor(
                name = "message", description = "Message from the agent", type = ToolParameterType.String
            )
        )
    )

    override suspend fun execute(args: Args): Result {
        return Result(args.query)
    }
}


public object AnalyzeTool : Tool<AnalyzeTool.Args, AnalyzeTool.Result>() {
/**
* Represents the arguments for the [AskUser] tool
*
* @property message The message to be used as an argument for the tool's execution.
*/
@Serializable
public data class Args(val message: String) : ToolArgs

    @Serializable
    public data class Result(val message: String) : ToolResult {
        override fun toStringDefault() = message
    }

    override val argsSerializer: KSerializer<Args> = Args.serializer()

    override val descriptor: ToolDescriptor = ToolDescriptor(
        name = "message",
        description = "Service tool, used by the agent to talk with user",
        requiredParameters = listOf(
            ToolParameterDescriptor(
                name = "message", description = "Message from the agent", type = ToolParameterType.String
            )
        )
    )

    override suspend fun execute(args: Args): Result {
        return Result(args.message)
    }
}

typealias PositiveToneTool = SayToUser
typealias NegativeToneTool = SayToUser

val mockLLMApi = getMockExecutor {

// Mock a tool call response
mockLLMToolCall(CreateTool, CreateTool.Args("solve")) onRequestEquals "Solve task"

// Mock tool behavior - simplest form without lambda
mockTool(PositiveToneTool) alwaysReturns "The text has a positive tone."

// Using lambda when you need to perform extra actions
mockTool(NegativeToneTool) alwaysTells {
  // Perform some extra action
  println("Negative tone tool called")

  // Return the result
  "The text has a negative tone."
}

// Mock tool behavior based on specific arguments
mockTool(AnalyzeTool) returns AnalyzeTool.Result("Detailed analysis") onArguments AnalyzeTool.Args("analyze deeply")

// Mock tool behavior with conditional argument matching
mockTool(SearchTool) returns SearchTool.Result("Found results") onArgumentsMatching { args ->
  args.query.contains("important")
}
}
