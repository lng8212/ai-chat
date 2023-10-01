package com.longkd.chatgpt_openai.base.util.threadexecutor

import java.util.concurrent.Executor
import java.util.concurrent.ThreadPoolExecutor

interface IExecutor {
    fun threadPoolDownload(): ThreadPoolExecutor

    fun threadPoolApiRequest(): ThreadPoolExecutor

    fun threadPoolSyncRequest(): ThreadPoolExecutor

    fun newSingleThreadExecutor(): Executor

    fun mainTaskExecutor(): Executor

    fun newUiThreadExecutor(): Executor
}
