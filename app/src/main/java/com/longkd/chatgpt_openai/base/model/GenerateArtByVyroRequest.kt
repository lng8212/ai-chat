package com.longkd.chatgpt_openai.base.model

import com.google.gson.annotations.SerializedName

data class GenerateArtByVyroRequest(
    /**
     * | [prompt] | String | The text that guides the image generation. | - | - | No |
     * | [aspectRatio] | String | The aspect ratio of the generated image. | '1:1' | 1:1, 3:2, 4:3, 3:4, 16:9, 9:16 | Yes |
     * | [modelId] | Integer | Determines the image style. | 27 | 21, 26, 29, 27, 28, 30, 31, 32, 33, 34 | Yes |
     * | [negativePrompt] | String | Text that guides the generator away from specified results. | ' ' | - | Yes |
     * | [cfg] | Float | Configures the generator's function. | 7.5 | 3-15 | Yes |
     * | [seed] | Integer | The seed value for the random number generator. | - | - | Yes |
     * | [steps] | Integer | The number of operations of the generator. | - | 30-50 | Yes |
     * | [highResResults] | Integer | Flag to request high-resolution results. | 0 | 0/1 | Yes |
     */
    @SerializedName("prompt") var prompt: String,
    @SerializedName("aspect_ratio") var aspectRatio: String? = null,
    @SerializedName("model_id") var modelId: Int = 32,
    @SerializedName("style_id") var styleId: Int = 0,
    @SerializedName("negative_prompt") var negativePrompt: String? = null,
    @SerializedName("cfg") var cfg: Float? = null,
    @SerializedName("seed") var seed: Int? = null,
    @SerializedName("steps") var steps: Int? = null,
    @SerializedName("high_res_results") var highResResults: Int? = null
)
