# Code embeddings

The `embeddings` module provides functionality for generating and comparing embeddings of text and code. Embeddings are vector representations that capture semantic meaning, allowing for efficient similarity comparisons.

## Overview

This module consists of two main components:

1. **embeddings-base**: core interfaces and data structures for embeddings.
2. **embeddings-local**: implementation using Ollama for local embedding generation.

## Getting started

### Installation

Add the following dependencies to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("ai.jetbrains.code.embeddings:code-embeddings-base:VERSION")
    implementation("ai.jetbrains.code.embeddings:code-embeddings-local:VERSION")
}
```

### Basic usage

To use the embeddings functionality, you need to have Ollama installed and running on your system.
For installation and running instructions, refer to the [official Ollama GitHub repository](https://github.com/ollama/ollama).

```kotlin
fun main() {
    // Initialize the Ollama embedder client
    val baseUrl = "http://localhost:11434"
    val model = OllamaEmbeddingModel.NOMIC_EMBED_TEXT
    val client = OllamaEmbedderClient(baseUrl, model)

    // Create an embedder
    val embedder = OllamaEmbedder(client)

    try {
        // Use the embedder
        // ...
    } finally {
        // Clean up
        client.close()
    }
}
```

To use an Ollama embedding model, make sure you download it locally using the following command:

```bash
ollama pull <ollama-model-id>
```

Replace `<ollama-model-id>` with the Ollama identifier of the specific model. For more information about available
models and their identifiers, see [Ollama models overview](#ollama-models-overview).

## API documentation

### Interface: `Embedder`

The core interface for embedding operations. Implementations of this interface can convert text into 
vector representations (embeddings) and calculate the difference between two embeddings.

#### Methods

`embed`

```kotlin
suspend fun embed(text: String): Vector
```

**Description:** Embeds the given text into a vector representation.

**Parameters**:

| Name | Type     | Description       |
|------|----------|-------------------|
| text | `String` | The text to embed |

- **Returns**: A vector representation of the text.

`diff`

```kotlin
fun diff(embedding1: Vector, embedding2: Vector): Double
```
**Description**: Calculates the difference between two embeddings. Lower values indicate more similar embeddings.

**Parameters**:

| Name       | Type     | Description                                        |
|------------|----------|----------------------------------------------------|
| embedding1 | `Vector` | The first embedding in the difference calculation  |
| embedding2 | `Vector` | The second embedding in the difference calculation |

- **Returns**: The measure of difference between the embeddings.

### Data class: `Vector`

A vector of floating-point values used for embeddings.

**Properties**

| Name  | Type           | Description                                       |
|-------|----------------|---------------------------------------------------|
| value | `List<Double>` | The floating-point values that make up the vector |

#### Methods

`cosineSimilarity`

```kotlin
fun cosineSimilarity(other: Vector): Double
```

**Description:** Calculates the cosine similarity between the vector and another vector. The result is a value between
-1 and 1, where 1 means the vectors are identical, 0 means they are orthogonal, and -1 means they are completely 
opposite.

**Parameters**:

| Name  | Type     | Description                      |
|-------|----------|----------------------------------|
| other | `Vector` | The other vector to compare with |

- **Returns**: The cosine similarity between the two vectors.

**Exceptions**: `IllegalArgumentException` if the vectors have different dimensions.

`euclideanDistance`

```kotlin
fun euclideanDistance(other: Vector): Double
```

**Description:** Calculates the Euclidean distance between this vector and another vector. The result is a non-negative value, where 0 means the vectors are identical.

**Parameters**:

| Name  | Type     | Description                      |
|-------|----------|----------------------------------|
| other | `Vector` | The other vector to compare with |

- **Returns**: The Euclidean distance between the two vectors.

### Class: `OllamaEmbedderClient`

The client for interacting with the Ollama API.

**Properties**:

| Name       | Type                   | Description                             |
|------------|------------------------|-----------------------------------------|
| baseUrl    | `String`               | The base URL of the Ollama API          |
| modelId    | `OllamaEmbeddingModel` | The ID of the model to use              |
| httpClient | `HttpClient`           | The HTTP client to use for API requests |

#### Methods

`embed`

```kotlin
suspend fun embed(text: String): Vector
```

**Description:** Embeds the given text into a vector representation using an Ollama embedding model.

**Parameters**:

| Name | Type     | Description       |
|------|----------|-------------------|
| text | `String` | The text to embed |

- **Returns**: A vector representation of the text.

`close`

```kotlin
fun close()
```

**Description:** Closes the HTTP client.

### Class: `OllamaEmbedder`

Implementation of the `Embedder` interface hat uses Ollama models for embedding text.

#### Properties

| Name   | Type                   | Description                                       |
|--------|------------------------|---------------------------------------------------|
| client | `OllamaEmbedderClient` | The Ollama model client to use for embedding text |


#### Methods

`embed`

```kotlin
override suspend fun embed(text: String): Vector
```

**Description:** Embeds the given text into a vector representation using an Ollama embedding model.

**Parameters**:

| Name | Type     | Description       |
|------|----------|-------------------|
| text | `String` | The text to embed |

- **Returns**: A vector representation of the text.

`diff`

```kotlin
override fun diff(embedding1: Vector, embedding2: Vector): Double
```
**Description**: Calculates the difference between two embeddings using cosine distance. Cosine distance is defined as 
1 - cosine similarity. The result is a value between 0 and 1, where 0 means the embeddings are identical.

**Parameters**:

| Name       | Type     | Description                                        |
|------------|----------|----------------------------------------------------|
| embedding1 | `Vector` | The first embedding in the difference calculation  |
| embedding2 | `Vector` | The second embedding in the difference calculation |

- **Returns**: The cosine distance between the embeddings.

## Ollama models overview

The following table provides an overview of the available Ollama embedding models.

| Model ID          | Ollama ID         | Parameters | Dimensions | Context Length | Performance                                                           | Tradeoffs                                                          |
|-------------------|-------------------|------------|------------|----------------|-----------------------------------------------------------------------|--------------------------------------------------------------------|
| NOMIC_EMBED_TEXT  | nomic-embed-text  | 137M       | 768        | 8192           | High-quality embeddings for semantic search and text similarity tasks | Balanced between quality and efficiency                            |
| ALL_MINILM        | all-minilm        | 33M        | 384        | 512            | Fast inference with good quality for general text embeddings          | Smaller model size with reduced context length, but very efficient |
| MULTILINGUAL_E5   | zylonai/multilingual-e5-large   | 300M       | 768        | 512            | Strong performance across 100+ languages                              | Larger model size but provides excellent multilingual capabilities |
| BGE_LARGE         | bge-large         | 335M       | 1024       | 512            | Excellent for English text retrieval and semantic search              | Larger model size but provides high-quality embeddings             |
| MXBAI_EMBED_LARGE | mxbai-embed-large | -          | -          | -              | High-dimensional embeddings of textual data                           | Designed for creating high-dimensional embeddings                  |

For more information about these models, see Ollama's [Embedding Models](https://ollama.com/blog/embedding-models)
blog post.

## Choosing a model

Here are some general tips on which Ollama embedding model to select depending on your requirements:

- For general text embeddings, use `NOMIC_EMBED_TEXT`.
- For multilingual support, use `MULTILINGUAL_E5`.
- For maximum quality (at the cost of performance), use `BGE_LARGE`.
- For maximum efficiency (at the cost of some quality), use `ALL_MINILM`.
- For high-dimensional embeddings, use `MXBAI_EMBED_LARGE`.

## Examples

The following examples show how you can use embeddings to compare code with text or other code snippets.

### Code-to-text comparison

Compare code snippets with natural language descriptions to find semantic matches:

```kotlin
suspend fun compareCodeToText(embedder: Embedder) { // Embedder type
    // Code snippet
    val code = """
        fun factorial(n: Int): Int {
            return if (n <= 1) 1 else n * factorial(n - 1)
        }
    """.trimIndent()

    // Text descriptions
    val description1 = "A recursive function that calculates the factorial of a number"
    val description2 = "A function that sorts an array of integers"

    // Generate embeddings
    val codeEmbedding = embedder.embed(code)
    val desc1Embedding = embedder.embed(description1)
    val desc2Embedding = embedder.embed(description2)

    // Calculate differences (lower value means more similar)
    val diff1 = embedder.diff(codeEmbedding, desc1Embedding)
    val diff2 = embedder.diff(codeEmbedding, desc2Embedding)

    println("Difference between code and description 1: $diff1")
    println("Difference between code and description 2: $diff2")

    // The code should be more similar to description1 than description2
    if (diff1 < diff2) {
        println("The code is more similar to: '$description1'")
    } else {
        println("The code is more similar to: '$description2'")
    }
}
```

### Code-to-code comparison

Compare code snippets to find semantic similarities regardless of syntax differences:

```kotlin
suspend fun compareCodeToCode(embedder: Embedder) { // Embedder type
    // Two implementations of the same algorithm in different languages
    val kotlinCode = """
        fun fibonacci(n: Int): Int {
            return if (n <= 1) n else fibonacci(n - 1) + fibonacci(n - 2)
        }
    """.trimIndent()

    val pythonCode = """
        def fibonacci(n):
            if n <= 1:
                return n
            else:
                return fibonacci(n-1) + fibonacci(n-2)
    """.trimIndent()

    val javaCode = """
        public static int bubbleSort(int[] arr) {
            int n = arr.length;
            for (int i = 0; i < n-1; i++) {
                for (int j = 0; j < n-i-1; j++) {
                    if (arr[j] > arr[j+1]) {
                        int temp = arr[j];
                        arr[j] = arr[j+1];
                        arr[j+1] = temp;
                    }
                }
            }
            return arr;
        }
    """.trimIndent()

    // Generate embeddings
    val kotlinEmbedding = embedder.embed(kotlinCode)
    val pythonEmbedding = embedder.embed(pythonCode)
    val javaEmbedding = embedder.embed(javaCode)

    // Calculate differences
    val diffKotlinPython = embedder.diff(kotlinEmbedding, pythonEmbedding)
    val diffKotlinJava = embedder.diff(kotlinEmbedding, javaEmbedding)

    println("Difference between Kotlin and Python implementations: $diffKotlinPython")
    println("Difference between Kotlin and Java implementations: $diffKotlinJava")

    // The Kotlin and Python implementations should be more similar
    if (diffKotlinPython < diffKotlinJava) {
        println("The Kotlin code is more similar to the Python code")
    } else {
        println("The Kotlin code is more similar to the Java code")
    }
}
```