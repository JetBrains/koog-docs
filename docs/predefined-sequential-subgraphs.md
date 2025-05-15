# Predefined sequential subgraphs

This page provides detailed information about predefined stages in the Kotlin Agentic Framework and how history is 
passed between these stages. Understanding these concepts is crucial for creating complex agent workflows that maintain context across multiple processing steps.

## Introduction

Stages are a fundamental concept in the Kotlin Agentic Framework that lets you break down complex agent workflows into
manageable, sequential steps. Each stage represents a distinct phase of processing, with its own set of tools, context,
and responsibilities.

One of the key challenges in multi-stage workflows is maintaining context between stages, particularly the conversation
history with the language model (LLM). The Kotlin Agentic Framework provides several mechanisms for passing history 
between stages, letting you balance context preservation with token efficiency.

## Understanding stages

A stage is a self-contained unit of processing within an agent strategy. Each stage:

- Has a unique name
- Contains a graph of nodes connected by edges
- Can have its own set of tools (or can use tools that are shared with other stages)
- Receives input from the previous stage (or the initial user input)
- Produces output that is passed to the next stage (or the output)

Stages are run sequentially within a strategy, with each stage building upon the results of the previous stages.

### Types of stages

The Kotlin Agentic Framework provides two types of stages:

1. **Static stages** (`LocalAgentStaticStage`): Expect a predefined set of tools and validate that all required tools
   are available. This is useful when you know exactly which tools a stage needs.

2. **Dynamic Stages** (`LocalAgentDynamicStage`): Do not expect any predefined tools, relying on the tools defined in
   the graph. This provides more flexibility but less validation.

### Stage context

Each stage executes within a context (`LocalAgentStageContext`) that provides access to:

- The environment
- Output from the previous stage
- The agent configuration
- The LLM context (including the conversation history)
- The state manager
- The storage
- Session, strategy, and stage identifiers

The context is passed to each node within the stage and provides the necessary resources for the node to perform its
operations.
