// This file was automatically generated from parallel-node-execution.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleParallelNodeExecution07

import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.anthropic.AnthropicModels
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.structure.json.JsonStructuredData

typealias Input = String
typealias Output = String

data class JokeRating(
   val bestJokeIndex: Int,
)

val strategy = strategy("best-joke") {
   // Define nodes for different LLM models
   val nodeOpenAI by node<String, String> { topic ->
      llm.writeSession {
         model = OpenAIModels.Chat.GPT4o
         updatePrompt {
            system("You are a comedian. Generate a funny joke about the given topic.")
            user("Tell me a joke about $topic.")
         }
         val response = requestLLMWithoutTools()
         response.content
      }
   }

   val nodeAnthropicSonnet by node<String, String> { topic ->
      llm.writeSession {
         model = AnthropicModels.Sonnet_3_5
         updatePrompt {
            system("You are a comedian. Generate a funny joke about the given topic.")
            user("Tell me a joke about $topic.")
         }
         val response = requestLLMWithoutTools()
         response.content
      }
   }

   val nodeAnthropicOpus by node<String, String> { topic ->
      llm.writeSession {
         model = AnthropicModels.Opus_3
         updatePrompt {
            system("You are a comedian. Generate a funny joke about the given topic.")
            user("Tell me a joke about $topic.")
         }
         val response = requestLLMWithoutTools()
         response.content
      }
   }

   // Execute joke generation in parallel and select the best joke
   val nodeGenerateBestJoke by parallel(
      nodeOpenAI, nodeAnthropicSonnet, nodeAnthropicOpus,
   ) {
      selectByIndex { jokes ->
         // Another LLM (e.g., GPT4o) would find the funniest joke:
         llm.writeSession {
            model = OpenAIModels.Chat.GPT4o
            updatePrompt {
               prompt("best-joke-selector") {
                  system("You are a comedy critic. Give a critique for the given joke.")
                  user(
                     """
                            Here are three jokes about the same topic:

                            ${jokes.mapIndexed { index, joke -> "Joke $index:\n$joke" }.joinToString("\n\n")}

                            Select the best joke and explain why it's the best.
                            """.trimIndent()
                  )
               }
            }

            val response = requestLLMStructured(JsonStructuredData.createJsonStructure<JokeRating>())
            val bestJoke = response.getOrNull()!!.structure
            bestJoke.bestJokeIndex
         }
      }
   }

   // Connect the nodes
   nodeStart then nodeGenerateBestJoke then nodeFinish
}
