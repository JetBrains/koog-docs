// This file was automatically generated from model-context-protocol.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleModelContextProtocol01

import ai.koog.agents.mcp.McpToolRegistryProvider

// Start an MCP server (for example, as a process)
val process = ProcessBuilder("path/to/mcp/server").start()

// Create the stdio transport 
val transport = McpToolRegistryProvider.defaultStdioTransport(process)
