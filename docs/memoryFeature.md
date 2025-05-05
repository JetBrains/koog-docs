# Memory Feature Documentation

## Feature Overview

The Memory Feature is a component of the Code Engine platform that enables AI agents to store, retrieve, and utilize
information across conversations.

### Purpose

The Memory Feature addresses the challenge of maintaining context in AI agent interactions by:

- Storing important facts extracted from conversations
- Organizing information by concepts, subjects, and scopes
- Retrieving relevant information when needed in future interactions
- Enabling personalization based on user preferences and history

### Architecture

The Memory Feature is built on a hierarchical structure:

**Facts**: Individual pieces of information stored in memory

- **SingleFact**: A single value associated with a concept
- **MultipleFacts**: Multiple values associated with a concept

**Concepts**: Categories of information with associated metadata

- Keyword: Unique identifier for the concept
- Description: Detailed explanation of what the concept represents
- FactType: Whether the concept stores single or multiple facts

**Subjects**: Entities that facts can be associated with

- USER: Facts about the user
- MACHINE: Facts about the local machine
- PROJECT: Facts about the current project
- ORGANIZATION: Facts about the organization

**Scopes**: Contexts in which facts are relevant

- Agent: Specific to an agent
- Feature: Specific to a feature
- Product: Specific to a product
- Organization: Relevant across an organization
- CrossProduct: Relevant across multiple products

## Configuration & Initialization

The feature integrates with the agent pipeline through the `MemoryFeature` class, which provides methods for saving and
loading facts, and can be installed as a feature in the agent configuration.

### MemoryFeature.Config

Configuration class for the Memory Feature.

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

To install the Memory Feature in an agent:

```kotlin
val agent = KotlinAIAgent(
    // other parameters...
) {
    install(MemoryFeature) {
        memoryProvider = YourMemoryProvider()
        agentName = "your-agent-name"
        featureName = "your-feature-name"
        organizationName = "your-organization-name"
        productName = "your-product-name"
    }
}
```

## Public API Documentation

### MemoryFeature

Main class that implements memory capabilities for a LocalAIAgent.

#### Constructor

```kotlin
class MemoryFeature(
    val agentMemory: AgentMemoryProvider,
    val llm: LocalAgentLLMContext,
    val scopesProfile: MemoryScopesProfile
)
```

| Parameter     | Type                 | Description                                  |
|---------------|----------------------|----------------------------------------------|
| agentMemory   | AgentMemoryProvider  | Provider for storing and retrieving facts    |
| llm           | LocalAgentLLMContext | Context for interacting with the LLM         |
| scopesProfile | MemoryScopesProfile  | Profile defining the available memory scopes |

Please note that `agentMemory`  can have a custom value or use one of the existing implementations: `NoMemory`,
`LocalFileMemoryProvider`, `SharedRemoteMemoryProvider`.

#### Methods

##### `saveFactsFromHistory`

Extracts facts about a specific concept from the LLM chat history and saves them to memory.

```kotlin
suspend fun saveFactsFromHistory(
    concept: Concept,
    subject: MemorySubject,
    scope: MemoryScope,
    preserveQuestionsInLLMChat: Boolean = false
)
```

| Parameter                  | Type          | Default  | Description                                                     |
|----------------------------|---------------|----------|-----------------------------------------------------------------|
| concept                    | Concept       | required | The concept to extract facts about                              |
| subject                    | MemorySubject | required | The subject scope for the facts                                 |
| scope                      | MemoryScope   | required | The memory scope for the facts                                  |
| preserveQuestionsInLLMChat | Boolean       | false    | If true, keeps the fact extraction messages in the chat history |

##### `loadFactsToAgent`

Loads facts about a specific concept from memory and adds them to the LLM chat history.

```kotlin
suspend fun loadFactsToAgent(
    concept: Concept,
    scopes: List<MemoryScopeType> = MemoryScopeType.entries,
    subjects: List<MemorySubject> = MemorySubject.entries,
)
```

| Parameter | Type                  | Default      | Description                        |
|-----------|-----------------------|--------------|------------------------------------|
| concept   | Concept               | required     | The concept to load facts about    |
| scopes    | List<MemoryScopeType> | all scopes   | List of memory scopes to search in |
| subjects  | List<MemorySubject>   | all subjects | List of subjects to look for       |

##### `loadAllFactsToAgent`

Loads all available facts from memory and adds them to the LLM chat history.

```kotlin
suspend fun loadAllFactsToAgent(
    scopes: List<MemoryScopeType> = MemoryScopeType.entries,
    subjects: List<MemorySubject> = MemorySubject.entries,
)
```

| Parameter | Type                  | Default      | Description                        |
|-----------|-----------------------|--------------|------------------------------------|
| scopes    | List<MemoryScopeType> | all scopes   | List of memory scopes to search in |
| subjects  | List<MemorySubject>   | all subjects | List of subjects to look for       |

### Extension Functions

#### memory

Extension function to access the memory feature from a LocalAgentStageContext.

```kotlin
fun LocalAgentStageContext.memory(): MemoryFeature
```

#### withMemory

Extension function to perform an action with the memory feature.

```kotlin
suspend fun <T> LocalAgentStageContext.withMemory(action: suspend MemoryFeature.() -> T)
```

### Memory Nodes

#### nodeSaveToMemoryAutoDetectFacts

Creates a node that automatically detects and saves facts from the conversation history.

```kotlin
fun <I> nodeSaveToMemoryAutoDetectFacts(
    subjects: List<MemorySubject> = listOf(MemorySubject.USER)
): NodeBuilder<I, Unit>
```

| Parameter | Type                | Default | Description                          |
|-----------|---------------------|---------|--------------------------------------|
| subjects  | List<MemorySubject> | [USER]  | List of subjects to detect facts for |

## Internal Helpers & Utilities

### retrieveFactsFromHistory

Helper function that retrieves facts about a concept from the LLM chat history.

```kotlin
val facts = retrieveFactsFromHistory(concept, preserveQuestionsInLLMChat)
```

## Error Handling & Edge Cases

The Memory Feature includes several mechanisms to handle edge cases:

1. **NoMemory Provider**: A default implementation that doesn't store anything, used when no memory provider is
   specified.

2. **Subject Specificity Handling**: When loading facts, the feature prioritizes facts from more specific subjects (
   e.g., USER over ORGANIZATION).

3. **Scope Filtering**: Facts can be filtered by scope to ensure only relevant information is loaded.

4. **Timestamp Tracking**: Facts are stored with timestamps to track when they were created.

5. **Fact Type Handling**: The feature supports both single facts and multiple facts, with appropriate handling for each
   type.

## Examples & Quickstarts

### Basic Usage

```kotlin
// Create a memory provider
val memoryProvider = YourMemoryProvider()

// Install the memory feature
val agent = KotlinAIAgent(
    // other parameters...
) {
    install(MemoryFeature) {
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

// Load facts to agent
agent.run {
    withMemory {
        loadFactsToAgent(
            concept = Concept("user-preference", "User's preferred programming language", FactType.SINGLE)
        )
    }
}
```

### Using Memory Nodes

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

## FAQ / Troubleshooting

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

This will allow storing multiple values for the concept, which will be retrieved as a list.