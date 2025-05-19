# Overview

This page provides detailed information about subgraphs in the Koog framework. Understanding these concepts is crucial for creating complex agent workflows that maintain context across multiple processing steps.

## Introduction

Subgraphs are a fundamental concept in the Koog framework that lets you break down complex agent workflows 
into manageable, sequential steps. Each subgraph represents a phase of processing, with its own set of tools, context,
and responsibilities.

Subgraphs are integral parts of strategies, which are graphs that represent the overall agent workflow. For more information about strategies, see [Custom strategy graphs](custom-strategy-graphs.md).

## Understanding subgraphs

A subgraph is a self-contained unit of processing within an agent strategy. Each subgraph:

- Has a unique name
- Contains a graph of nodes connected by edges
- Can use any tool or a subset of tools from the tool registry
- Receives input from the previous subgraph (or the initial user input)
- Produces output that is passed to the next subgraph (or the output)

Subgraphs are run in a defined sequence within a strategy, with each subgraph building upon the results of the previous one.

### Subgraph context

Each subgraph executes within a context that provides access to:

- The environment
- Output from the previous subgraph
- The agent configuration
- The LLM context (including the conversation history)
- The state manager
- The storage
- Session, strategy, and subgraph identifiers

The context is passed to each node within the subgraph and provides the necessary resources for the node to perform its
operations.
