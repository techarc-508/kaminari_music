package com.example.kaminarimusic.data.network

import retrofit2.http.GET
import retrofit2.http.Query

import com.squareup.moshi.Json

interface NavidromeApi {

    @GET("rest/ping.view")
    suspend fun ping(
        @Query("u") user: String,
        @Query("t") token: String,
        @Query("s") salt: String,
        @Query("v") version: String = "1.16.1",
        @Query("c") client: String = "KaminariMusic"
    ): SubsonicResponse

    @GET("rest/getAlbumList2.view")
    suspend fun getAlbums(
        @Query("u") user: String,
        @Query("t") token: String,
        @Query("s") salt: String,
        @Query("v") version: String = "1.16.1",
        @Query("c") client: String = "KaminariMusic",
        @Query("type") type: String = "newest",
        @Query("size") size: Int = 50
    ): SubsonicResponse

    @GET("rest/search3.view")
    suspend fun search(
        @Query("u") user: String,
        @Query("t") token: String,
        @Query("s") salt: String,
        @Query("v") version: String = "1.16.1",
        @Query("c") client: String = "KaminariMusic",
        @Query("query") query: String
    ): SubsonicResponse
}

data class SubsonicResponse(
    @Json(name = "subsonic-response")
    val subsonicResponse: SubsonicResponseData? = null
)

data class SubsonicResponseData(
    val status: String,
    val version: String,
    val albumList2: AlbumList2Container? = null,
    val searchResult3: SearchResult3Container? = null
)

data class AlbumList2Container(
    val album: List<SubsonicAlbum>? = null
)

data class SubsonicAlbum(
    val id: String,
    val title: String,
    val artist: String,
    val year: Int? = null,
    val coverArt: String? = null,
    val songCount: Int? = null
)

data class SearchResult3Container(
    val song: List<SubsonicSong>? = null,
    val album: List<SubsonicAlbum>? = null,
    val artist: List<SubsonicArtist>? = null
)

data class SubsonicSong(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long? = null,
    val coverArt: String? = null,
    val bitRate: Int? = null
)

data class SubsonicArtist(
    val id: String,
    val name: String,
    val artistImageUrl: String? = null
)
