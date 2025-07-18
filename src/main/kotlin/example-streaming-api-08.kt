// This file was automatically generated from streaming-api.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleStreamingApi08

import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.ToolArgs
import ai.koog.agents.core.tools.ToolDescriptor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
data class Book(
   val title: String,
   val author: String,
   val description: String
) : ToolArgs

class BookTool(): SimpleTool<Book>() {
   companion object {
      const val NAME = "book"
   }

   override suspend fun doExecute(args: Book): String {
      println("${args.title} by ${args.author}:\n ${args.description}")
      return "Done"
   }

   override val argsSerializer: KSerializer<Book>
      get() = Book.serializer()
   override val descriptor: ToolDescriptor
      get() = ToolDescriptor(
         name = NAME,
         description = "A tool to parse book information from Markdown",
         requiredParameters = listOf(),
         optionalParameters = listOf()
      )
}
