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

In addition to providing text messages within prompts, Koog also lets you send images, audio, and documents to LLMs
along with `user` prompt messages. As with standard text-only prompts, you also add media to the prompt using the DSL structure for prompt construction. Specifically, you add files within the `attachments` lambda in the
following format:

```kotlin
attachments {
    image("/Users/koog/photo.jpg")
    document("report.pdf")
    audio(audioData, "mp3")
}
```

In addition to text, Koog currently supports the following types of media in `user` messages:

- `image`: local image files or images from a URL in various image file formats.
- `document`: local document files. For security reasons, Koog does not support providing documents from URLs.
- `audio`: audio data provided as a byte array.

Note that the support for certain formats or file sources may differ between LLM providers and models. For more 
information, see file type support references in the sections below.

### Text

When using multiple media types in a single `user` message, you need to make a clear distinction between the types of content that are provided to the LLM. To accommodate for the support for various media types, text messages also have a 
dedicated `text` function to indicate their content type. The format of the function is specified below:

```kotlin
user {
    text(text)
}
```

The `text` function takes the following parameter:

| Name   | Data type | Required | Description                                          |
|--------|-----------|----------|------------------------------------------------------|
| `text` | String    | Yes      | The text of the message you want to send to the LLM. |

The following example shows a prompt that includes a text message passed using the `text` function:

```kotlin
val prompt = prompt("llm_question", LLMParams()) {
    // Add a system message to set the context
    system("You are a helpful assistant.")

    // Add the user message
    user {
        text("When was the first version of IntelliJ IDEA released?")
    }
}
```

### Images

Koog supports images provided from one of the following sources:

- Local image files
- Images from URLs

To include an image in a prompt, provide the image file inside the `attachments` lambda, following the format below:

```kotlin
attachments {
    image(source)
}
```

The `image` function takes the following parameter:

| Name     | Data type | Required | Description                                               |
|----------|-----------|----------|-----------------------------------------------------------|
| `source` | String    | Yes      | The path to the local image file or the URL of the image. |


Here is an example of a prompt that includes a local image file:

```kotlin
val prompt = prompt("image_description", LLMParams()) {
    // Add a system message to set the context
    system("You are a helpful assistant.")

    // Add the user message along with a local image file
    user {
        text("What do you see in this image?")
        attachments {
            image("/Users/koog/capture.png")
        }
    }
}
```

#### Image format support per LLM provider

The following table provides an overview of supported image sources and file format types, classified by LLM provider.

| Provider   | Source                | Supported file formats                       |
|------------|-----------------------|----------------------------------------------|
| Anthropic  | Local images and URLs | `png`, `jpeg`, `webp`, `gif`                 |
| Google     | Local images          | `png`, `jpeg`, `webp`, `heic`, `heif`, `gif` |
| Ollama     | No image support      |                                              |
| OpenAI     | Local images and URLs | `png`, `jpeg`, `webp`, `gif`                 |
| OpenRouter | Local images and URLs | `png`, `jpeg`, `webp`, `gif`                 |


### Documents

To include a document in a prompt, provide the file inside the `attachments` lambda, following the format below:

```kotlin
attachments {
    document(source)
}
```

The `document` function takes the following parameter:

| Name     | Data type | Required | Description                     |
|----------|-----------|----------|---------------------------------|
| `source` | String    | Yes      | The local path to the document. |

Note that Koog only supports documents from a specified local file path, while URLs are not supported as sources for
security reasons.

Here is an example of a document summarization prompt that also includes a PDF document from the current directory and
provides it to the LLM for summarization:

```kotlin
val prompt = prompt("document_summarization", LLMParams()) {
    // Add a system message to set the context
    system("You are a helpful assistant.")

    // Add the user message along with a local PDF document
    user {
        text("Provide a summary of this PDF document in no more than 500 words.")
        attachments {
            document("report.pdf")
        }
    }
}
```

#### Document format support per LLM provider

The following table provides an overview of document formats supported in Koog, classified by LLM provider. You can 
provide documents only as local files.

| Provider   | Supported file formats                                             |
|------------|--------------------------------------------------------------------|
| Anthropic  | `pdf`, `txt`, `md`                                                 |
| Google     | `pdf`, `js`, `py`, `txt`, `html`, `css`, `md`, `csv`, `xml`, `rtf` |
| Ollama     | No document support                                                |
| OpenAI     | `pdf`                                                              |
| OpenRouter | `pdf`                                                              |

### Audio

To add audio files to prompts, first use the `readBytes` function to read the content of the file into a byte array, then 
provide the byte array as an `audio` attachment within the prompt in the following format:

```kotlin
attachments {
    audio(data, format)
}
```

The `audio` function takes the following parameters:

| Name     | Data type | Required | Description                                                                 |
|----------|-----------|----------|-----------------------------------------------------------------------------|
| `data`   | ByteArray | Yes      | The audio data as a byte array.                                             |
| `format` | String    | Yes      | The file format of the original audio file that was read into a byte array. |

Here is an example of an audio transcription prompt that includes a local mp3 file as a byte array:

```kotlin
// Read the audio file into a byte array
val audioData = File("/Users/koog/workshop.mp3").readBytes()

// Construct the prompt
val prompt = prompt("audio_transcription", LLMParams()) {
    // Add a system message to set the context
    system("You are a helpful assistant.")

    // Add the user message along with the audio
    user {
        text("Transcribe this audio.")
        attachments {
            audio(audioData, "mp3")
        }
    }
}
```
#### Audio format support per LLM provider

The following table provides an overview of supported audio file formats, classified by LLM provider.

| Provider   | Supported file formats                     |
|------------|--------------------------------------------|
| Anthropic  | No audio support                           |
| Google     | `wav`, `mp3`, `aiff`, `aac`, `ogg`, `flac` |
| Ollama     | No audio support                           |
| OpenAI     | `wav`, `mp3`                               |
| OpenRouter | `wav`, `mp3`                               |

### Mixed media content

In addition to providing different types of media in separate prompts or messages, you can also provide multiple and 
mixed types of content in a single `user` message, as shown below:

```kotlin
val prompt = prompt("mixed_content", LLMParams()) {
    // Add a system message to set the context
    system("You are a helpful assistant.")

    // Add the user message with different types of attached files
    user {
        text("Compare the image with the document content:")
        attachments {
            image("screenshot.png")
            document("/Users/koog/report.pdf")
        }
        text("What are the key differences?")
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
