package com.longkd.base_android.data.api

/**
 * @Author: longkd
 * @Since: 12:50 - 13/08/2023
 */
inline fun <reified S> api(block: ApiServiceBuilder<S>.() -> Unit): S =
    ApiServiceBuilder(S::class.java).getConfigFromAnnotation().apply(block).build()

inline fun <reified S> ApiService(): S =
    ApiServiceBuilder(S::class.java).getConfigFromAnnotation().build()
