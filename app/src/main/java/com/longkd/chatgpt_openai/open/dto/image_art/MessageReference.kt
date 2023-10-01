

package com.longkd.chatgpt_openai.open.dto.image_art

import com.google.gson.annotations.SerializedName


data class MessageReference (

  @SerializedName("channel_id" ) var channelId : String? = null,
  @SerializedName("guild_id"   ) var guildId   : String? = null,
  @SerializedName("message_id" ) var messageId : String? = null

)