// This file was automatically generated from content-moderation.md by Knit tool. Do not edit.
package ai.koog.agents.example.exampleContentModeration07

import ai.koog.prompt.dsl.ModerationCategory
import ai.koog.prompt.dsl.ModerationCategoryResult
import ai.koog.prompt.dsl.ModerationResult

val result =

ModerationResult(
    isHarmful = true,
    categories = mapOf(
        ModerationCategory.Harassment to ModerationCategoryResult(false),
        ModerationCategory.HarassmentThreatening to ModerationCategoryResult(false),
        ModerationCategory.Hate to ModerationCategoryResult(true),    // from S10
        ModerationCategory.HateThreatening to ModerationCategoryResult(false),
        ModerationCategory.Sexual to ModerationCategoryResult(false),
        ModerationCategory.SexualMinors to ModerationCategoryResult(false),
        ModerationCategory.Violence to ModerationCategoryResult(false),
        ModerationCategory.ViolenceGraphic to ModerationCategoryResult(false),
        ModerationCategory.SelfHarm to ModerationCategoryResult(false),
        ModerationCategory.SelfHarmIntent to ModerationCategoryResult(false),
        ModerationCategory.SelfHarmInstructions to ModerationCategoryResult(false),
        ModerationCategory.Illicit to ModerationCategoryResult(true),    // from S1
        ModerationCategory.IllicitViolent to ModerationCategoryResult(true),    // from S1
    )
)
