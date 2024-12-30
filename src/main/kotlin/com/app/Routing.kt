package com.app

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*



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
                val postId = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "ID do post é obrigatório.")
                val isDeleted = deletePost(postId)

                if (isDeleted) {
                    call.respond(HttpStatusCode.OK, "Post eliminado com sucesso.")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Erro ao eliminar o post.")
                }
            }

            put("/posts/update/{id}") {
                val postId = call.parameters["id"]?.toIntOrNull()
                if (postId != null) {
                    try {
                        val updatedPost = call.receive<Post>() // Recebe o post atualizado no corpo da requisição
                        updatedPost.id = postId // Garante que o ID no objeto é o mesmo da rota
                        val success = updatePost(updatedPost) // Chama a função para atualizar o post
                        if (success) {
                            call.respondText("Post com id $postId atualizado com sucesso.")
                        } else {
                            call.respondText("Post não encontrado ou erro ao atualizar o post.", status = HttpStatusCode.InternalServerError)
                        }
                    } catch (e: Exception) {
                        call.respondText("Erro ao processar a atualização: ${e.message}", status = HttpStatusCode.BadRequest)
                    }
                } else {
                    call.respondText("Id inválido.", status = HttpStatusCode.BadRequest)
                }
            }

            // Comentários
            get("/posts/{id}/comments") {
                val postId = call.parameters["id"]?.toIntOrNull()
                if (postId != null) {
                    val comments = getComments(postId) // Chama a função que retorna os comentários
                    call.respond(comments)
                } else {
                    call.respondText("Id inválido.", status = HttpStatusCode.BadRequest)
                }
            }

            post("/posts/{id}/addComment") {
                val postId = call.parameters["id"]?.toIntOrNull()
                if (postId != null) {
                    try {
                        val comment = call.receive<Comment>() // Recebe o comentário no corpo da requisição
                        val success = addComment(postId, comment) // Chama a função para adicionar o comentário
                        if (success) {
                            call.respondText("Comentário adicionado ao post $postId.")
                        } else {
                            call.respondText("Post não encontrado ou erro ao adicionar comentário.", status = HttpStatusCode.InternalServerError)
                        }
                    } catch (e: Exception) {
                        call.respondText("Erro ao processar o comentário: ${e.message}", status = HttpStatusCode.BadRequest)
                    }
                } else {
                    call.respondText("Id inválido.", status = HttpStatusCode.BadRequest)
                }
            }

            // Likes
            get("/posts/{id}/likes") {
                val postId = call.parameters["id"]?.toIntOrNull()
                if (postId != null) {
                    val likes = getLikes(postId) // Chama a função que retorna o número de likes
                    call.respond(mapOf("postId" to postId, "likes" to likes))
                } else {
                    call.respondText("Id inválido.", status = HttpStatusCode.BadRequest)
                }
            }

            post("/posts/{id}/like") {
                val postId = call.parameters["id"]?.toIntOrNull()
                if (postId != null) {
                    val success = addLike(postId) // Chama a função para incrementar os likes
                    if (success) {
                        call.respondText("Like adicionado ao post $postId.")
                    } else {
                        call.respondText("Post não encontrado ou erro ao adicionar like.", status = HttpStatusCode.InternalServerError)
                    }
                } else {
                    call.respondText("Id inválido.", status = HttpStatusCode.BadRequest)
                }
            }

            post("/posts/{id}/dislike") {
                val postId = call.parameters["id"]?.toIntOrNull()
                if (postId != null) {
                    val success = removeLike(postId) // Chama a função para decrementar os likes
                    if (success) {
                        call.respondText("Like removido do post $postId.")
                    } else {
                        call.respondText("Post não encontrado ou erro ao remover like.", status = HttpStatusCode.InternalServerError)
                    }
                } else {
                    call.respondText("Id inválido.", status = HttpStatusCode.BadRequest)
                }
            }

        }
    }
}
