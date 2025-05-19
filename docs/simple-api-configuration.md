# Configuration options

If your goal is to build a simple agent to experiment with, you can provide only an API key and coroutine scope when
creating it.
But if you want more flexibility and customization, you can pass optional parameters to configure the agent.

Both the `simpleChatAgent` and `simpleSingleRunAgent` accept the following parameters:

| Parameter name    | Data type                         | Default                   | Required | Description                                                                                                        |
|-------------------|-----------------------------------|---------------------------|----------|--------------------------------------------------------------------------------------------------------------------|
| `executor`        | PromptExecutor                    |                           | Yes      | Prompt executor that would connect to LLM(s) and execute prompts with messages                                     |
| `systemPrompt`    | String                            |                           | No       | The system instruction to guide the agent behavior. By default, the empty string is passed.                        |
| `llmModel`        | LLModel                           | `OpenAIModels.Chat.GPT4o` | No       | The specific LLM to use.                                                                                           |
| `temperature`     | Double                            | `1.0`                     | No       | The temperature for LLM output generation.                                                                         |
| `maxIterations`   | Int                               | `50`                      | No       | The maximum number of steps an agent can take before it is forced to stop.                                         |
| `installFeatures` | AIAgent.FeatureContext.() -> Unit | {}                        | No       | Additional features (ex: EventHandler, Tracing, AgentMemory) with their configurations, extending agent's behavior |

