// This file was automatically generated from complex-workflow-agents.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleComplexWorkflowAgents05

import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy

const val transformedOutput = "transformed-output"

val strategy = strategy<String, String>("Simple calculator") {

    val sourceNode by node<String, String> { input ->
        // Process the input and return an output
        // You can use llm.writeSession to interact with the LLM
        // You can call tools using callTool, callToolRaw, etc.
        transformedOutput
    }

    val targetNode by node<String, String> { input ->
        // Process the input and return an output
        // You can use llm.writeSession to interact with the LLM
        // You can call tools using callTool, callToolRaw, etc.
        transformedOutput
    }

// Basic edge
edge(sourceNode forwardTo targetNode)

// Edge with condition
edge(sourceNode forwardTo targetNode onCondition { output ->
    // Return true to follow this edge, false to skip it
    output.contains("specific text")
})

// Edge with transformation
edge(sourceNode forwardTo targetNode transformed { output ->
    // Transform the output before passing it to the target node
    "Modified: $output"
})

// Combined condition and transformation
edge(sourceNode forwardTo targetNode onCondition { it.isNotEmpty() } transformed { it.uppercase() })
}
