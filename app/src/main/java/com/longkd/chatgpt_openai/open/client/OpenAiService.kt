package com.longkd.chatgpt_openai.open.client

import com.longkd.chatgpt_openai.base.model.*
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.Constants
import com.longkd.chatgpt_openai.open.dto.DeleteResult
import com.longkd.chatgpt_openai.open.dto.OpenAiResponse
import com.longkd.chatgpt_openai.open.dto.completion.*
import com.longkd.chatgpt_openai.open.dto.edit.EditRequest
import com.longkd.chatgpt_openai.open.dto.edit.EditResult
import com.longkd.chatgpt_openai.open.dto.embedding.EmbeddingRequest
import com.longkd.chatgpt_openai.open.dto.embedding.EmbeddingResult
import com.longkd.chatgpt_openai.open.dto.engine.Engine
import com.longkd.chatgpt_openai.open.dto.file.File
import com.longkd.chatgpt_openai.open.dto.finetune.FineTuneEvent
import com.longkd.chatgpt_openai.open.dto.finetune.FineTuneRequest
import com.longkd.chatgpt_openai.open.dto.finetune.FineTuneResult
import com.longkd.chatgpt_openai.open.dto.generate.GenerateArtRequest
import com.longkd.chatgpt_openai.open.dto.generate.GenerateArtResult
import com.longkd.chatgpt_openai.open.dto.image.CreateImageEditRequest
import com.longkd.chatgpt_openai.open.dto.image.CreateImageRequest
import com.longkd.chatgpt_openai.open.dto.image.CreateImageVariationRequest
import com.longkd.chatgpt_openai.open.dto.image.ImageResult
import com.longkd.chatgpt_openai.open.dto.image_art.ImageArtRequest
import com.longkd.chatgpt_openai.open.dto.image_art.ImageArtResult
import com.longkd.chatgpt_openai.open.dto.model.Model
import com.longkd.chatgpt_openai.open.dto.moderation.*
import com.google.android.gms.common.annotation.KeepName
import com.longkd.chatgpt_openai.base.model.GenerateArtByVyroRequest
import com.longkd.chatgpt_openai.base.model.GenerateArtResponse
import com.longkd.chatgpt_openai.base.model.ImageStyleResponse
import com.longkd.chatgpt_openai.base.model.SummaryFileResponse
import com.longkd.chatgpt_openai.base.model.TopicResponse
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import java.util.concurrent.TimeUnit

class OpenAiService {
    val api: OpenAiApi
    val mToken = ""

    /**
     * Creates a new OpenAiService that wraps OpenAiApi
     *
     * @param token   OpenAi token string "sk-XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
     * @param timeout http read timeout in seconds, 0 means no timeout
     */
    @Deprecated("use {@link OpenAiService(String, Duration)}")
    constructor(token: String, timeout: Long,
                type:Int) : this(
        token,
        BASE_URL,
        timeout,
        type
    ) {
    }

    /**
     * Creates a new OpenAiService that wraps OpenAiApi
     *
     * @param token   OpenAi token string "sk-XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
     * @param timeout http read timeout, Duration.ZERO means no timeout
     */
    /**
     * Creates a new OpenAiService that wraps OpenAiApi
     *
     * @param token   OpenAi token string "sk-XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
     * @param timeout http read timeout, Duration.ZERO means no timeout
     */
    /**
     * Creates a new OpenAiService that wraps OpenAiApi
     *
     * @param token OpenAi token string "sk-XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
     */
    constructor(
        token: String,
        baseUrl: String = BASE_URL,
        timeout: Long = 10,
        type:Int,
        apiVyro: Boolean = false
    ) {
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthenticationInterceptor(token, type))
            .connectionPool(ConnectionPool(5, 1, TimeUnit.SECONDS))
            .readTimeout(timeout, TimeUnit.SECONDS)
            .connectTimeout(timeout * 2, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(if (!apiVyro) baseUrl else BASE_URL_ART)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        api = retrofit.create(OpenAiApi::class.java)
    }

    constructor(timeout: Long) {
        val client: OkHttpClient = OkHttpClient.Builder()
            .connectionPool(ConnectionPool(5, 1, TimeUnit.SECONDS))
            .readTimeout(timeout, TimeUnit.SECONDS)
            .connectTimeout(timeout * 2, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_IMAGE)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        api = retrofit.create(OpenAiApi::class.java)
    }


    /**
     * Creates a new OpenAiService that wraps OpenAiApi
     *
     * @param api OpenAiApi instance to use for all methods
     */
    constructor(api: OpenAiApi) {
        this.api = api
    }

    fun listModels(): List<Model> {
        return api.listModels()?.blockingGet()?.data ?: listOf()
    }

    fun getModel(modelId: String?): Model? {
        return api.getModel(modelId).blockingGet()
    }

    fun generateImageArt(request: GenerateArtRequest?): GenerateArtResult? {
        return api.generateImageArt(request).blockingGet()
    }
    fun createImageArt(request: ImageArtRequest?): ImageArtResult? {
        return api.createImageArt(request).blockingGet()
    }

    fun createCompletion(request: CompletionRequest?): CompletionResult? {
        return api.createCompletionNew(request).blockingGet()
    }

    fun createCompletion35(request: Completion35Request?): Completion35Result? {
        return if (CommonSharedPreferences.getInstance().modelChatGpt == Constants.MODEL_CHAT.GPT_3_5) {
            api.createCompletionV1Chat(request).blockingGet()
        } else {
            api.createCompletionV1ChatGPT4(request).blockingGet()
        }
    }

    fun generateArtByVyro(request: GenerateArtByVyroRequest?): GenerateArtResponse? {
        return api.generateArtByVyro(request)?.blockingGet()
    }

    fun completeSummaryChat(request: Completion35Request?): Completion35Result? {
        return api.completeSummaryChat(request).blockingGet()
    }

    fun completeTopicChat(request: Completion35Request?): TopicResponse? {
        return api.completeTopicChat(request).blockingGet()
    }

    fun uploadSummaryText(request: Completion35Request?): SummaryFileResponse? {
        return api.uploadSummaryText(request).blockingGet()
    }

    fun uploadSummaryFile(filepath: String): SummaryFileResponse? {
        val mFile = java.io.File(filepath)
        val fileBody = RequestBody.create("application/pdf".toMediaTypeOrNull(), mFile)
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", mFile.name, fileBody)
        return api.uploadSummaryFile(body).blockingGet()
    }
    /**
     * Use [OpenAiService.createCompletion] and [CompletionRequest.model]instead
     */
    @Deprecated("")
    fun createCompletion(engineId: String?, request: CompletionRequest?): CompletionResult? {
        return api.createCompletion(engineId, request).blockingGet()
    }

    fun createEdit(request: EditRequest?): EditResult? {
        return api.createEdit(request).blockingGet()
    }

    /**
     * Use [OpenAiService.createEdit] and [EditRequest.model]instead
     */
    @Deprecated("")
    fun createEdit(engineId: String?, request: EditRequest?): EditResult? {
        return api.createEdit(engineId, request).blockingGet()
    }

    fun createEmbeddings(request: EmbeddingRequest?): EmbeddingResult? {
        return api.createEmbeddings(request).blockingGet()
    }


    /**
     * Use [OpenAiService.createEmbeddings] and [EmbeddingRequest.model]instead
     */
    @Deprecated("")
    fun createEmbeddings(engineId: String?, request: EmbeddingRequest?): EmbeddingResult? {
        return api.createEmbeddings(engineId, request).blockingGet()
    }

    fun listFiles(): List<File> {
        return api.listFiles()?.blockingGet()?.data ?: listOf()
    }

    fun uploadFile(purpose: String?, filepath: String): File? {
        val file = java.io.File(filepath)
        val purposeBody = RequestBody.create(MultipartBody.FORM, purpose!!)
        val fileBody = RequestBody.create("text".toMediaType(), file)
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", filepath, fileBody)
        return api.uploadFile(purposeBody, body).blockingGet()
    }

    fun deleteFile(fileId: String?): DeleteResult? {
        return api.deleteFile(fileId).blockingGet()
    }

    fun retrieveFile(fileId: String?): File? {
        return api.retrieveFile(fileId).blockingGet()
    }

    fun createFineTune(request: FineTuneRequest?): FineTuneResult? {
        return api.createFineTune(request).blockingGet()
    }

    fun createFineTuneCompletion(request: CompletionRequest?): CompletionResult? {
        return api.createFineTuneCompletion(request).blockingGet()
    }

    fun listFineTunes(): List<FineTuneResult> {
        return api.listFineTunes()?.blockingGet()?.data ?: listOf()
    }

    fun retrieveFineTune(fineTuneId: String?): FineTuneResult? {
        return api.retrieveFineTune(fineTuneId).blockingGet()
    }

    fun cancelFineTune(fineTuneId: String?): FineTuneResult? {
        return api.cancelFineTune(fineTuneId).blockingGet()
    }

    fun listFineTuneEvents(fineTuneId: String?): List<FineTuneEvent> {
        return api.listFineTuneEvents(fineTuneId)?.blockingGet()?.data ?: listOf()
    }

    fun deleteFineTune(fineTuneId: String?): DeleteResult? {
        return api.deleteFineTune(fineTuneId).blockingGet()
    }

    fun createImage(request: CreateImageRequest?): ImageResult? {
        return api.createImage(request).blockingGet()
    }

    fun getListImageStyle(folder: String): ImageStyleResponse {
        return api.getImageStyle(folder).blockingGet()
    }

    fun createImageEdit(
        request: CreateImageEditRequest,
        imagePath: String?,
        maskPath: String?
    ): ImageResult? {
        val image = java.io.File(imagePath)
        var mask: java.io.File? = null
        if (maskPath != null) {
            mask = java.io.File(maskPath)
        }
        return createImageEdit(request, image, mask)
    }

    fun createImageEdit(
        request: CreateImageEditRequest,
        image: java.io.File?,
        mask: java.io.File?
    ): ImageResult? {
        val imageBody = image?.let { RequestBody.create("image".toMediaTypeOrNull(), it) }
        val builder: MultipartBody.Builder = MultipartBody.Builder()
            .setType("multipart/form-data".toMediaType())
            .addFormDataPart("prompt", request.prompt)
            .addFormDataPart("size", request.size)
            .addFormDataPart("response_format", request.responseFormat)
            .addFormDataPart("image", "image", imageBody!!)
        builder.addFormDataPart("n", request.n.toString())
        if (mask != null) {
            val maskBody = RequestBody.create("image".toMediaTypeOrNull(), mask)
            builder.addFormDataPart("mask", "mask", maskBody)
        }
        return api.createImageEdit(builder.build()).blockingGet()
    }

    fun createImageVariation(
        request: CreateImageVariationRequest,
        imagePath: String?
    ): ImageResult? {
        val image = java.io.File(imagePath)
        return createImageVariation(request, image)
    }

    fun createImageVariation(
        request: CreateImageVariationRequest,
        image: java.io.File?
    ): ImageResult? {
        val imageBody = RequestBody.create("image".toMediaTypeOrNull(), image!!)
        val builder: MultipartBody.Builder = MultipartBody.Builder()
            .setType("multipart/form-data".toMediaType())
            .addFormDataPart("size", request.size)
            .addFormDataPart("response_format", request.responseFormat)
            .addFormDataPart("image", "image", imageBody)
        builder.addFormDataPart("n", request.n.toString())
        return api.createImageVariation(builder.build()).blockingGet()
    }

    fun createModeration(request: ModerationRequest?): ModerationResult? {
        return api.createModeration(request).blockingGet()
    }

    @get:Deprecated("")
    val engines: List<Engine>
        get() = (api.engines?.blockingGet() as? OpenAiResponse<Engine>)?.data ?: listOf()

    @Deprecated("")
    fun getEngine(engineId: String?): Engine? {
        return api.getEngine(engineId).blockingGet()
    }

    fun getTokenBm(bundleID: String): String? {
        return api.getTimeBm(bundleID)?.blockingGet()?.data
    }

    @KeepName
    fun getCompletionsBm(@Body request: CompletionRequest?): CompletionResult? {
        return api.getCompletionsBm(request)?.blockingGet()
    }

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8088"
        private const val BASE_URL_ART = "https://aiapi-art.longkd.com"
        private const val BASE_URL_IMAGE = "https://images-server.longkd.com/"
    }
}