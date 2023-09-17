package com.longkd.base_android.annotation

/**
 * @Author: longkd
 * @Since: 11:30 - 13/08/2023
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Timeout(val read: Long = 10_000L, val write: Long = 10_000L)