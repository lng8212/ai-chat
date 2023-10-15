package com.longkd.chatgpt_openai.open.client

import com.longkd.chatgpt_openai.base.model.*
import com.longkd.chatgpt_openai.open.dto.BmOpenAiResponse
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
import com.longkd.chatgpt_openai.open.dto.image.CreateImageRequest
import com.longkd.chatgpt_openai.open.dto.image.ImageResult
import com.longkd.chatgpt_openai.open.dto.image_art.ImageArtRequest
import com.longkd.chatgpt_openai.open.dto.image_art.ImageArtResult
import com.longkd.chatgpt_openai.open.dto.model.Model
import com.longkd.chatgpt_openai.open.dto.moderation.*
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface OpenAiApi {
    @GET("v1/models")
    fun listModels(): Single<OpenAiResponse<Model>>?

    @GET("/v1/models/{model_id}")
    fun getModel(@Path("model_id") modelId: String?): Single<Model>

    @GET("/api/getTime")
    fun getTime(): Single<TokenDto>
    @POST("/v1/completions")
    fun createCompletion(@Body request: CompletionRequest?): Single<CompletionResult>
    @POST("/v1/chat/completions")
    fun createCompletion35(@Body request: Completion35Request?): Single<Completion35Result>
    @POST("/api/v1/chat")
    fun createCompletionV1Chat(@Body request: Completion35Request?): Single<Completion35Result>

    @POST("/api/v1/chat_v4")
    fun createCompletionV1ChatGPT4(@Body request: Completion35Request?): Single<Completion35Result>
    @POST("/api/v1/completion")
    fun createCompletionNew(@Body request: CompletionRequest?): Single<CompletionResult>

    @POST("/api/v2/art")
    fun createImageArt(@Body request: ImageArtRequest?): Single<ImageArtResult>

    @POST("/api/v2/art")
    fun generateImageArt(@Body request: GenerateArtRequest?): Single<GenerateArtResult>

    @Deprecated("")
    @POST("/v1/engines/{engine_id}/completions")
    fun createCompletion(
        @Path("engine_id") engineId: String?,
        @Body request: CompletionRequest?
    ): Single<CompletionResult>

    @POST("/v1/edits")
    fun createEdit(@Body request: EditRequest?): Single<EditResult>

    @Deprecated("")
    @POST("/v1/engines/{engine_id}/edits")
    fun createEdit(
        @Path("engine_id") engineId: String?,
        @Body request: EditRequest?
    ): Single<EditResult>

    @POST("/v1/embeddings")
    fun createEmbeddings(@Body request: EmbeddingRequest?): Single<EmbeddingResult>

    @Deprecated("")
    @POST("/v1/engines/{engine_id}/embeddings")
    fun createEmbeddings(
        @Path("engine_id") engineId: String?,
        @Body request: EmbeddingRequest?
    ): Single<EmbeddingResult>

    @GET("/v1/files")
    fun listFiles(): Single<OpenAiResponse<File>>?

    @Multipart
    @POST("/v1/files")
    fun uploadFile(@Part("purpose") purpose: RequestBody?, @Part file: MultipartBody.Part?): Single<File>

    @DELETE("/v1/files/{file_id}")
    fun deleteFile(@Path("file_id") fileId: String?): Single<DeleteResult>

    @GET("/v1/files/{file_id}")
    fun retrieveFile(@Path("file_id") fileId: String?): Single<File>

    @POST("/v1/fine-tunes")
    fun createFineTune(@Body request: FineTuneRequest?): Single<FineTuneResult>

    @POST("/v1/completions")
    fun createFineTuneCompletion(@Body request: CompletionRequest?): Single<CompletionResult>

    @GET("/v1/fine-tunes")
    fun listFineTunes(): Single<OpenAiResponse<FineTuneResult>>?

    @GET("/v1/fine-tunes/{fine_tune_id}")
    fun retrieveFineTune(@Path("fine_tune_id") fineTuneId: String?): Single<FineTuneResult>

    @POST("/v1/fine-tunes/{fine_tune_id}/cancel")
    fun cancelFineTune(@Path("fine_tune_id") fineTuneId: String?): Single<FineTuneResult>

    @GET("/v1/fine-tunes/{fine_tune_id}/events")
    fun listFineTuneEvents(@Path("fine_tune_id") fineTuneId: String?): Single<OpenAiResponse<FineTuneEvent>>?

    @DELETE("/v1/models/{fine_tune_id}")
    fun deleteFineTune(@Path("fine_tune_id") fineTuneId: String?): Single<DeleteResult>

    @POST("/v1/images/generations")
    fun createImage(@Body request: CreateImageRequest?): Single<ImageResult>

    @POST("/v1/images/edits")
    fun createImageEdit(@Body requestBody: RequestBody?): Single<ImageResult>

    @POST("/v1/images/variations")
    fun createImageVariation(@Body requestBody: RequestBody?): Single<ImageResult>

    @POST("/v1/moderations")
    fun createModeration(@Body request: ModerationRequest?): Single<ModerationResult>

    @get:GET("v1/engines")
    @get:Deprecated("")
    val engines: Single<Any>?

    @Deprecated("")
    @GET("/v1/engines/{engine_id}")
    fun getEngine(@Path("engine_id") engineId: String?): Single<Engine>

    @POST("v1/gettoken")
    fun getTimeBm(@Query("bundleID") bundleID:String): Single<BmOpenAiResponse>?
    @POST("v1/completions")
    fun getCompletionsBm(@Body request: CompletionRequest?): Single<CompletionResult>?

    @POST("/api/v1/summarize_text")
    fun uploadSummaryText(@Body request: Completion35Request?): Single<SummaryFileResponse>

    @POST("/api/v1/summarize_chat")
    fun completeSummaryChat(@Body request: Completion35Request?): Single<Completion35Result>

    @GET("list")
    fun getImageStyle(@Query("folder") folder: String): Single<ImageStyleResponse>

    @POST("api/v1/generate_art")
    fun generateArtByVyro(@Body request: GenerateArtByVyroRequest?): Single<GenerateArtResponse>?
}