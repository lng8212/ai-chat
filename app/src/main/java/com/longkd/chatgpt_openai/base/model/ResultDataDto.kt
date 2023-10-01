package com.longkd.chatgpt_openai.base.model


sealed class ResultDataDto {
    class Success(val result: String, var mIsCallMore : Boolean? = false) : ResultDataDto()
    class Error(val errorType: ErrorType) : ResultDataDto()
    class SuccessImage(val url: String) : ResultDataDto()
    class SuccessImageVyro(val data: ByteArray) : ResultDataDto()
    class SuccessTopic(val result: TopicResponse) : ResultDataDto()
}
