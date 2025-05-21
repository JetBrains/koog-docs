# Available tools

Agents use tools to complete specific tasks.
The Simple API provides a set of built-in tools, but you can also implement your own custom tools if needed.

## Built-in tools

The following built-in tools are available:

| Tool      | Name              | Description                                                                                                                |
|-----------|-------------------|----------------------------------------------------------------------------------------------------------------------------|
| SayToUser | `__say_to_user__` | Lets the agent output a message to the user:<br/>- Prints the agent message to the console with the `Agent says: ` prefix. |
| AskUser   | `__ask_user__`    | Lets the agent ask the user for input:<br/>- Prints the agent message to the console.                                      |
| ExitTool  | `__exit__`        | Lets the agent finish the conversation.                                                                                    |

## Custom tools

You can create custom tools by extending the `SimpleTool` class, register them in a tool registry, and pass it to the
created agent. For more details, see [Tools](tools.md).

## Tool configuration

Tools are configured during agent creation using the `toolRegistry` parameter, which defines the tools available to the agent.
To learn more, see [Tool registry](tools.md).