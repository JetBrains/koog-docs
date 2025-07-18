// This file was automatically generated from parallel-node-execution.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleParallelNodeExecution05

import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.structure.json.JsonStructuredData

typealias Input = String
typealias Output = String

data class JokeRating(
   val bestJokeIndex: Int,
)

val strategy = strategy<String, String>("strategy_name") {
   val nodeOpenAI by node<Input, Output>() { "openai" }
   val nodeAnthropicSonnet by node<Input, Output>() { "sonnet" }
   val nodeAnthropicOpus by node<Input, Output>() { "opus" }

val nodeBestJoke by parallel<String, String>(
   nodeOpenAI, nodeAnthropicSonnet, nodeAnthropicOpus,
) {
   selectByIndex { jokes ->
      // Use another LLM to determine the best joke
      llm.writeSession {
         model = OpenAIModels.Chat.GPT4o
         updatePrompt {
            system("You are a comedy critic. Select the best joke.")
            user("Here are three jokes: ${jokes.joinToString("\n\n")}")
         }
         val response = requestLLMStructured(JsonStructuredData.createJsonStructure<JokeRating>())
         response.getOrNull()!!.structure.bestJokeIndex
      }
   }
}
}
