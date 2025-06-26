# Prompt API

The Prompt API lets you create well-structured prompts with Kotlin DSL, execute them against different LLM providers, and process responses in different formats.

## Create a prompt

The Prompt API uses Kotlin DSL to create prompts. It supports the following types of messages:

- `system`: sets the context and instructions for the LLM.
- `user`: represents user input.
- `assistant`: represents LLM responses.

Here's an example of a simple prompt:

```kotlin
val prompt = prompt("prompt_name", LLMParams()) {
    // Add a system message to set the context
    system("You are a helpful assistant.")

    // Add a user message
    user("Tell me about Kotlin")

    // You can also add assistant messages for few-shot examples
    assistant("Kotlin is a modern programming language...")

    // Add another user message
    user("What are its key features?")
}
```

## Execute a prompt

To execute a prompt with a specific LLM, you need to the following:

1. Create a corresponding LLM client that handles the connection between your application and LLM providers. For example:
```kotlin
// Create an OpenAI client
val client = OpenAILLMClient(apiKey)
```
2. Call the `execute` method with the prompt and LLM as arguments.
```kotlin
// Execute the prompt
val response = client.execute(
    prompt = prompt,
    model = OpenAIModels.Chat.GPT4o  // You can choose different models
)
```

The following LLM clients are available:

* [OpenAILLMClient](https://api.koog.ai/prompt/prompt-executor/prompt-executor-clients/prompt-executor-openai-client/ai.koog.prompt.executor.clients.openai/-open-a-i-l-l-m-client/index.html)
* [AnthropicLLMClient](https://api.koog.ai/prompt/prompt-executor/prompt-executor-clients/prompt-executor-anthropic-client/ai.koog.prompt.executor.clients.anthropic/-anthropic-l-l-m-client/index.html)
* [GoogleLLMClient](https://api.koog.ai/prompt/prompt-executor/prompt-executor-clients/prompt-executor-google-client/ai.koog.prompt.executor.clients.google/-google-l-l-m-client/index.html)
* [OpenRouterLLMClient](https://api.koog.ai/prompt/prompt-executor/prompt-executor-clients/prompt-executor-openrouter-client/ai.koog.prompt.executor.clients.openrouter/-open-router-l-l-m-client/index.html)
* [OllamaClient](https://api.koog.ai/prompt/prompt-executor/prompt-executor-clients/prompt-executor-ollama-client/ai.koog.prompt.executor.ollama.client/-ollama-client/index.html)


Here's a simple example of using the Prompt API:

```kotlin
fun main() {
    // Set up the OpenAI client with your API key
    val token = System.getenv("OPENAI_API_KEY")
    val client = OpenAILLMClient(token)

    // Create a prompt
    val prompt = prompt("prompt_name", LLMParams()) {
        // Add a system message to set the context
        system("You are a helpful assistant.")

        // Add a user message
        user("Tell me about Kotlin")

        // You can also add assistant messages for few-shot examples
        assistant("Kotlin is a modern programming language...")

        // Add another user message
        user("What are its key features?")
    }

    // Execute the prompt and get the response
    val response = client.execute(prompt = prompt, model = OpenAIModels.Chat.GPT4o)
    println(response)
}
```

## Multimodal inputs

In addition to providing text messages within prompts, Koog also lets you send images, audio, video, and files to LLMs along with `user` messages. As with standard text-only prompts, you also add media to the prompt using the DSL structure for prompt construction.

```kotlin
val prompt = prompt("multimodal_input") {
    system("You are a helpful assistant.")

    user {
        +"Describe these images"

        attachments {
            image("https://example.com/test.png")
            image(Path("/User/koog/image.png"))
        }
    }
}
```

### Textual prompt content

To accommodate for the support for various attachment types and create a clear distinction between text and file inputs in a prompt, you put text messages in a dedicated `content` parameter within a user prompt. 
To add file inputs, provide them as a list within the `attachments` parameter. 

The general format of a user message that includes a text message and a list of attachments is as follows:

```kotlin
user(
    content = "This is the user message",
    attachments = listOf(
        // Add attachments
    )
)
```

### File attachments

To include an attachment, provide the file in the `attachments` parameter, following the format below:

```kotlin
user(
    content = "Describe this image",
    attachments = listOf(
        Attachment.Image(
            content = AttachmentContent.URL("https://example.com/capture.png"),
            format = "png",
            mimeType = "image/png",
            fileName = "capture.png"
        )
    )
)
```

The `attachments` parameter takes a list of file inputs, where each item is an instance of one of the following classes:

- `Attachment.Image`: local image files or images from a URL in various image file formats.
- `Attachment.Audio`: audio files from a URL or a local file path.
- `Attachment.Video`: video files provided from a URL or a local file path.
- `Attachment.File`: various document types or plain text files provided from a URL or a local file path.

Each of the classes above takes the following parameters:

| Name       | Data type                               | Required                   | Description                                                                                                 |
|------------|-----------------------------------------|----------------------------|-------------------------------------------------------------------------------------------------------------|
| `content`  | [AttachmentContent](#attachmentcontent) | Yes                        | The source of the provided file content. For more information, see [AttachmentContent](#attachmentcontent). |
| `format`   | String                                  | Yes                        | The format of the provided file. For example, `png`.                                                        |
| `mimeType` | String                                  | Only for `Attachment.File` | The MIME Type of the provided file. For example, `image/png`.                                               |
| `fileName` | String                                  | No                         | The name of the provided file including the extension. For example, `screenshot.png`.                       |

#### AttachmentContent

`AttachmentContent` defines the type and source of content that is provided as an input to the LLM. The following 
classes are supported:

`AttachmentContent.URL(val url: String)`

Provides file content from the specified URL. Takes the following parameter:

| Name   | Data type | Required | Description                      |
|--------|-----------|----------|----------------------------------|
| `url`  | String    | Yes      | The URL of the provided content. |

`AttachmentContent.Binary.Bytes(val data: ByteArray)`

Provides file content as a byte array. Takes the following parameter:

| Name   | Data type | Required | Description                                |
|--------|-----------|----------|--------------------------------------------|
| `data` | ByteArray | Yes      | The file content provided as a byte array. |

`AttachmentContent.Binary.Base64(val base64: String)`

Provides file content encoded as a Base64 string. Takes the following parameter:

| Name     | Data type | Required | Description                             |
|----------|-----------|----------|-----------------------------------------|
| `base64` | String    | Yes      | The Base64 string containing file data. |

`AttachmentContent.PlainText(val text: String)`

_Applies only if the attachment type is `Attachment.File`_. Provides content from a plain text file (such as the `text/plain` MIME type). Takes the following parameter:

| Name   | Data type | Required | Description              |
|--------|-----------|----------|--------------------------|
| `text` | String    | Yes      | The content of the file. |

### Mixed attachment content

In addition to providing different types of attachments in separate prompts or messages, you can also provide multiple and mixed types of attachments in a single `user` message, as shown below:

```kotlin
val prompt = prompt("mixed_content") {
    system("You are a helpful assistant.")

    user {
        +"Compare the image with the document content."

        attachments {
            image(Path("/User/koog/page.png"))
            binaryFile(Path("/User/koog/page.pdf"), "application/pdf")
        }
    }
}
```

## Prompt executors

Prompt executors provide a higher-level way to work with LLMs, handling the details of client creation and management.

You can use a prompt executor to manage and run prompts.
You can choose a prompt executor based on the LLM provider you plan to use or create a custom prompt executor using one of the available LLM clients.

The Koog framework provides several prompt executors:

- **Single provider executors**:
    - `simpleOpenAIExecutor`: for executing prompts with OpenAI models.
    - `simpleAnthropicExecutor`: for executing prompts with Anthropic models.
    - `simpleGoogleExecutor`: for executing prompts with Google models.
    - `simpleOpenRouterExecutor`: for executing prompts with OpenRouter.
    - `simpleOllamaExecutor`: for executing prompts with Ollama.

- **Multi-provider executor**:
    - `DefaultMultiLLMPromptExecutor`: For working with multiple LLM providers

### Create a single provider executor

To create a prompt executor for a specific LLM provider, use the corresponding function.
For example, to create the OpenAI prompt executor, you need to call the `simpleOpenAIExecutor` function and provide it with the API key required for authentication with the OpenAI service:

1. Create a prompt executor:
```kotlin
// Create an OpenAI executor
val promptExecutor = simpleOpenAIExecutor(apiToken)
```
2. Execute the prompt with a specific LLM:
```kotlin
// Execute a prompt
val response = promptExecutor.execute(
    prompt = prompt,
    model = OpenAIModels.Chat.GPT4o
)
```

### Create a multi-provider executor

To create a prompt executor that works with multiple LLM providers, do the following:

1. Configure clients for the required LLM providers with the corresponding API keys. For example:
```kotlin
val openAIClient = OpenAILLMClient(System.getenv("OPENAI_KEY"))
val anthropicClient = AnthropicLLMClient(System.getenv("ANTHROPIC_KEY"))
val googleClient = GoogleLLMClient(System.getenv("GOOGLE_KEY"))
```
2. Pass the configured clients to the `DefaultMultiLLMPromptExecutor` class constructor to create a prompt executor with multiple LLM providers:
```kotlin
val multiExecutor = DefaultMultiLLMPromptExecutor(openAIClient, anthropicClient, googleClient)
```
3. Execute the prompt with a specific LLM:
```kotlin
val response = multiExecutor.execute(
    prompt = prompt,
    model = OpenAIModels.Chat.GPT4o
)
```
