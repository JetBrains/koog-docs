## Overview

Koog provides a way to store and pass data using `AIAgentStorage`, which is a key-value storage system designed as a 
type-safe way to pass data between different nodes or even subgraphs.

The storage is accessible through the `storage` property (`storage: AIAgentStorage`) available in agent nodes, allowing 
for seamless data sharing across different components of your AI agent system.

## Key and value structure

The key-value data storage structure relies on the `AIAgentStorageKey` data class. For more information about 
`AIAgentStorageKey`, see the sections below.

### AIAgentStorageKey

The storage uses a typed key system to ensure type safety when storing and retrieving data:

- `AIAgentStorageKey<T>`: A data class that represents a storage key used for identifying and accessing data. Here are
the key features of the `AIAgentStorageKey` class:
    - The generic type parameter `T` specifies the type of data associated with this key, ensuring type safety.
    - Each key has a `name` property which is a string identifier that uniquely represents the storage key.

### Creating storage keys

To create a storage key, use the `createStorageKey` function:

```kotlin
public inline fun <reified T : Any> createStorageKey(name: String): AIAgentStorageKey<T>
```

The function takes the following parameter:

| Name   | Data type | Required | Description                                                     |
|--------|-----------|----------|-----------------------------------------------------------------|
| `name` | String    | Yes      | The string identifier that uniquely represents the storage key. |

This function creates a storage key for a specific type, allowing identification and retrieval of values associated with
it. For more information on how to define the data structure and create a storage based on it, see 
[Usage examples](#usage-examples).

## Working with storage keys

The following sections list the methods for different operations you can perform with storage keys.

### set

Sets the value associated with the given key in the storage.

```kotlin
public suspend fun <T : Any> set(key: AIAgentStorageKey<T>, value: T): Unit
```

Here is a reference of parameters for the `set` method:

| Name  | Data type            | Required | Description                                       |
|-------|----------------------|----------|---------------------------------------------------|
| key   | AIAgentStorageKey<T> | Yes      | The key used to identify the value in the storage |
| value | T                    | Yes      | The value to be associated with the key           |

### get

Retrieves the value associated with the given key from the storage.

```kotlin
public suspend fun <T : Any> get(key: AIAgentStorageKey<T>): T?
```
Here is the parameter reference for the `get` method:

| Name | Data type            | Required | Description                                       |
|------|----------------------|----------|---------------------------------------------------|
| key  | AIAgentStorageKey<T> | Yes      | The key used to identify the value in the storage |

The method returns the value associated with the key, cast to type T, or null if the key does not exist.

### getValue

Retrieves the non-null value associated with the given key from the storage.

```kotlin
public suspend fun <T : Any> getValue(key: AIAgentStorageKey<T>): T
```

Here is the parameter reference for the `getValue` method:

| Name | Data type            | Required | Description                                       |
|------|----------------------|----------|---------------------------------------------------|
| key  | AIAgentStorageKey<T> | Yes      | The key used to identify the value in the storage |

Returns the value associated with the key, of type T.

Throws a `NoSuchElementException` if the key does not exist in the storage.

### remove

Removes the value associated with the given key from the storage.

```kotlin
public suspend fun <T : Any> remove(key: AIAgentStorageKey<T>): T?
```

Here is the parameter reference for the `remove` method:

| Name | Data type            | Required | Description                                       |
|------|----------------------|----------|---------------------------------------------------|
| key  | AIAgentStorageKey<T> | Yes      | The key used to identify the value in the storage |

The method returns the value associated with the key, cast to type T, or null if the key does not exist.

### toMap

Converts the storage to a map representation.

```kotlin
public suspend fun toMap(): Map<AIAgentStorageKey<*>, Any>
```

Returns a map containing all key-value pairs currently stored in the system, where keys are of type `AIAgentStorageKey` 
and values are of type `Any`.

### putAll

Adds all key-value pairs from the given map to the storage.

```kotlin
public suspend fun putAll(map: Map<AIAgentStorageKey<*>, Any>): Unit
```

Here is the parameter reference for the `putAll` method:

| Name | Data type                      | Required | Description                                                                  |
|------|--------------------------------|----------|------------------------------------------------------------------------------|
| map  | Map<AIAgentStorageKey<*>, Any> | Yes      | A map containing keys and their associated values to be added to the storage |

### clear

Clears all data from the storage.

```kotlin
public suspend fun clear(): Unit
```

## Usage examples

The following sections provide an actual example of creating a storage key and using it to store and retrieve data. 

### Defining a class

The first step in storing data that you want to pass is creating a class that represents your data. Here is an example 
of a simple class with basic user data: 

```kotlin
class UserData(
   val name: String,
   val age: Int
)
```

Once defined, use the class to create a storage key as described below.

### Creating a storage key

Create a typed storage key for the defined data structure:

```kotlin
val userDataKey = createStorageKey<UserData>("user-data")
```

The `createStorageKey` function takes a single string parameter that uniquely identifies the key. For more information
and the parameter reference, see [Creating storage keys](#creating-storage-keys).

### Storing data

To save data using a created storage key, use the `storage.set` method in a node:

```kotlin
val nodeSaveData by node<Unit, Unit> {
    storage.set(userDataKey, UserData("John", 26))
}
```

### Retrieving data

To retrieve the data, use the `storage.get` method in a node: 

```kotlin
val node by node<String, Unit> { message ->
    storage.get(userDataKey)?.let { userFromStorage ->
        println("Hello dear $userFromStorage, here's a message for you: $message")
    }
}
```

## Additional information

- AIAgentStorage is thread-safe, using a Mutex to ensure concurrent access is handled properly.
- The storage is designed to work with any type that extends `Any`.
- When retrieving values, type casting is handled automatically, ensuring type safety throughout your application.
- For non-nullable access to values, use the `getValue` method which throws an exception if the key doesn't exist.
- The storage can be cleared entirely using the `clear` method, which removes all stored key-value pairs.