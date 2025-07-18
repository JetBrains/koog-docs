// This file was automatically generated from sessions.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleSessions07

import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.prompt.message.Message
import ai.koog.prompt.message.RequestMetaInfo
import kotlinx.datetime.Clock

val myToolResult = Message.Tool.Result(
    id = "",
    tool = "",
    content = "",
    metaInfo = RequestMetaInfo(Clock.System.now())
)

val strategy = strategy<Unit, Unit>("strategy-name") {
    val node by node<Unit, Unit> {

llm.writeSession {
    updatePrompt {
        // Add a system message
        system("You are a helpful assistant.")

        // Add a user message
        user("Hello, can you help me with a coding question?")

        // Add an assistant message
        assistant("Of course! What's your question?")

        // Add a tool result
        tool {
            result(myToolResult)
        }
    }
}
   }
}
