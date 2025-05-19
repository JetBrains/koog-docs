# Key concepts

- **Agent**: a Kotlin-based AI entity that can interact with tools, handle complex workflows, and communicate with
  users. The framework offers two main approaches: the Simple API and AI Agent.

- **Concept** (Memory): a category of information with associated metadata in the Memory feature, including a keyword,
  description, and fact type. Concepts are fundamental building blocks of the agent memory system that the agent can remember and recall.
  To learn more, see [Memory](memory.md).

- **Context**: the environment in which LLM interactions occur, with access to the conversation history and
  tools.

- **Edge**: a connection between nodes in an agent graph that defines the flow of operations, often with conditions
  that specify when to follow each edge.

- **Event handler**: a component that processes events generated during the operation of an agent, such as tool calls, LLM
  responses, and errors.

- **Fact** (Memory): an individual piece of information stored in the agent memory system.
  Facts are associated with concepts and can either have a single value or multiple values.
  To learn more, see [Memory](memory.md).

- **Feature**: a component that extends and enhances the functionality of AI agents.

- **Graph**: a structure of nodes connected by edges that defines an agent strategy workflow.

- **History compression**: the process of reducing the size of the conversation history to manage token usage by applying various compression strategies.
  To learn more, see [History compression](history-compression.md).

- **LLM (Large Language Model)**: the underlying AI model that powers agent capabilities.

- **Memory**: a feature that enables AI agents to store, retrieve, and use information across conversations. To learn more, see [Memory](memory.md).

- **Memory scope**: the context in which facts are relevant. To learn more, see [Memory](memory.md).

- **Message**: a unit of communication in the agent system that represents data passed from a user, assistant, or system.

- **Node**: a fundamental building block of an agent strategy workflow that represents a specific operation or transformation.

- **Prompt**: the conversation history provided to an LLM that consists of messages from a user, assistant, and system.

- **Session**: a context for interacting with an LLM that includes the conversation history, available tools,
  and methods to make requests.

- **Subgraph**: a self-contained unit of processing within an agent strategy, with its own set of tools, context, and
  responsibilities. Information about subgraph operations can be either encapsulated within the subgraph or transferred between
  subgraphs using the Memory feature.

- **Strategy**: a defined workflow for an agent that consists of sequential subgraphs.
  The strategy defines how the agent processes input, interacts with tools, and generates output.
  A strategy graph consists of nodes connected by edges that represent transitions between nodes.

- **System prompt**: instructions provided to an agent to guide its behavior and define its role. The instructions can also provide context for the agent.

- **Tool**: a function that an agent can use to perform specific tasks or access external systems. The agent is aware of the
  available tools and their arguments but lacks knowledge of their implementation details.

- **Tool call**: a request from an LLM to run a specific tool using the provided arguments. It functions similarly to a function call.

- **Tool descriptor**: tool metadata that includes its name, description, and parameters.

- **Tool registry**: a list of tools available to an agent. The registry informs the agent about the available tools.

- **Tool result**: an output produced by running a tool. For example, if the tool is a method, the result would be its return value.

