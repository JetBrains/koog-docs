# Tool declaration

Tools are functions that an agent can use to perform specific tasks or access external systems. There are built-in tools and custom tools that can be used with AI agents.

The process for enabling tools is the same for all agent types:

1. Add the tool to a tool registry. For details, see [Tool registry](#tool-registry)
2. Pass the tool registry to the agent. For details, see [Passing tools to an agent](#passing-tools-to-an-agent)

This page explains how to implement a tool and use it in the agent. To learn more about built-in tools, see [Available tools](simple-api-available-tools.md).

## Getting started

To start using tool annotations in your project, you need to understand the following key annotations:

| Annotation        | Description                                                             |
|-------------------|-------------------------------------------------------------------------|
| `@Tool`           | Marks methods that should be exposed as tools to LLMs.                  |
| `@LLMDescription` | Provides descriptive information about your tools and their components. |


## @Tool annotation

### Purpose

The `@Tool` annotation is used to mark methods that should be exposed as tools to LLMs. Methods annotated with `@Tool` are collected by reflection from objects implementing the `ToolSet` interface.

### Definition

```kotlin
@Target(AnnotationTarget.FUNCTION)
public annotation class Tool(val customName: String = "")
```

### Parameters

| Name         | Required | Description                                                                              |
|--------------|----------|------------------------------------------------------------------------------------------|
| `customName` | No       | Specifies a custom name for the tool. If not provided, the name of the function is used. |

### Usage

Apply the `@Tool` annotation to methods in a class that implements the `ToolSet` interface:

```kotlin
class MyToolSet : ToolSet {
    @Tool
    fun myTool(): String {
        // Tool implementation
        return "Result"
    }

    @Tool(customName = "customToolName")
    fun anotherTool(): String {
        // Tool implementation
        return "Result"
    }
}
```

## @LLMDescription annotation

### Purpose

The `@LLMDescription` annotation provides descriptive information about code elements (classes, methods, parameters, etc.) to LLMs. This helps LLMs understand the purpose and usage of these elements.

### Definition

```kotlin
@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.TYPE,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION
)
public annotation class LLMDescription(val description: String)
```

### Parameters

| Name          | Required | Description                                    |
|---------------|----------|------------------------------------------------|
| `description` | Yes      | A string that describes the annotated element. |


### Usage

The `@LLMDescription` annotation can be applied at various levels:

#### Method level

```kotlin
@Tool
@LLMDescription("Performs a specific operation and returns the result")
fun myTool(): String {
    // Method implementation
    return "Result"
}
```

#### Parameter level

```kotlin
@Tool
@LLMDescription("Processes input data")
fun processTool(
    @LLMDescription("The input data to process")
    input: String,

    @LLMDescription("Optional configuration parameters")
    config: String = ""
): String {
    // Method implementation
    return "Processed: $input with config: $config"
}
```

## Tool creation

### 1. Create a ToolSet class

Create a class that implements the `ToolSet` interface. This interface marks your class as a container for tools.

```kotlin
class MyFirstToolSet : ToolSet {
    // Tools will go here
}
```

### 2. Add tool methods

Add methods to your class and annotate them with `@Tool` to expose them as tools:

```kotlin
class MyFirstToolSet : ToolSet {
    @Tool
    fun getWeather(location: String): String {
        // In a real implementation, you would call a weather API
        return "The weather in $location is sunny and 72°F"
    }
}
```

### 3. Add descriptions

Add `@LLMDescription` annotations to provide context for the LLM:

```kotlin
@LLMDescription("Tools for getting weather information")
class MyFirstToolSet : ToolSet {
    @Tool
    @LLMDescription("Get the current weather for a location")
    fun getWeather(
        @LLMDescription("The city and state/country")
        location: String
    ): String {
        // In a real implementation, you would call a weather API
        return "The weather in $location is sunny and 72°F"
    }
}
```

### 4. Use your tools with an agent

Now you can use your tools with an agent:

```kotlin
fun main() = runBlocking {
    // Create your tool set
    val weatherTools = MyFirstToolSet()

    // Create an agent with your tools

    val agent = AIAgent(
        executor = simpleOpenAIExecutor(TokenService.openAIToken),
        llmModel = OpenAIModels.Reasoning.GPT4oMini,
        systemPrompt = "Provide weather information for a given location.",
        toolRegistry = ToolRegistry {
            tools(weatherTools.asTools())
        }
    )

    // The agent can now use your weather tools
    agent.run("What's the weather like in New York?")
}
```

## Usage examples

Here are some real-world examples of tool annotations in action.

### Basic example: Switch controller

This example shows a simple tool set for controlling a switch:

```kotlin
@LLMDescription("Tools for controlling a switch")
class SwitchTools(val switch: Switch) : ToolSet {
    @Tool
    @LLMDescription("Switches the state of the switch")
    fun switch(
        @LLMDescription("The state to set (true for on, false for off)")
        state: Boolean
    ): String {
        switch.switch(state)
        return "Switched to ${if (state) "on" else "off"}"
    }

    @Tool
    @LLMDescription("Returns the current state of the switch")
    fun switchState(): String {
        return "Switch is ${if (switch.isOn()) "on" else "off"}"
    }
}
```

When an LLM needs to control a switch, it can understand the following information from the provided description:

- The purpose and functionality of the tools.
- The required parameters for using the tools.
- The acceptable values for each parameter.
- The expected return values upon execution.

### Advanced example: Diagnostic tools

This example shows a more complex tool set for device diagnostics:

```kotlin
@LLMDescription("Tools for performing diagnostics and troubleshooting on devices")
class DiagnosticToolSet : ToolSet {
    @Tool
    @LLMDescription("Run diagnostic on a device to check its status and identify any issues")
    fun runDiagnostic(
        @LLMDescription("The ID of the device to diagnose")
        deviceId: String,

        @LLMDescription("Additional information for the diagnostic (optional)")
        additionalInfo: String = ""
    ): String {
        // Implementation
        return "Diagnostic results for device $deviceId"
    }

    @Tool
    @LLMDescription("Analyze an error code to determine its meaning and possible solutions")
    fun analyzeError(
        @LLMDescription("The error code to analyze (e.g., 'E1001')")
        errorCode: String
    ): String {
        // Implementation
        return "Analysis of error code $errorCode"
    }
}
```

## Best practices

1. **Provide clear descriptions**: write clear, concise descriptions that explain the purpose and behavior of tools, parameters, and return values.

2. **Describe all parameters**: add `@LLMDescription` to all parameters to help LLMs understand what each parameter is for.

3. **Use consistent naming**: use consistent naming conventions for tools and parameters to make them more intuitive.

4. **Group related tools**: group related tools in the same `ToolSet` implementation and provide a class-level description.

5. **Return informative results**: make sure tool return values provide clear information about the result of the operation.

6. **Handle errors gracefully**: include error handling in your tools and return informative error messages.

7. **Document default values**: when parameters have default values, document this in the description.

8. **Keep Tools Focused**: Each tool should perform a specific, well-defined task rather than trying to do too many things.

## Troubleshooting common issues

When working with tool annotations, you might encounter some common issues. Here is how to resolve them.

### Tools not being recognized

**Problem**: The agent does not recognize your tools.

**Solutions**:

- Ensure your class implements the `ToolSet` interface.
- Verify that all tool methods are annotated with `@Tool`.
- Check that your tool methods have appropriate return types (String is recommended for simplicity).
- Make sure you're properly registering your tool set with the agent using `withTools()`.

### Unclear tool descriptions

**Problem**: The LLM does not use your tools correctly or misunderstands their purpose.

**Solutions**:

- Improve your `@LLMDescription` annotations to be more specific and clear.
- Include examples in your descriptions when appropriate.
- Specify parameter constraints in the descriptions (e.g., "Must be a positive number").
- Use consistent terminology throughout your descriptions.

### Parameter type issues

**Problem**: The LLM provides incorrect parameter types.

**Solutions**:

- Use simple parameter types when possible (String, Boolean, Int).
- Clearly describe the expected format in the parameter description.
- For complex types, consider using String parameters with a specific format and parse them in your tool.
- Include examples of valid inputs in your parameter descriptions.

### Performance issues

**Problem**: Your tools cause performance problems.

**Solutions**:

- Keep tool implementations lightweight.
- For resource-intensive operations, consider implementing asynchronous processing.
- Cache results when appropriate.
- Log tool usage to identify bottlenecks.
