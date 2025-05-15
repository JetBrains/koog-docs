# Available tools

Agents use tools to complete specific tasks.
The Simple API provides a set of built-in tools and lets you implement your own custom tools.

## Built-in tools

The following built-in tools are available:

| Tool      | Name              | Description                                                                                                                                                                                        |
|-----------|-------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| SayToUser | `__say_to_user__` | Lets the agent output a message to the user:<br/>- Prints the agent message to the console with the `Agent says: ` prefix                                                                          |
| AskUser   | `__ask_user__`    | Lets the agent ask the user for input:<br/>- Prints the agent's message to the console.<br/>- Reads user input and returns it to the agent.<br/>This is the default tool in the `simpleChatAgent`. |
| ExitTool  | `__exit__`        | Lets the agent finish the conversation:<br/>- Used in chat agents to terminate the session.<br/>This is the default tool in the `simpleChatAgent`.                                                 |

## Custom tools

You can create custom tools by extending the `SimpleTool` class, register them in a tool registry, and pass it to the
created agent. For more details, see [Creating tools](tools.md).

## Tool usage

The tools are provided during the agent creation

The `simpleChatAgent` always uses the `AskUser` and `ExitTool` built-in tools by default, whether custom tools are provided or not.
If the custom tools are provided, these built-in tools are combined with them.

The `simpleSingleRunAgent` doesn't use any tools by default.
If none of the built-in or custom tools are provided when creating the agent, only plain text responses are received.

