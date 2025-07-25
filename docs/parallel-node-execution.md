## Overview

Parallel node execution lets you run multiple AI agent nodes concurrently, improving performance and enabling complex workflows. This feature is particularly useful when you need to:

- Process the same input through different models or approaches simultaneously
- Perform multiple independent operations in parallel
- Implement competitive evaluation patterns where multiple solutions are generated and then compared

## Key components

Parallel node execution in Koog consists of the methods and data structures described below.

### Methods

- `parallel()`: executes multiple nodes in parallel and collects their results.

### Data structures

- `ParallelResult`: represents the completed result of a parallel node execution.
- `NodeExecutionResult`: contains the output and context of a node execution.

## Basic usage

### Running nodes in parallel

To initiate parallel execution of nodes, use the `parallel` method in the following format:

```kotlin
val nodeName by parallel<Input, Output>(
   firstNode, secondNode, thirdNode /* Add more nodes if needed */
) {
   // Merge strategy goes here
   // For example: selectByMax { it.length }
}
```

Here is an actual example of running three nodes in parallel and selecting the result with the maximum length:

```kotlin
val calc by parallel<String, Int>(
   nodeCalcTokens, nodeCalcSymbols, nodeCalcWords,
) {
   selectByMax { it }
}
```

The code above runs the `nodeCalcTokens`, `nodeCalcSymbols`, and `nodeCalcWords` nodes in parallel and returns the result with the maximum value.

### Merge strategies

After executing nodes in parallel, you need to specify how to merge the results. Koog provides the following merge
strategies:

- `selectBy()`: selects a result based on a predicate function.
- `selectByMax()`: selects the result with the maximum value based on a comparison function.
- `selectByIndex()`: selects a result based on an index returned by a selection function.
- `fold()`: folds the results into a single value using an operation function.

#### selectBy

Selects a result based on a predicate function:

```kotlin
val nodeSelectJoke by parallel<String, String>(
   nodeOpenAI, nodeAnthropicSonnet, nodeAnthropicOpus,
) {
   selectBy { it.contains("programmer") }
}
```

This selects the first joke that contains the word "programmer".

#### selectByMax

Selects the result with the maximum value based on a comparison function:

```kotlin
val nodeLongestJoke by parallel<String, String>(
   nodeOpenAI, nodeAnthropicSonnet, nodeAnthropicOpus,
) {
   selectByMax { it.length }
}
```

This selects the joke with the maximum length.

#### selectByIndex

Selects a result based on an index returned by a selection function:

```kotlin
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
```

This uses another LLM call to determine the index of the best joke.

#### fold

Folds the results into a single value using an operation function:

```kotlin
val nodeAllJokes by parallel<String, String>(
   nodeOpenAI, nodeAnthropicSonnet, nodeAnthropicOpus,
) {
   fold("Jokes:\n") { result, joke -> "$result\n$joke" }
}
```

This combines all jokes into a single string.

## Example: Best joke agent

Here is a complete example that uses parallel execution to generate jokes from different LLM models and select the best one:

```kotlin
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

                val response = requestLLMStructured(JsonStructuredData.createJsonStructure<JokeWinner>())
                val bestJoke = response.getOrNull()!!.structure
                bestJoke.index
            }
        }
    }

    // Connect the nodes
    nodeStart then nodeGenerateBestJoke then nodeFinish
}
```

## Best practices

1. **Consider resource constraints**: Be mindful of resource usage when executing nodes in parallel, especially when making multiple LLM API calls simultaneously.

2. **Context management**: Each parallel execution creates a forked context. When merging results, choose which context to preserve or how to combine contexts from different executions.

3. **Optimize for your use case**:
    - For competitive evaluation (like the joke example), use `selectByIndex` to select the best result
    - For finding the maximum value, use `selectByMax`
    - For filtering based on a condition, use `selectBy`
    - For aggregation, use `fold` to combine all results into a composite output

## Performance considerations

Parallel execution can significantly improve throughput, but it comes with some overhead:

- Each parallel node creates a new coroutine
- Context forking and merging add some computational cost
- Resource contention may occur with many parallel executions

For optimal performance, parallelize operations that:

- Are independent of each other
- Have significant execution time
- Don't share mutable state