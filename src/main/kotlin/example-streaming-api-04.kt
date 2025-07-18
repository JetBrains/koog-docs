// This file was automatically generated from streaming-api.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleStreamingApi04

import ai.koog.prompt.markdown.markdown
import ai.koog.prompt.structure.markdown.MarkdownStructuredDataDefinition

fun markdownBookDefinition(): MarkdownStructuredDataDefinition {
    return MarkdownStructuredDataDefinition("bookList", schema = {
        markdown {
            header(1, "title")
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
