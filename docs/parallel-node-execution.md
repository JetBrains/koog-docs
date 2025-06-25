## Overview

Parallel node execution lets you run multiple AI agent nodes concurrently, improving performance and enabling complex workflows. This feature is particularly useful when you need to:

- Process the same input through different models or approaches simultaneously
- Perform multiple independent operations on the same data
- Implement competitive evaluation patterns where multiple solutions are generated and then compared

## Key components

Parallel node execution in Koog consists of the methods and data structures described below. 

### Methods

- `parallel()`: executes multiple nodes in parallel and collects their results.
- `transform()`: applies a transformation function to the outputs of parallel executions.
- `merge()`: combines the results from parallel executions into a single output.

### Data structures

- `AsyncParallelResult`: represents the asynchronous result of a parallel node execution.
- `ParallelResult`: represents the completed result of a parallel node execution.
- `NodeExecutionResult`: contains the output and context of a node execution.

## Basic usage

### Running nodes in parallel

To initiate parallel execution of nodes, use the `parallel` method in the following format:

```kotlin
val nodeName by parallel<Input, Output>(
   firstNode, secondNode, thirdNode /* Add more nodes if needed */
)
```

Here is an actual example of running three nodes in parallel:

```kotlin
val calc by parallel<String, Int>(
   nodeCalcTokens, nodeCalcSymbols, nodeCalcWords,
)
```

The code above runs the `nodeCalcTokens`, `nodeCalcSymbols`, and `nodeCalcWords` nodes in parallel and returns the
results as an instance of `AsyncParallelResult`.

Here is an example of how to use parallel node execution in your agent strategy and how to process the results:

```kotlin
val strategy = strategy("my-strategy") {
    // Define individual nodes
    val nodeCalcTokens by node<String, Int> { input -> /* processing logic */ }
    val nodeCalcSymbols by node<String, Int> { input -> /* processing logic */ }
    val nodeCalcWords by node<String, Int> { input -> /* processing logic */ }
    
    // Execute nodes in parallel
    val calc by parallel<String, Int>(
       nodeCalcTokens, nodeCalcSymbols, nodeCalcWords,
    )
    
   // Transform the output of parallel execution to a different format
    val process by transform<String, Int, String> { prevOutput -> /* processing Int output to String */ }
    
   // Merge the results and context of all parallel nodes
    val aggregate by merge<String, String>() { results -> /* aggregation logic to select or combine the result and context */ }
   
    nodeStart then calc then process then aggregate then nodeFinish
}
```

The example above also includes `transform` and `merge` methods. For more information about their use, see [Transforming parallel results](#transforming-parallel-results) and [Merging parallel results](#merging-parallel-results).

### Transforming parallel results

The `transform` method applies a transformation function to the output of parallel node executions. The `transform`
method has the following general format:

```kotlin
val nodeName by transform<Input, prevOutput, newOutput> { prevOutput -> /* processing prevOutput to newOutput */ }
```

Here is an example of defining a transformation node with the `transform` method with actual input and output values:

```kotlin
val process by transform<String, Int, String> { prevOutput -> /* processing Int to String */ }
```

The transformation will also run asynchronously and return the results as `AsyncParallelResult`.

`AsyncParallelResult` awaits for the asynchronous execution of a parallel node and converts it into a `ParallelResult`.

#### ParallelResult
A `ParallelResult` instance contains the following information:

| Name       | Data type                   | Description                            |
|------------|-----------------------------|----------------------------------------|
| `nodeName` | String                      | The name of the node                   |
| `input`    | Input                       | The input to the node                  |
| `result`   | NodeExecutionResult<Output> | The output and the context of the node |


### Merging parallel results

To merge the results of multiple nodes executed in parallel, use the `merge` method that takes the following general
format:

```kotlin
val mergeNodeName by merge<Input, Output> { results -> /* the logic to select or combine the output and context */ }
```

Here is what the use of the `merge` method would look like when used with actual `Input` and `Ouput` values:

```kotlin
val aggregate by merge<String, String>() { results -> /* the logic to select or combine the output and context */ }
```

The merge node will wait for all parallel executions to complete and return the combined results as `ParallelResult`.
For more information, see [ParallelResult](#parallelresult).

## Example: Best joke agent

Here is a complete example that uses parallel execution to generate jokes from different LLM models and select the best one. The example presents both parallel node execution and processing of the outputs (transformation and merging):

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
            // Add the prompt 
            val response = requestLLMWithoutTools()
            response.content
        }
    }

    val nodeAnthropicOpus by node<String, String> { topic ->
        // Define the mode, add prompt, and handle the response 
    }

    // Execute joke generation in parallel
    val nodeGenerateJokes by parallel<String, String>(
        nodeOpenAI, nodeAnthropicSonnet, nodeAnthropicOpus
    )

    // Optional: transform the jokes
    val nodeTransformJoke by transform<String, String, String> { joke ->
        "My favorite joke: $joke"
    }

    // Merge and select the best joke
    val nodeSelectBestJoke by merge<String, String>() { results ->
        val jokes = results.map { it.result.output }
        val contexts = results.map { it.result.context }
        
        // Use another LLM call to select the best joke
        val bestJokeIndex = determineBestJokeIndex(jokes)
        
        // Return the selected context and joke
        contexts[bestJokeIndex] to jokes[bestJokeIndex]
    }

    // Connect the nodes
    nodeStart then nodeGenerateJokes then nodeTransformJoke then nodeSelectBestJoke then nodeFinish
}
```

## Best practices

1. **Consider resource constraints**: Be mindful of resource usage when executing nodes in parallel, especially when making multiple LLM API calls simultaneously.

2. **Context management**: Each parallel execution creates a forked context. When merging results, choose which context to preserve or how to combine contexts from different executions.

3. **Optimize for your use case**:
    - For competitive evaluation (like the joke example), use the merge node to select the best result
    - For aggregation, combine all results into a composite output
    - For validation, use parallel execution to cross-check results from different approaches

## Performance considerations

Parallel execution can significantly improve throughput, but it comes with some overhead:

- Each parallel node creates a new coroutine
- Context forking and merging add some computational cost
- Resource contention may occur with many parallel executions

For optimal performance, parallelize operations that:

- Are independent of each other
- Have significant execution time
- Don't share mutable state