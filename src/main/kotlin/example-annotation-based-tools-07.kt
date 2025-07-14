// This file was automatically generated from annotation-based-tools.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleAnnotationBasedTools07

import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet

class MyFirstToolSet : ToolSet {
    @Tool
    fun getWeather(location: String): String {
        // In a real implementation, you would call a weather API
        return "The weather in $location is sunny and 72°F"
    }
}
