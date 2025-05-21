# Tools Declaration

Tools are functions that an agent can use to perform specific tasks or access external systems.

There are built-in tools available in the Simple API only and custom tools that can be used with both agents created using the Simple API and more sophisticated agents
created with the AI Agent.

The process for enabling tools is the same for all agent types:

1. Add the tool to a tool registry. For details, see [Tool registry](#tool-registry)
2. Pass the tool registry to the agent. For details, see [Passing tools to an agent](#passing-tools-to-an-agent)

This page explains how to implement a tool and use it in the agent. To learn more about built-in tools, see [Available tools](simple-api-available-tools.md).

## Getting Started

To start using tool annotations in your Koog Agents project, you'll need to understand two key annotations:

1. `@Tool` - Marks methods that should be exposed as tools to LLMs
2. `@LLMDescription` - Provides descriptive information about your tools and their components

Let's explore how to use these annotations step by step.

## The @Tool Annotation

### Purpose
The `@Tool` annotation is used to mark methods that should be exposed as tools to LLMs (Large Language Models). Methods annotated with `@Tool` are collected by reflection from objects implementing the `ToolSet` interface.

### Definition
```kotlin
@Target(AnnotationTarget.FUNCTION)
public annotation class Tool(val customName: String = "")
```

### Parameters
- `customName` (optional): Specifies a custom name for the tool. If not provided, the name of the function is used.

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

## @LLMDescription Annotation

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
- `description`: A string describing the annotated element.

### Usage
The `@LLMDescription` annotation can be applied at various levels:

#### Method Level
```kotlin
@Tool
@LLMDescription("Performs a specific operation and returns the result")
fun myTool(): String {
    // Method implementation
    return "Result"
}
```

#### Parameter Level
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

## Step-by-Step Guide to Creating Tools

Let's walk through the process of creating tools for your AI agent step by step:

### Step 1: Create a ToolSet Class

First, create a class that implements the `ToolSet` interface. This interface marks your class as a container for tools.

```kotlin
class MyFirstToolSet : ToolSet {
    // Tools will go here
}
```

### Step 2: Add Tool Methods

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

### Step 3: Add Descriptions

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

### Step 4: Use Your Tools with an Agent

Now you can use your tools with a Koog Agent:

```kotlin
fun main() = runBlocking {
    // Create your tool set
    val weatherTools = MyFirstToolSet()

    // Create an agent with your tools

    val agent = simpleChatAgent(
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

## Real-World Examples

Let's look at some real-world examples of tool annotations in action:

### Basic Example: Switch Controller

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

When an LLM needs to control a switch, it can understand from the descriptions:
- What the tools do
- What parameters they need
- What values are acceptable for those parameters
- What to expect as a return value

### Advanced Example: Diagnostic Tools

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

## Best Practices

1. **Provide Clear Descriptions**: Write clear, concise descriptions that explain the purpose and behavior of tools, parameters, and return values.

2. **Describe All Parameters**: Add `@LLMDescription` to all parameters to help LLMs understand what each parameter is for.

3. **Use Consistent Naming**: Use consistent naming conventions for tools and parameters to make them more intuitive.

4. **Group Related Tools**: Group related tools in the same `ToolSet` implementation and provide a class-level description.

5. **Return Informative Results**: Make sure tool return values provide clear information about the result of the operation.

6. **Handle Errors Gracefully**: Include error handling in your tools and return informative error messages.

7. **Document Default Values**: When parameters have default values, document this in the description.

8. **Keep Tools Focused**: Each tool should perform a specific, well-defined task rather than trying to do too many things.

## Troubleshooting Common Issues

When working with tool annotations, you might encounter some common issues. Here's how to resolve them:

### Tools Not Being Recognized

**Problem**: Your tools are not being recognized by the agent.

**Solutions**:
- Ensure your class implements the `ToolSet` interface
- Verify that all tool methods are annotated with `@Tool`
- Check that your tool methods have appropriate return types (String is recommended for simplicity)
- Make sure you're properly registering your tool set with the agent using `withTools()`

### Unclear Tool Descriptions

**Problem**: The LLM is not using your tools correctly or misunderstands their purpose.

**Solutions**:
- Improve your `@LLMDescription` annotations to be more specific and clear
- Include examples in your descriptions when appropriate
- Specify parameter constraints in the descriptions (e.g., "Must be a positive number")
- Use consistent terminology throughout your descriptions

### Parameter Type Issues

**Problem**: The LLM is providing incorrect parameter types.

**Solutions**:
- Use simple parameter types when possible (String, Boolean, Int)
- Clearly describe the expected format in the parameter description
- For complex types, consider using String parameters with a specific format and parse them in your tool
- Include examples of valid inputs in your parameter descriptions

### Performance Issues

**Problem**: Your tools are causing performance problems.

**Solutions**:
- Keep tool implementations lightweight
- For resource-intensive operations, consider implementing asynchronous processing
- Cache results when appropriate
- Log tool usage to identify bottlenecks

## Next Steps

Now that you understand how to create and use tool annotations in Koog Agents, you can:

1. **Explore the Examples**: Look at the example code in the repository to see more complex implementations
2. **Create Your Own Tools**: Start building tools specific to your application's needs
3. **Combine Multiple Tool Sets**: Create specialized tool sets and combine them in your agent
4. **Experiment with Different Descriptions**: Try different description styles to see what works best with your LLM