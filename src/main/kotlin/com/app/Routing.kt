package com.app

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.date.*
import java.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Post(var id: Int, var title: String, var content: String, var author: String = "",
                var comments: MutableList<Comment> = mutableListOf(),
                var likes: Int = 0, var createdAt: Long = Instant.now().epochSecond
) {
    constructor() : this(0, "", "", "", emptyList<Comment>().toMutableList(),0, 0)
}

@Serializable
data class Comment(
    val id: Int,
    val content: String,
    val author: String
)

val posts = mutableListOf<Post>()

val users = mapOf(
    "jetrains" to "foobar",  // Exemplo de usuário e senha
    "user1" to "password1",
    "user2" to "password2"
)

fun Application.configureRouting() {
    authentication {
        basic("auth-basic") {
            realm = "Access to the '/' path"
            validate { credentials ->
                if (users[credentials.name] == credentials.password) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
    routing {
        get("/") {
            // AQUI QUERO COLOCAR O README
        }


        get("posts") {
            call.respond(getAllPosts())
        }


        authenticate("auth-basic") {
            post("posts/add") {
                val postReceived = call.receive<Post>()
                // Adiciona cada post na lista de posts
                savePost(postReceived)
                val isSaved = savePost(postReceived)

                if (isSaved) {
                    call.respond(HttpStatusCode.Created, "Post: ${postReceived.title} foi adicionado")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Erro ao salvar o post.")
                }
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
            put("/posts/update/{id}") {
                val postId = call.parameters["id"]?.toIntOrNull()
                if (postId != null) {
                    val postToUpdate = posts.find { it.id == postId }

                    if (postToUpdate != null) {
                        val updatedPost = call.receive<Post>()

                        // Atualiza os campos do post existente
                        postToUpdate.title = updatedPost.title
                        postToUpdate.content = updatedPost.content

                        call.respondText("Post com id $postId atualizado com sucesso: ${postToUpdate.title}")
                    } else {
                        call.respondText("Post com id $postId não encontrado.", status = HttpStatusCode.NotFound)
                    }
                } else {
                    call.respondText("Id inválido.", status = HttpStatusCode.BadRequest)
                }
            }

            // Comentários
//            get("/posts/{id}/comments") {
//                val postId = call.parameters["id"]?.toIntOrNull()
//                if (postId != null) {
//                    val post = posts.find { it.id == postId }
//                    if (post != null) {
//                        call.respond(post.comments)
//                    } else {
//                        call.respondText("Post com id $postId não encontrado.", status = HttpStatusCode.NotFound)
//                    }
//                } else {
//                    call.respondText("Id inválido.", status = HttpStatusCode.BadRequest)
//                }
//            }
//
//            post("/posts/{id}/addComment") {
//                val postId = call.parameters["id"]?.toIntOrNull()
//                if (postId != null) {
//                    val post = posts.find { it.id == postId }
//                    if (post != null) {
//                        val comment = call.receive<Comment>()
//                        post.comments.add(comment)
//                        call.respondText("Comentário adicionado ao post $postId!")
//                    } else {
//                        call.respondText("Post com id $postId não encontrado.", status = HttpStatusCode.NotFound)
//                    }
//                } else {
//                    call.respondText("Id inválido.", status = HttpStatusCode.BadRequest)
//                }
//            }

            // Likes
            get("/posts/{id}/likes") {
                val postId = call.parameters["id"]?.toIntOrNull()
                if (postId != null) {
                    val post = posts.find { it.id == postId }
                    if (post != null) {
                        call.respond("Likes: " + post.likes)
                    } else {
                        call.respondText("Post com id $postId não encontrado.", status = HttpStatusCode.NotFound)
                    }
                } else {
                    call.respondText("Id inválido.", status = HttpStatusCode.BadRequest)
                }
            }

            post("/posts/{id}/like") {
                val postId = call.parameters["id"]?.toIntOrNull()
                if (postId != null) {
                    val post = posts.find { it.id == postId }
                    if (post != null) {
                        post.likes +=1
                        call.respondText("Post $postId curtido! Total de curtidas: ${post.likes}")
                    } else {
                        call.respondText("Post com id $postId não encontrado.", status = HttpStatusCode.NotFound)
                    }
                } else {
                    call.respondText("Id inválido.", status = HttpStatusCode.BadRequest)
                }
            }
        }
    }
}
