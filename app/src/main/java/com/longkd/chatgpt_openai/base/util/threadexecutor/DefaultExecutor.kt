package com.longkd.chatgpt_openai.base.util.threadexecutor

import java.util.concurrent.*

class DefaultExecutor private constructor() : IExecutor {
    companion object {
        private const val MAX_POOL_SIZE = 5
        private const val CORE_POOL_SIZE = 3
        private const val TIME_A_LIVE = 5
        private var sInstance: DefaultExecutor? = null
        var NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()
        var KEEP_ALIVE_TIME = 1
        var KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS

        @Synchronized
        fun getInstance(): DefaultExecutor? {
            return if (sInstance == null) {
                sInstance = DefaultExecutor()
                sInstance
            } else sInstance
        }
    }

    var taskQueue: BlockingQueue<Runnable> = LinkedBlockingQueue<Runnable>()

    private val mMainExecutor: Executor by lazy { MainThreadExecutor() }

    private val mThreadPoolDownloadExecutor: ThreadPoolExecutor by lazy {
        ThreadPoolExecutor(
            CORE_POOL_SIZE, MAX_POOL_SIZE, TIME_A_LIVE.toLong(),
            TimeUnit.SECONDS, LinkedBlockingQueue<Runnable>()
        )
    }

    private val mThreadPoolAPIExecutor: ThreadPoolExecutor by lazy {
        ThreadPoolExecutor(
            CORE_POOL_SIZE, MAX_POOL_SIZE, TIME_A_LIVE.toLong(),
            TimeUnit.SECONDS, LinkedBlockingQueue<Runnable>()
        )
    }

    private val mThreadPoolSyncEffectExecutor: ThreadPoolExecutor by lazy {
        ThreadPoolExecutor(
            CORE_POOL_SIZE, MAX_POOL_SIZE, TIME_A_LIVE.toLong(),
            TimeUnit.SECONDS, LinkedBlockingQueue<Runnable>()
        )
    }
    private val mThreadPoolUiExecutor: ThreadPoolExecutor by lazy {
        ThreadPoolExecutor(
            NUMBER_OF_CORES,
            NUMBER_OF_CORES * 2,
            KEEP_ALIVE_TIME.toLong(),
            KEEP_ALIVE_TIME_UNIT,
            taskQueue, BackgroundThreadFactory()
        )
    }


    override fun threadPoolDownload(): ThreadPoolExecutor = mThreadPoolDownloadExecutor

    override fun threadPoolApiRequest(): ThreadPoolExecutor = mThreadPoolAPIExecutor

    override fun threadPoolSyncRequest(): ThreadPoolExecutor = mThreadPoolSyncEffectExecutor

    override fun newSingleThreadExecutor(): Executor = Executors.newSingleThreadExecutor()

    override fun newUiThreadExecutor(): Executor = mThreadPoolUiExecutor

    override fun mainTaskExecutor(): Executor = mMainExecutor

    fun executorThread(f: () -> Unit) {
        newSingleThreadExecutor().execute(f)
    }
}
