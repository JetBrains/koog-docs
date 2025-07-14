// This file was automatically generated from complex-workflow-agents.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleComplexWorkflowAgents03

import ai.koog.agents.example.exampleComplexWorkflowAgents02.anthropicClient
import ai.koog.agents.example.exampleComplexWorkflowAgents02.googleClient
import ai.koog.agents.example.exampleComplexWorkflowAgents02.openAIClient
import ai.koog.prompt.executor.llms.all.DefaultMultiLLMPromptExecutor

val multiExecutor = DefaultMultiLLMPromptExecutor(openAIClient, anthropicClient, googleClient)
