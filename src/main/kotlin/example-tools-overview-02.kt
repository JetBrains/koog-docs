// This file was automatically generated from tools-overview.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleToolsOverview02

import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.ext.tool.AskUser
import ai.koog.agents.ext.tool.SayToUser

typealias FirstSampleTool = AskUser
typealias SecondSampleTool = SayToUser

val firstToolRegistry = ToolRegistry {
    tool(FirstSampleTool)
}

val secondToolRegistry = ToolRegistry {
    tool(SecondSampleTool)
}

val newRegistry = firstToolRegistry + secondToolRegistry
