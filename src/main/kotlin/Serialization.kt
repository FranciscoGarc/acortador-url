package com.example

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

// Clases de datos para la comunicación
@Serializable
data class ShortenRequest(val url: String)

@Serializable
data class ShortenResponse(val shortUrl: String)

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}
