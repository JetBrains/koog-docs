# Configuration options

If your goal is to build a simple agent to experiment with, you can provide only an API key when creating it.
But if you want more flexibility and customization, you can pass optional parameters to configure the agent.

`simpleSingleRunAgent` accept the following parameters:

| Parameter name    | Data type    | Default                   | Required | Description                                                                                                                                                                                           |
|-------------------|--------------|---------------------------|----------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `executor`        | String       |                           | Yes      | The prompt executor that connects to LLM and runs prompts with messages.                                                                                                                              |
| `systemPrompt`    | String       |                           | No       | The system instruction to guide the agent behavior. By default, the empty string is passed.                                                                                                           |
| `llmModel`        | LLModel      |                           | No       | The specific LLM to use.                                                                                                                                                                              |
| `temperature`     | Double       | `1.0`                     | No       | The temperature for LLM output generation.                                                                                                                                                            |
| `toolRegistry`    | ToolRegistry |                           | No       | The list of built-in and custom tools your agent can use. For a chat agent, the list includes the built-in `AskUser` and `ExitTool` tools by default. For a single-run agent, it is empty by default. |
| `maxIterations`   | Int          | `50`                      | No       | The maximum number of steps an agent can take before it is forced to stop.                                                                                                                            |
| `installFeatures` |              |                           | No       | Additional features with their configurations that extend the agent behavior.                                                                                                                         |

