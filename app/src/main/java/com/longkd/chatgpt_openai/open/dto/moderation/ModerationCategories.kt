

package com.longkd.chatgpt_openai.open.dto.moderation

import com.google.gson.annotations.SerializedName


/**
 * An object containing the flags for each moderation category
 *
 * https://beta.openai.com/docs/api-reference/moderations/create
 */
class ModerationCategories {
    var hate = false

    @SerializedName("hate/threatening")
    var hateThreatening = false

    @SerializedName("self-harm")
    var selfHarm = false
    var sexual = false

    @SerializedName("sexual/minors")
    var sexualMinors = false
    var violence = false

    @SerializedName("violence/graphic")
    var violenceGraphic = false
}