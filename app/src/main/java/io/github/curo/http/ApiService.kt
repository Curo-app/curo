package io.github.curo.http

import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("note/share")
    suspend fun share(@Body note: NoteDto): UrlDto

    @POST("collection/share")
    suspend fun share(@Body collection: CollectionDto): UrlDto
}