# AgentMemory

## Feature overview

The AgentMemory feature is a component of the Koog framework that lets AI agents store, retrieve, and use
information across conversations.

### Purpose

The AgentMemory Feature addresses the challenge of maintaining context in AI agent interactions by:

- Storing important facts extracted from conversations.
- Organizing information by concepts, subjects, and scopes.
- Retrieving relevant information when needed in future interactions.
- Enabling personalization based on user preferences and history.

### Architecture

The AgentMemory feature is built on a hierarchical structure:

**Facts**: individual pieces of information stored in memory

- **SingleFact**: a single value associated with a concept.
- **MultipleFacts**: multiple values associated with a concept.

**Concepts**: categories of information with associated metadata.

- Keyword: unique identifier for the concept.
- Description: detailed explanation of what the concept represents.
- FactType: whether the concept stores single or multiple facts.

**Subjects**: entities that facts can be associated with:

- USER: facts about the user.
- MACHINE: facts about the local machine.
- PROJECT: facts about the current project.
- ORGANIZATION: facts about the organization.

**Scopes**: contexts in which facts are relevant:

- Agent: specific to an agent.
- Feature: specific to a feature.
- Product: specific to a product.
- Organization: relevant across an organization.
- CrossProduct: relevant across multiple products.

## Configuration and initialization

The feature integrates with the agent pipeline through the `AgentMemory` class, which provides methods for saving and
loading facts, and can be installed as a feature in the agent configuration.

### Configuration

The `AgentMemory.Config` class is the configuration class for the AgentMemory feature.

```kotlin
class Config : FeatureConfig() {
    var memoryProvider: AgentMemoryProvider = NoMemory
    var scopesProfile: MemoryScopesProfile = MemoryScopesProfile()

    var agentName: String
    var featureName: String
    var organizationName: String
    var productName: String
}
```

### Installation

To install the AgentMemory feature in an agent, follow the pattern provided in the code sample below.

```kotlin
val agent = AIAgent(
    // Other parameters
) {
    install(AgentMemory) {
        memoryProvider = YourMemoryProvider()
        agentName = "your-agent-name"
        featureName = "your-feature-name"
        organizationName = "your-organization-name"
        productName = "your-product-name"
    }
}
```

## Error handling and edge cases

The AgentMemory feature includes several mechanisms to handle edge cases:

1. **NoMemory provider**: a default implementation that doesn't store anything, used when no memory provider is
   specified.

2. **Subject specificity handling**: when loading facts, the feature prioritizes facts from more specific subjects. 
For example, USER over ORGANIZATION.

3. **Scope filtering**: facts can be filtered by scope to ensure only relevant information is loaded.

4. **Timestamp tracking**: facts are stored with timestamps to track when they were created.

5. **Fact type handling**: the feature supports both single facts and multiple facts, with appropriate handling for each
   type.

## Examples and quickstarts

### Basic usage

```kotlin
// Create a memory provider
val memoryProvider = YourMemoryProvider()

// Install the memory feature
val agent = AIAgent(
    // Other parameters
) {
    install(AgentMemory) {
        memoryProvider = memoryProvider
        agentName = "example-agent"
    }
}

// Save facts from history
agent.run {
    withMemory {
        saveFactsFromHistory(
            concept = Concept("user-preference", "User's preferred programming language", FactType.SINGLE),
            subject = MemorySubject.USER,
            scope = MemoryScope.Agent("example-agent")
        )
    }
}

// Load facts to the agent
agent.run {
    withMemory {
        loadFactsToAgent(
            concept = Concept("user-preference", "User's preferred programming language", FactType.SINGLE)
        )
    }
}
```

### Using memory nodes

```kotlin
val strategy = strategy("example-agent") {
    stage {
        // Node to automatically detect and save facts
        val detectFacts by nodeSaveToMemoryAutoDetectFacts<Unit>(
            subjects = listOf(MemorySubject.USER, MemorySubject.PROJECT)
        )

        // Node to load specific facts
        val loadPreferences by node<Unit, Unit> {
            withMemory {
                loadFactsToAgent(
                    concept = Concept("user-preference", "User's preferred programming language", FactType.SINGLE),
                    subjects = listOf(MemorySubject.USER)
                )
            }
        }

        // Connect nodes in the strategy
        edge(nodeStart forwardTo detectFacts)
        edge(detectFacts forwardTo loadPreferences)
        edge(loadPreferences forwardTo nodeFinish)
    }
}
```

## API documentation

For a complete API reference related to the AgentMemory feature, see the reference documentation for the following packages:

- [ai.grazie.code.agents.local.memory.feature](#): Includes the `AgentMemory` class and the core implementation of the
  AI agents memory feature.
- [ai.grazie.code.agents.local.memory.feature.nodes](#): Includes predefined memory-related nodes that can be used in
  subgraphs.
- [ai.grazie.code.agents.local.memory.config](#): Provides definitions of memory scopes used for memory operations.
- [ai.grazie.code.agents.local.memory.model](#): Includes definitions of the core data structures and interfaces
  that enable agents to store, organize, and retrieve information across different contexts and time periods.
- [ai.grazie.code.agents.local.memory.feature.history](#): Provides the history compression strategy for retrieving and
  incorporating factual knowledge about specific concepts from past session activity or stored memory.
- [ai.grazie.code.agents.local.memory.providers](#): Provides the core interface that defines the fundamental operation
  for storing and retrieving knowledge in a structured, context-aware manner and its implementations.
- [ai.grazie.code.agents.local.memory.storage](#): Provides the core interface and specific implementations for file operations across different platforms and storage backends.

<!---
### `AgentMemory`

For a complete API reference related to the AgentMemory feature, follow the links below:

* Class: [AgentMemory](#)
  * Types:
    * [Config](#)
    * [Feature](#)
  * Methods: 
    * [saveFactsFromHistory](#)
    * [loadFactsToAgent](#)
    * [loadAllFactsToAgent](#)
  * Extension functions:
    * [memory](#)
    * [withMemory](#)

### `AgentMemory` nodes

`nodeSaveToMemoryAutoDetectFacts`

Creates a node that automatically detects and saves facts from the conversation history.

```kotlin
fun <I> nodeSaveToMemoryAutoDetectFacts(
    subjects: List<MemorySubject> = listOf(MemorySubject.USER)
): NodeBuilder<I, Unit>
```

| Parameter | Type                | Default | Description                          |
|-----------|---------------------|---------|--------------------------------------|
| subjects  | List<MemorySubject> | `USER`  | List of subjects to detect facts for |

## Internal helpers and utilities

`retrieveFactsFromHistory`

A helper function that retrieves facts about a concept from the LLM chat history.

```kotlin
val facts = retrieveFactsFromHistory(concept, preserveQuestionsInLLMChat)
```
-->

## FAQ and troubleshooting

### How do I implement a custom memory provider?

To implement a custom memory provider, create a class that implements the `AgentMemoryProvider` interface:

```kotlin
class MyCustomMemoryProvider : AgentMemoryProvider {
    override suspend fun save(fact: Fact, subject: MemorySubject, scope: MemoryScope) {
        // Implementation for saving facts
    }

    override suspend fun load(concept: Concept, subject: MemorySubject, scope: MemoryScope): List<Fact> {
        // Implementation for loading facts by concept
    }

    override suspend fun loadAll(subject: MemorySubject, scope: MemoryScope): List<Fact> {
        // Implementation for loading all facts
    }

    override suspend fun loadByDescription(
        description: String,
        subject: MemorySubject,
        scope: MemoryScope
    ): List<Fact> {
        // Implementation for loading facts by description
    }
}
```

### How are facts prioritized when loading from multiple subjects?

Facts are prioritized based on subject specificity. The order of specificity (from most to least specific) is:

1. MACHINE
2. USER
3. PROJECT
4. ORGANIZATION

When loading facts, if the same concept has facts from multiple subjects, the fact from the most specific subject will
be used.

### Can I store multiple values for the same concept?

Yes, by using the `MultipleFacts` type. When defining a concept, set its `factType` to `FactType.MULTIPLE`:

```kotlin
val concept = Concept(
    keyword = "user-skills",
    description = "Programming languages the user is skilled in",
    factType = FactType.MULTIPLE
)
```

This lets you store multiple values for the concept, which is retrieved as a list.
