// This file was automatically generated from prompt-api.md by Knit tool. Do not edit.
package ai.koog.agents.example.examplePromptApi08

import ai.koog.prompt.dsl.prompt
import kotlinx.io.files.Path

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
