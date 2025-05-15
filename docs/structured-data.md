# Structured data processing

## Introduction

The Structured Data Processing API provides a way to ensure that responses from Large Language Models (LLMs) 
conform to specific data structures.
This is crucial for building reliable AI applications where you need predictable, well-formatted data rather than free-form text.

This page explains how to use the Structured Data Processing API to define data structures, generate schemas, and 
request structured responses from LLMs.

## Key components and concepts

The Structured Data Processing API consists of several key components:

1. **Data structure definition**: Kotlin data classes annotated with kotlinx.serialization and LLM-specific annotations.
2. **JSON Schema generation**: tools to generate JSON schemas from Kotlin data classes.
3. **Structured LLM requests**: methods to request responses from LLMs that conform to the defined structures.
4. **Response handling**: processing and validating the structured responses.

## Defining data structures

The first step in using the Structured Data Processing API is to define your data structures using Kotlin data classes.

### Basic structure

```kotlin
@Serializable
@SerialName("WeatherForecast")
@LLMDescription("Weather forecast for a given location")
data class WeatherForecast(
    @LLMDescription("Temperature in Celsius")
    val temperature: Int,
    @LLMDescription("Weather conditions (e.g., sunny, cloudy, rainy)")
    val conditions: String,
    @LLMDescription("Chance of precipitation in percentage")
    val precipitation: Int
)
```

### Key annotations

- `@Serializable`: required for kotlinx.serialization to work with the class.
- `@SerialName`: specifies the name to use during serialization.
- `@LLMDescription`: provides a description of the class or field for the LLM.

### Supported features

The API supports a wide range of data structure features:

#### Nested classes

```kotlin
@Serializable
@SerialName("WeatherForecast")
data class WeatherForecast(
    // Other fields
    @LLMDescription("Coordinates of the location")
    val latLon: LatLon
) {
    @Serializable
    @SerialName("LatLon")
    data class LatLon(
        @LLMDescription("Latitude of the location")
        val lat: Double,
        @LLMDescription("Longitude of the location")
        val lon: Double
    )
}
```

#### Collections (lists and maps)

```kotlin
@Serializable
@SerialName("WeatherForecast")
data class WeatherForecast(
    // Other fields
    @LLMDescription("List of news articles")
    val news: List<WeatherNews>,
    @LLMDescription("Map of weather sources")
    val sources: Map<String, WeatherSource>
)
```

#### Enums

```kotlin
@Serializable
@SerialName("Pollution")
enum class Pollution { Low, Medium, High }
```

#### Polymorphism with sealed classes

```kotlin
@Serializable
@SerialName("WeatherAlert")
sealed class WeatherAlert {
    abstract val severity: Severity
    abstract val message: String

    @Serializable
    @SerialName("Severity")
    enum class Severity { Low, Moderate, Severe, Extreme }

    @Serializable
    @SerialName("StormAlert")
    data class StormAlert(
        override val severity: Severity,
        override val message: String,
        @LLMDescription("Wind speed in km/h")
        val windSpeed: Double
    ) : WeatherAlert()

    @Serializable
    @SerialName("FloodAlert")
    data class FloodAlert(
        override val severity: Severity,
        override val message: String,
        @LLMDescription("Expected rainfall in mm")
        val expectedRainfall: Double
    ) : WeatherAlert()
}
```

## Generating JSON schemas

Once you have defined your data structures, you can generate JSON schemas from them using the `JsonStructuredData` class:

```kotlin
val weatherForecastStructure = JsonStructuredData.createJsonStructure<WeatherForecast>(
    schemaFormat = JsonSchemaGenerator.SchemaFormat.JsonSchema,
    examples = exampleForecasts,
    schemaType = JsonStructuredData.JsonSchemaType.SIMPLE
)
```

### Schema format options

- `JsonSchema`: Standard JSON Schema format.
- `SimpleSchema`: A simplified schema format that may work better with some models but has limitations such as no polymorphism support.

### Schema type options

The following schema types are supported

* `SIMPLE`: A simplified schema type.
    - Supports only standard JSON fields
    - Does not support definitions, URL references, and recursive checks
    - **Does not support polymorphism**
    - Supported by a larger number of language models
    - Used for simpler data structures

* `FULL`: A more comprehensive schema type.
    - Supports advanced JSON Schema capabilities, including definitions, URL references, and recursive checks
    - **Supports polymorphism**: can work with sealed classes or interfaces and their implementations
    - Supported by fewer language models
    - Used for complex data structures with inheritance hierarchies

### Providing examples

You can provide examples to help the LLM understand the expected format:

```kotlin
val exampleForecasts = listOf(
    WeatherForecast(
        temperature = 25,
        conditions = "Sunny",
        precipitation = 0,
        // Other fields
    ),
    WeatherForecast(
        temperature = 18,
        conditions = "Cloudy",
        precipitation = 30,
        // Other fields
    )
)
```

## Requesting structured responses

To request a structured response from an LLM, use the `requestLLMStructured` method within a `writeSession`:

```kotlin
val structuredResponse = llm.writeSession {
    this.requestLLMStructured(
        structure = weatherForecastStructure,
        fixingModel = JetBrainsAIModels.OpenAI.GPT4o,
    )
}
```

### Fixing model

The `fixingModel` parameter specifies a model that will handle coercion if the output does not conform to the requested structure. This helps ensure that you always get a valid response.

## Integrating with agent strategies

You can integrate structured data processing into your agent strategies:

```kotlin
val agentStrategy = strategy("weather-forecast") {
    stage("weather") {
        val setup by nodeLLMSendStageInput()

        val getStructuredForecast by node<Message.Response, String> { _ ->
            val structuredResponse = llm.writeSession {
                this.requestLLMStructured(
                    structure = weatherForecastStructure,
                    fixingModel = JetBrainsAIModels.OpenAI.GPT4o,
                )
            }

            """
            Response structure:
            ${structuredResponse.structure}
            """.trimIndent()
        }

        edge(nodeStart forwardTo setup)
        edge(setup forwardTo getStructuredForecast)
        edge(getStructuredForecast forwardTo nodeFinish)
    }
}
```

## Full code sample

Here is a full example of using the Structured Data Processing API:

```kotlin
// Note: Import statements are omitted for brevity

@Serializable
@SerialName("SimpleWeatherForecast")
@LLMDescription("Simple weather forecast for a location")
data class SimpleWeatherForecast(
    @LLMDescription("Location name")
    val location: String,
    @LLMDescription("Temperature in Celsius")
    val temperature: Int,
    @LLMDescription("Weather conditions (e.g., sunny, cloudy, rainy)")
    val conditions: String
)

fun main(): Unit = runBlocking {
    // Create sample forecasts
    val exampleForecasts = listOf(
        SimpleWeatherForecast(
            location = "New York",
            temperature = 25,
            conditions = "Sunny"
        ),
        SimpleWeatherForecast(
            location = "London",
            temperature = 18,
            conditions = "Cloudy"
        )
    )

    // Generate JSON Schema
    val forecastStructure = JsonStructuredData.createJsonStructure<SimpleWeatherForecast>(
        schemaFormat = JsonSchemaGenerator.SchemaFormat.JsonSchema,
        examples = exampleForecasts,
        schemaType = JsonStructuredData.JsonSchemaType.SIMPLE
    )

    // Define agent strategy
    val agentStrategy = strategy("weather-forecast") {
        stage("weather") {
            val setup by nodeLLMSendStageInput()

            val getStructuredForecast by node<Message.Response, String> { _ ->
                val structuredResponse = llm.writeSession {
                    this.requestLLMStructured(
                        structure = forecastStructure,
                        fixingModel = JetBrainsAIModels.OpenAI.GPT4o,
                    )
                }

                """
                Response structure:
                ${structuredResponse.structure}
                """.trimIndent()
            }

            edge(nodeStart forwardTo setup)
            edge(setup forwardTo getStructuredForecast)
            edge(getStructuredForecast forwardTo nodeFinish)
        }
    }

    // Set up event handler
    val eventHandler = EventHandler {
        handleError {
            println("An error occurred: ${it.message}")
            true
        }

        handleResult {
            println("Result:\n$it")
        }
    }

    // Configure and run the agent
    val token = System.getenv("GRAZIE_TOKEN") ?: error("Environment variable GRAZIE_TOKEN is not set")

    val agentConfig = LocalAgentConfig(
        prompt = prompt(JetBrainsAIModels.OpenAI.GPT4o, "weather-forecast") {
            system(
                """
                You are a weather forecasting assistant.
                When asked for a weather forecast, provide a realistic but fictional forecast.
                """.trimIndent()
            )
        },
        maxAgentIterations = 5
    )

    val runner = KotlinAIAgent(
        promptExecutor = simpleGrazieExecutor(token),
        toolRegistry = ToolRegistry.EMPTY,
        strategy = agentStrategy,
        eventHandler = eventHandler,
        agentConfig = agentConfig,
        cs = this,
    )

    runner.run("Get weather forecast for Paris")
}
```

## Best practices

1. **Use clear descriptions**: provide clear and detailed descriptions using `@LLMDescription` annotations to help the LLM understand the expected data.

2. **Provide examples**: include examples of valid data structures to guide the LLM.

3. **Handle errors gracefully**: implement proper error handling to deal with cases where the LLM might not produce a valid structure.

4. **Use appropriate schema types**: select the appropriate schema format and type based on your needs and the capabilities of the LLM you are using.

5. **Test with different models**: different LLMs may have varying abilities to follow structured formats, so test with multiple models if possible.

6. **Start simple**: begin with simple structures and gradually add complexity as needed.

7. **Use polymorphism Carefully**: while the API supports polymorphism with sealed classes, be aware that it can be more challenging for LLMs to handle correctly.
