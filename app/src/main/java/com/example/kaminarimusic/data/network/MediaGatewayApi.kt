package com.example.kaminarimusic.data.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface MediaGatewayApi {

    @GET("api/capabilities")
    suspend fun getCapabilities(): Response<GatewayCapabilities>

    @PUT("api/media/{id}/metadata")
    suspend fun updateMetadata(
        @Path("id") id: String,
        @Body metadata: MetadataUpdateRequest
    ): Response<GatewayOperationResponse>

    @PUT("api/media/{id}/artwork")
    @Multipart
    suspend fun updateArtwork(
        @Path("id") id: String,
        @Part artwork: MultipartBody.Part
    ): Response<GatewayOperationResponse>

    @DELETE("api/media/{id}")
    suspend fun deleteMedia(
        @Path("id") id: String
    ): Response<GatewayOperationResponse>

    @POST("api/media/upload")
    @Multipart
    suspend fun uploadMedia(
        @Part file: MultipartBody.Part,
        @Part("metadata") metadata: RequestBody?
    ): Response<GatewayOperationResponse>

    @POST("api/library/scan")
    suspend fun triggerScan(): Response<ScanResponse>

    @GET("api/library/scan/status")
    suspend fun getScanStatus(): Response<ScanStatusResponse>
}

data class GatewayCapabilities(
    val streaming: Boolean,
    val offline: Boolean,
    val upload: Boolean,
    val metadataEdit: Boolean,
    val artworkEdit: Boolean,
    val delete: Boolean,
    val replaceFile: Boolean,
    val scanLibrary: Boolean
)

data class MetadataUpdateRequest(
    val title: String?,
    val artist: String?,
    val album: String?,
    val genre: String?,
    val year: Int?,
    val trackNumber: Int?,
    val lyrics: String?
)

data class GatewayOperationResponse(
    val success: Boolean,
    val message: String,
    val operationId: String?
)

data class ScanResponse(
    val status: String,
    val scanId: String
)

data class ScanStatusResponse(
    val scanning: Boolean,
    val progress: Int
)
