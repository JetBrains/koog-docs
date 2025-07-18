// This file was automatically generated from prompt-api.md by Knit tool. Do not edit.
package ai.koog.agents.example.examplePromptApi05

import ai.koog.prompt.dsl.prompt
import kotlinx.io.files.Path

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
