// This file was automatically generated from model-context-protocol.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleModelContextProtocol04

import ai.koog.agents.mcp.McpToolRegistryProvider
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.client.Client
import kotlinx.coroutines.runBlocking

val existingMcpClient =  Client(clientInfo = Implementation(name = "mcpClient", version = "dev"))

fun main() {
    runBlocking {

// Create a tool registry from an existing MCP client
val toolRegistry = McpToolRegistryProvider.fromClient(
    mcpClient = existingMcpClient
)
    }
}
