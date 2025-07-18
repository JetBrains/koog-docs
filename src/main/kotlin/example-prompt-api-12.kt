// This file was automatically generated from prompt-api.md by Knit tool. Do not edit.
package ai.koog.agents.example.examplePromptApi12

import ai.koog.agents.example.examplePromptApi11.anthropicClient
import ai.koog.agents.example.examplePromptApi11.googleClient
import ai.koog.agents.example.examplePromptApi11.openAIClient
import ai.koog.prompt.executor.llms.all.DefaultMultiLLMPromptExecutor

val multiExecutor = DefaultMultiLLMPromptExecutor(openAIClient, anthropicClient, googleClient)
