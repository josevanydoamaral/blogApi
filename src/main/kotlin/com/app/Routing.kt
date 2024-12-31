package com.app

import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.mindrot.jbcrypt.BCrypt



fun Application.configureRouting() {
    authentication {
        basic("auth-basic") {
            realm = "Access to Blog API"
            validate { credentials ->
                val user = getUserFromFirestore(credentials.name)
                // Verifica se o usuário existe, está ativo e valida a senha
                if (user != null && user.isActive && verifyPasswordBCrypt(credentials.password, user.password)) {
                    UserPrincipal(user.username, user.role.toString())
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
                //call.requireRole(Role.EDITOR) // Apenas um Editor pode adicionar posts
                val postReceived = call.receive<Post>()
                println(postReceived)

                // Adiciona cada post na lista de posts
                savePost(postReceived)
                val isSaved = savePost(postReceived)

                if (isSaved) {
                    call.respond(HttpStatusCode.Created, "Post: ${postReceived.title} foi adicionado")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Erro ao salvar o post.")
                }
            }

            delete("posts/delete/{id}") {
                call.requireRole(Role.ADMIN) // Apenas um ADMIN pode apagar posts
                val postId = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "ID do post é obrigatório.")
                val isDeleted = deletePost(postId)

                if (isDeleted) {
                    call.respond(HttpStatusCode.OK, "Post eliminado com sucesso.")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Erro ao eliminar o post.")
                }
            }

            put("/posts/update/{id}") {
                call.requireRole(Role.EDITOR) // Apenas um Editor pode atualizar posts
                val postId = call.parameters["id"]?.toIntOrNull()
                if (postId != null) {
                    try {
                        val updatedPost = call.receive<Post>() // Recebe o post atualizado no corpo da requisição
                        updatedPost.id = postId.toString() // Garante que o ID no objeto é o mesmo da rota
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
                call.requireRole(Role.USER) // Apenas um USER pode ver comentários do post
                val postId = call.parameters["id"]?.toIntOrNull()
                if (postId != null) {
                    val comments = getComments(postId) // Chama a função que retorna os comentários
                    call.respond(comments)
                } else {
                    call.respondText("Id inválido.", status = HttpStatusCode.BadRequest)
                }
            }

            post("/posts/{id}/addComment") {
                call.requireRole(Role.USER) // Apenas um USER pode comentar posts
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
                call.requireRole(Role.USER) // Apenas um USER pode ver likes do post
                val postId = call.parameters["id"]?.toIntOrNull()
                if (postId != null) {
                    val likes = getLikes(postId) // Chama a função que retorna o número de likes
                    call.respond(mapOf("postId" to postId, "likes" to likes))
                } else {
                    call.respondText("Id inválido.", status = HttpStatusCode.BadRequest)
                }
            }
            post("/users/add") {
                val user = call.receive<User>() // Recebe os dados do novo usuário
                val success = addUser(user)
                if (success) {
                    call.respondText("Usuário criado com sucesso.")
                } else {
                    call.respondText("Erro ao criar o usuário.", status = HttpStatusCode.InternalServerError)
                }
            }

            post("/posts/{id}/like") {
                call.requireRole(Role.USER) // Apenas um USER pode dar likes no post
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
                call.requireRole(Role.USER) // Apenas um USER pode dar deslikes no post
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

            post("/posts/{id}/dislike") {
                call.requireRole(Role.USER) // Apenas um USER pode dar deslikes no post
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

            // USERSSSS



            put("/users/{id}") {
                val userId = call.parameters["id"] ?: return@put call.respondText("Id inválido.", status = HttpStatusCode.BadRequest)
                try {
                    val updatedUser = call.receive<User>()
                    updatedUser.id = userId // Garante que o ID corresponde ao fornecido
                    val success = updateUser(updatedUser)
                    if (success) {
                        call.respondText("Usuário atualizado com sucesso.")
                    } else {
                        call.respondText("Erro ao atualizar o usuário.", status = HttpStatusCode.InternalServerError)
                    }
                } catch (e: Exception) {
                    call.respondText("Erro ao processar a atualização: ${e.message}", status = HttpStatusCode.BadRequest)
                }
            }

            post("/users/add") {
                val user = call.receive<User>() // Recebe os dados do novo usuário
                val success = addUser(user)
                if (success) {
                    call.respondText("Usuário criado com sucesso.")
                } else {
                    call.respondText("Erro ao criar o usuário.", status = HttpStatusCode.InternalServerError)
                }
            }

            get("/users") {
                try {
                    val users = getAllUsersFromFirestore() // Chama a função para obter todos os usuários
                    call.respond(HttpStatusCode.OK, users)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Erro ao buscar usuários: ${e.message}")
                }
            }

            // USERSSSS

            put("/users/{id}") {
                val userId = call.parameters["id"] ?: return@put call.respondText("Id inválido.", status = HttpStatusCode.BadRequest)
                try {
                    val updatedUser = call.receive<User>()
                    updatedUser.id = userId // Garante que o ID corresponde ao fornecido
                    val success = updateUser(updatedUser)
                    if (success) {
                        call.respondText("Usuário atualizado com sucesso.")
                    } else {
                        call.respondText("Erro ao atualizar o usuário.", status = HttpStatusCode.InternalServerError)
                    }
                } catch (e: Exception) {
                    call.respondText("Erro ao processar a atualização: ${e.message}", status = HttpStatusCode.BadRequest)
                }
            }
        }
        delete("/users/delete/{id}") {
            //call.requireRole(Role.ADMIN) // Apenas um ADMIN pode apagar posts
            val userId = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "ID do user é obrigatório.")
            val isDeleted = deleteUser(userId)

            if (isDeleted) {
                call.respond(HttpStatusCode.OK, "User eliminado com sucesso.")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao eliminar o User.")
            }
        }

        get("/users") {
            try {
                val users = getAllUsersFromFirestore() // Chama a função para obter todos os usuários
                call.respond(HttpStatusCode.OK, users)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao buscar usuários: ${e.message}")
            }
        }

    }
}
