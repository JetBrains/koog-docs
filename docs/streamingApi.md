# Streaming API

## Introduction

The Streaming API in the Kotlin AI Library allows you to process structured data from Large Language Models (LLMs) as it
arrives, rather than waiting for the entire response.
This guide explains how to use the Streaming API to efficiently handle structured data in markdown format.

## What is the Streaming API?

The Streaming API enables real-time processing of structured data from LLM responses. Instead of waiting for the
complete response, you can:

- Process data as it arrives in chunks
- Parse structured information on-the-fly
- Emit structured objects as they are completed
- Handle these objects immediately (collect them or pass to tools)

This approach is particularly useful for:

- Improving responsiveness in user interfaces
- Processing large responses efficiently
- Implementing real-time data processing pipelines

The Streaming API allows parsing the output as **a structured data** from the .md format or as a set of **plain text**
chunks.

## Working with Raw String Stream

It's important to note that you can parse the output by working directly with the raw string stream. This approach gives
you more flexibility and control over the parsing process.

Raw String Stream with the Markdown definition of the output structure:

```kotlin
fun markdownBookDefinition(): MarkdownStructuredDataDefinition {
    return MarkdownStructuredDataDefinition("name", schema = { /*...*/ })
}

val mdDefinition = markdownBookDefinition()

llm.writeSession {
    val stream = requestLLMStreaming(mdDefinition)
    // Access the raw string chunks directly
    stream.collect { chunk ->
        // Process each chunk of text as it arrives
        println("Received chunk: $chunk") // the chunks together will be structured as a text following the mdDefinition scheme
    }
}
```

Or without the definition:

```kotlin
llm.writeSession {
    val stream = requestLLMStreaming()
    // Access the raw string chunks directly
    stream.collect { chunk ->
        // Process each chunk of text as it arrives
        println("Received chunk: $chunk") // the chunks together won't be structured in a specific way
    }
}
```

## Working with Structured Data

Although it's possible to work with the raw string stream, it's often more convenient to work with structured data.

The structured data approach includes of the following key components:

1. **MarkdownStructuredDataDefinition**: a class to help you define the schema and examples for structured data in
   Markdown format.
2. **markdownStreamingParser**: a function to create a parser that processes a stream of Markdown chunks and emits
   events.

### 1. Define Your Data Structure

First, define a data class to represent your structured data:

```kotlin
@Serializable
data class Book(
    val bookName: String,
    val author: String,
    val description: String
)
```

### 2. Define the Markdown Structure

Create a definition that specifies how your data should be structured in Markdown with
`MarkdownStructuredDataDefinition` class:

```kotlin
fun markdownBookDefinition(): MarkdownStructuredDataDefinition {
    return MarkdownStructuredDataDefinition("bookList", schema = {
        markdown {
            header(1, "bookName")
            bulleted {
                item("author")
                item("description")
            }
        }
    }, examples = {
        markdown {
            header(1, "The Great Gatsby")
            bulleted {
                item("F. Scott Fitzgerald")
                item("A novel set in the Jazz Age that tells the story of Jay Gatsby's unrequited love for Daisy Buchanan.")
            }
        }
    })
}
```

### 3. Create a Parser for Your Data Structure

The `markdownStreamingParser` provides several handlers for different Markdown elements:

```kotlin
markdownStreamingParser {
    // Handle headers of level 1
    // The header level can be from 1 to 6
    onHeader(1) { headerText ->
        // Process header text
    }

    // Handle bullet points
    onBullet { bulletText ->
        // Process bullet text
    }

    // Handle code blocks
    onCodeBlock { codeBlockContent ->
        // Process code block content
    }

    // Handle lines matching a regex pattern
    onLineMatching(Regex("pattern")) { line ->
        // Process matching line
    }

    // Handle the end of the stream
    onFinishStream { remainingText ->
        // Process any remaining text or perform cleanup
    }
}
```

Using them, you can implement a function that parses the Markdown stream and emits your data objects with `markdownStreamingParser`
function.

```kotlin
fun parseMarkdownStreamToBooks(markdownStream: Flow<String>): Flow<Book> {
    return flow {
        markdownStreamingParser {
            var currentBookName = ""
            val bulletPoints = mutableListOf<String>()

            // handle the event of receiving the Markdown header in the response stream
            onHeader(1) { headerText ->
                // if we had a previous book, emit it
                if (currentBookName.isNotEmpty() && bulletPoints.isNotEmpty()) {
                    val author = bulletPoints.getOrNull(0) ?: ""
                    val description = bulletPoints.getOrNull(1) ?: ""
                    emit(Book(currentBookName, author, description))
                }

                currentBookName = headerText
                bulletPoints.clear()
            }

            // handle the event of receiving the Markdown bullets list in the response stream
            onBullet { bulletText ->
                bulletPoints.add(bulletText)
            }

            // handle the end of the response stream
            onFinishStream {
                // Emit the last book if it exists
                if (currentBookName.isNotEmpty() && bulletPoints.isNotEmpty()) {
                    val author = bulletPoints.getOrNull(0) ?: ""
                    val description = bulletPoints.getOrNull(1) ?: ""
                    emit(Book(currentBookName, author, description))
                }
            }
        }.parseStream(markdownStream)
    }
}
```

### 4. Use the Parser in Your Agent Strategy

```kotlin
val agentStrategy = simpleStrategy("library-assistant") {
    // describe the node containing the output stream parsing 
    val getMdOutput by node<Unit, String> { _ ->
        val books = mutableListOf<Book>()
        val mdDefinition = markdownBookDefinition()

        llm.writeSession {
            // initiate the response stream in a form of the definition `mdDefinition`
            val markdownStream = requestLLMStreaming(mdDefinition)
            // call the parser with the result of the response stream and make actions with the result
            parseMarkdownStreamToBooks(markdownStream).collect { book ->
                books.add(book)
                println("Parsed Book: ${book.bookName} by ${book.author}")
            }
        }

        // a custom function for output formatting
        formatOutput(books)
    }

    // describe the agent's graph making sure the node is accessible
    edge(nodeStart forwardTo getMdOutput)
    edge(getMdOutput forwardTo nodeFinish)
}
```

## Advanced Usage: Streaming with Tools

You can also use the Streaming API with tools to process data as it arrives.

### 1. Define a Tool for Your Data Structure

```kotlin
class BookTool() : SimpleTool<Book>() {
    companion object {
        const val NAME = "book"
    }

    override suspend fun doExecute(args: Book): String {
        println("${args.bookName} by ${args.author}:\n ${args.description}")
        return "Done"
    }

    override val argsSerializer: KSerializer<Book>
        get() = Book.serializer()
    override val descriptor: ToolDescriptor
        get() = ToolDescriptor(
            name = NAME,
            description = "A tool to parse book information from markdown",
            requiredParameters = listOf(),
            optionalParameters = listOf()
        )
}
```

### 2. Use the Tool with Streaming Data

```kotlin
val agentStrategy = simpleStrategy("library-assistant") {
    val getMdOutput by node<Unit, String> { _ ->
        val mdDefinition = markdownBookDefinition()

        llm.writeSession {
            val markdownStream = requestLLMStreaming(mdDefinition)

            parseMarkdownStreamToBooks(markdownStream).collect { book ->
                callToolRaw(BookTool.NAME, book)
                // Other possible options:
                // callTool(BookTool::class, book)
                // callTool<BookTool>(book)
                // findTool(BookTool::class).execute(book)
            }

            // For parallel tool calls:
            // parseMarkdownStreamToBooks(markdownStream).toParallelToolCallsRaw(BookTool::class).collect()
        }
        ""
    }

    edge(nodeStart forwardTo getMdOutput)
    edge(getMdOutput forwardTo nodeFinish)
}
```

### 3. Register the Tool in Your Agent Configuration

```kotlin
val toolRegistry = SimpleToolRegistry {
    tool(BookTool())
}

val runner = KotlinAIAgent(
    promptExecutor = simpleGrazieExecutor(token),
    toolRegistry = toolRegistry,
    strategy = agentStrategy,
    agentConfig = agentConfig,
    cs = this,
)
```

## Best Practices

1. **Define Clear Structures**: Create clear and unambiguous markdown structures for your data.

2. **Provide Good Examples**: Include comprehensive examples in your MarkdownStructuredDataDefinition to guide the LLM.

3. **Handle Incomplete Data**: Always check for null or empty values when parsing data from the stream.

4. **Clean Up Resources**: Use the `onFinishStream` handler to clean up resources and process any remaining data.

5. **Error Handling**: Implement proper error handling for malformed markdown or unexpected data.

6. **Testing**: Test your parser with various input scenarios, including partial chunks and malformed input.

7. **Parallel Processing**: For independent data items, consider using parallel tool calls for better performance.
