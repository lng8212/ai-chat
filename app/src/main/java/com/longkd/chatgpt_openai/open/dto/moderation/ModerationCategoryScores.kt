

package com.longkd.chatgpt_openai.open.dto.moderation

import com.google.gson.annotations.SerializedName

/**
 * An object containing the scores for each moderation category
 *
 * https://beta.openai.com/docs/api-reference/moderations/create
 */
class ModerationCategoryScores {
    var hate = 0.0

    @SerializedName("hate/threatening")
    var hateThreatening = 0.0

    @SerializedName("self-harm")
    var selfHarm = 0.0
    var sexual = 0.0

    @SerializedName("sexual/minors")
    var sexualMinors = 0.0
    var violence = 0.0

    @SerializedName("violence/graphic")
    var violenceGraphic = 0.0
}