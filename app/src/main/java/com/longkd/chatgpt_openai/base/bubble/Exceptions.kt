package com.longkd.chatgpt_openai.base.bubble


class PermissionDeniedException : Exception() {
    override val message: String
        get() = "display-over-other-app permission IS NOT granted!"
}

class NullViewException(private val inputMessage: String?) : Exception() {
    override val message: String?
        get() = inputMessage
}




