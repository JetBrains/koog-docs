# Memory

## Feature overview

The Memory feature is a component of the Kotlin Agentic Framework that lets AI agents store, retrieve, and use
information across conversations.

### Purpose

The Memory Feature addresses the challenge of maintaining context in AI agent interactions by:

- Storing important facts extracted from conversations.
- Organizing information by concepts, subjects, and scopes.
- Retrieving relevant information when needed in future interactions.
- Enabling personalization based on user preferences and history.

### Architecture

The Memory feature is built on a hierarchical structure:

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

### Class: `AgentMemory.Config`

Configuration class for the Memory feature.

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

To install the Memory feature in an agent:

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

## API documentation

### Class: `AgentMemory`

The main class that implements memory capabilities for a AIAgent.

#### Constructor

```kotlin
class AgentMemory(
    val agentMemory: AgentMemoryProvider,
    val llm: AIAgentLLMContext,
    val scopesProfile: MemoryScopesProfile
)
```

| Parameter     | Type                 | Description                                  |
|---------------|----------------------|----------------------------------------------|
| agentMemory   | AgentMemoryProvider  | Provider for storing and retrieving facts    |
| llm           | AIAgentLLMContext | Context for interacting with the LLM         |
| scopesProfile | MemoryScopesProfile  | Profile defining the available memory scopes |

Please note that `agentMemory` can have a custom value or use one of the existing implementations: `NoMemory`,
`LocalFileMemoryProvider`, `SharedRemoteMemoryProvider`.

#### Methods

`saveFactsFromHistory`

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

`loadFactsToAgent`

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

`loadAllFactsToAgent`

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

### Extension functions

`memory`

An extension function to access the memory feature from a AIAgentStageContext.

```kotlin
fun AIAgentStageContext.memory(): AgentMemory
```

`withMemory`

Extension function to perform an action with the memory feature.

```kotlin
suspend fun <T> AIAgentStageContext.withMemory(action: suspend AgentMemory.() -> T)
```

### Memory nodes

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

## Error handling and edge cases

The Memory feature includes several mechanisms to handle edge cases:

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
```

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
