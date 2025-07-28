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
import redis.clients.jedis.Jedis

//val urlStorage = ConcurrentHashMap<String, String>()
val redisUrl = System.getenv("REDIS_URL") ?: "redis://localhost:6379"
val jedis = Jedis(redisUrl) // Creamos una instancia del cliente de Redis

fun Application.configureRouting() {
    routing {
        post("/shorten") {
            val request = call.receive<ShortenRequest>()
            val longUrl = request.url

            val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01234TUE"
            val shortCode = (1..6).map { alphabet.random() }.joinToString("")

            jedis.set(shortCode, longUrl)

            val baseUrl = System.getenv("BASE_URL") ?: "http://${call.request.host()}:${call.request.port()}"
            val shortUrl = "$baseUrl/$shortCode"

            call.respond(ShortenResponse(shortUrl = shortUrl))
        }
        get("/{shortCode}") {
            val shortCode = call.parameters["shortCode"] ?: ""

            // --- Leer de Redis ---
            val longUrl = jedis.get(shortCode)

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
