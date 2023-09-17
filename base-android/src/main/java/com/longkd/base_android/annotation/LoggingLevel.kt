package com.longkd.base_android.annotation

/**
 * @Author: longkd
 * @Since: 11:49 - 13/08/2023
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class LoggingLever(val level: Level)