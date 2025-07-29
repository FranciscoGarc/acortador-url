package com.example

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import kotlinx.serialization.Serializable
import java.util.concurrent.ConcurrentHashMap

val urlStorage = ConcurrentHashMap<String, String>()

fun Application.configureRouting() {
    routing {
        post("/shorten") {
            val request = call.receive<ShortenRequest>()
            val longUrl = request.url

            // Genera un código corto aleatorio
            val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
            val shortCode = (1..6).map { alphabet.random() }.joinToString("")

            urlStorage[shortCode] = longUrl

            // Construye la URL corta completa para la respuesta
            val baseUrl = System.getenv("BASE_URL") ?: "http://${call.request.host()}:${call.request.port()}"
            val shortUrl = "$baseUrl/$shortCode"

            call.respond(ShortenResponse(shortUrl = shortUrl))
        }
        get("/{shortCode}") {
            val shortCode = call.parameters["shortCode"]
            val longUrl = urlStorage[shortCode]

            if (longUrl != null) {
                call.respondRedirect(longUrl, permanent = true)
            } else {
                call.respond(HttpStatusCode.NotFound, "URL no encontrada.")
            }
        }
        // Static plugin. Try to access `/static/index.html`
        staticResources("/", "static")
    }
}
