// This file was automatically generated from content-moderation.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleContentModeration06

import ai.koog.prompt.dsl.ModerationCategory
import ai.koog.prompt.dsl.ModerationCategoryResult
import ai.koog.prompt.dsl.ModerationResult

val result =

ModerationResult(
    isHarmful = false,
    categories = mapOf(
        ModerationCategory.Harassment to ModerationCategoryResult(false, confidenceScore = 0.0001),
        ModerationCategory.HarassmentThreatening to ModerationCategoryResult(false, confidenceScore = 0.0001),
        ModerationCategory.Hate to ModerationCategoryResult(false, confidenceScore = 0.0001),
        ModerationCategory.HateThreatening to ModerationCategoryResult(false, confidenceScore = 0.0001),
        ModerationCategory.Sexual to ModerationCategoryResult(false, confidenceScore = 0.0001),
        ModerationCategory.SexualMinors to ModerationCategoryResult(false, confidenceScore = 0.0001),
        ModerationCategory.Violence to ModerationCategoryResult(false, confidenceScore = 0.0001),
        ModerationCategory.ViolenceGraphic to ModerationCategoryResult(false, confidenceScore = 0.0001),
        ModerationCategory.SelfHarm to ModerationCategoryResult(false, confidenceScore = 0.0001),
        ModerationCategory.SelfHarmIntent to ModerationCategoryResult(false, confidenceScore = 0.0001),
        ModerationCategory.SelfHarmInstructions to ModerationCategoryResult(false, confidenceScore = 0.0001),
        ModerationCategory.Illicit to ModerationCategoryResult(false, confidenceScore = 0.0001),
        ModerationCategory.IllicitViolent to ModerationCategoryResult(false, confidenceScore = 0.0001),
    )
)
