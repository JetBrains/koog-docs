// This file was automatically generated from prompt-api.md by Knit tool. Do not edit.
package ai.koog.agents.example.examplePromptApi07

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.message.Attachment
import ai.koog.prompt.message.AttachmentContent

val prompt = prompt("prompt") {

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
}
