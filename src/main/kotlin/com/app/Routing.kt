package com.app

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class Post(val id: Int, val title: String, val content: String)

val posts = mutableListOf<Post>()

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("posts") {
            call.respond(posts)
        }
        post("posts/add") {
            val postsReceived = call.receive<List<Post>>()
            // Adiciona cada post na lista de posts
            posts.addAll(postsReceived)
            // Responde com sucesso indicando o número de posts adicionados
            call.respondText("Foram adicionados ${postsReceived.size} post(s): ${postsReceived.joinToString { it.title }}")
        }

        delete("/posts/delete/{id}") {
            val postId = call.parameters["id"]?.toIntOrNull()

            if (postId != null) {
                val postToRemove = posts.find { it.id == postId }

                if (postToRemove != null) {
                    posts.remove(postToRemove) // Remove o post da lista
                    call.respondText("Post com id $postId removido com sucesso.")
                } else {
                    call.respondText("Post com id $postId não encontrado.", status = HttpStatusCode.NotFound)
                }
            } else {
                call.respondText("Id inválido.", status = HttpStatusCode.BadRequest)
            }
        }
    }
}
