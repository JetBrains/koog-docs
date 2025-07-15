// This file was automatically generated from model-context-protocol.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleModelContextProtocol03

import ai.koog.agents.example.exampleModelContextProtocol01.transport
import ai.koog.agents.mcp.McpToolRegistryProvider
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {

// Create a tool registry with tools from the MCP server
val toolRegistry = McpToolRegistryProvider.fromTransport(
    transport = transport,
    name = "my-client",
    version = "1.0.0"
)
    }
}
