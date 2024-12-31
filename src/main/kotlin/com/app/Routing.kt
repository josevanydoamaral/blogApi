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
import kotlinx.serialization.Serializable

@Serializable
data class Post(val id: Int, var title: String, var content: String,
                val comments: MutableList<Comment> = mutableListOf(),
                var likes: Int = 0
)

@Serializable
data class Comment(
    val id: Int,
    val content: String,
    val author: String
)

val posts = mutableListOf<Post>()

data class UserPrincipal(val name: String, val role: Role) : Principal

val users = mapOf(
    "admin" to Pair("password123", Role.ADMIN),
    "editor" to Pair("editorpass", Role.EDITOR),
    "user" to Pair("userpass", Role.USER)
)


fun Application.configureRouting() {
    authentication {
        basic("auth-basic") {
            realm = "Access to Blog API"
            validate { credentials ->
<<<<<<< Updated upstream
                val userRole = users[credentials.name]
                if (userRole != null && userRole.first == credentials.password) {
                    UserPrincipal(credentials.name, userRole.second)
=======
                val user = getUserFromFirestore(credentials.name.toString())
                // Verifica se o usuário existe, está ativo e valida a senha
                if (user != null && user.isActive && verifyPasswordBCrypt(credentials.password, user.password)) {
                    UserPrincipal(user.username, user.role.toString())
>>>>>>> Stashed changes
                } else {
                    null
                }
            }
        }
    }
    routing {
        get("/") {
            call.respondText("Hello, ${call.principal<UserIdPrincipal>()?.name}!")
            // AQUI QUERO COLOCAR O README
        }


        get("posts") {
            call.respond(posts)
        }
        delete("/posts/delete/{id}") {
            //call.requireRole(Role.ADMIN) // Apenas um ADMIN pode apagar posts
            val postId = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "ID do post é obrigatório.")
            val isDeleted = deletePost(postId)

            if (isDeleted) {
                call.respond(HttpStatusCode.OK, "Post eliminado com sucesso.")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao eliminar o post.")
            }
        }


        authenticate("auth-basic") {

            post("posts/add") {
<<<<<<< Updated upstream
                call.requireRole(Role.EDITOR) // Apenas editores e administradores
                val postsReceived = call.receive<List<Post>>()
=======
                //call.requireRole(Role.EDITOR) // Apenas um Editor pode adicionar posts
                val postReceived = call.receive<Post>()
>>>>>>> Stashed changes
                // Adiciona cada post na lista de posts
                posts.addAll(postsReceived)
                // Responde com sucesso indicando o número de posts adicionados
                call.respondText("Foram adicionados ${postsReceived.size} post(s): ${postsReceived.joinToString { it.title }}")
            }

<<<<<<< Updated upstream
            delete("/posts/delete/{id}") {
                call.requireRole(Role.ADMIN) // Apenas administradores
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
=======


>>>>>>> Stashed changes
            put("/posts/update/{id}") {
                call.requireRole(Role.EDITOR) // Apenas editores e administradores
                val postId = call.parameters["id"]?.toIntOrNull()
                if (postId != null) {
<<<<<<< Updated upstream
                    val postToUpdate = posts.find { it.id == postId }

                    if (postToUpdate != null) {
                        val updatedPost = call.receive<Post>()

                        // Atualiza os campos do post existente
                        postToUpdate.title = updatedPost.title
                        postToUpdate.content = updatedPost.content

                        call.respondText("Post com id $postId atualizado com sucesso: ${postToUpdate.title}")
                    } else {
                        call.respondText("Post com id $postId não encontrado.", status = HttpStatusCode.NotFound)
=======
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
>>>>>>> Stashed changes
                    }
                } else {
                    call.respondText("Id inválido.", status = HttpStatusCode.BadRequest)
                }
            }

            // Comentários
            get("/posts/{id}/comments") {
                call.requireRole(Role.USER) // Users
                val postId = call.parameters["id"]?.toIntOrNull()
                if (postId != null) {
                    val post = posts.find { it.id == postId }
                    if (post != null) {
                        call.respond(post.comments)
                    } else {
                        call.respondText("Post com id $postId não encontrado.", status = HttpStatusCode.NotFound)
                    }
                } else {
                    call.respondText("Id inválido.", status = HttpStatusCode.BadRequest)
                }
            }

            post("/posts/{id}/addComment") {
                call.requireRole(Role.USER) // Users
                val postId = call.parameters["id"]?.toIntOrNull()
                if (postId != null) {
<<<<<<< Updated upstream
                    val post = posts.find { it.id == postId }
                    if (post != null) {
                        val comment = call.receive<Comment>()
                        post.comments.add(comment)
                        call.respondText("Comentário adicionado ao post $postId!")
                    } else {
                        call.respondText("Post com id $postId não encontrado.", status = HttpStatusCode.NotFound)
=======
                    try {
                        val comment = call.receive<Comment>() // Recebe o comentário no corpo da requisição
                        val principal = call.principal<UserPrincipal>()
                        val success = addComment(postId, comment) // Chama a função para adicionar o comentário
                        if (success) {
                            call.respondText("Comentário adicionado ao post $postId.")
                        } else {
                            call.respondText("Post não encontrado ou erro ao adicionar comentário.", status = HttpStatusCode.InternalServerError)
                        }
                    } catch (e: Exception) {
                        call.respondText("Erro ao processar o comentário: ${e.message}", status = HttpStatusCode.BadRequest)
>>>>>>> Stashed changes
                    }
                } else {
                    call.respondText("Id inválido.", status = HttpStatusCode.BadRequest)
                }
            }

            // Likes
            get("/posts/{id}/likes") {
                call.requireRole(Role.USER) // Users
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
                call.requireRole(Role.USER) // Users
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
<<<<<<< Updated upstream
        }
=======

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

        }

        get("/users") {
            try {
                val users = getAllUsersFromFirestore() // Chama a função para obter todos os usuários
                call.respond(HttpStatusCode.OK, users)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao buscar usuários: ${e.message}")
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

>>>>>>> Stashed changes
    }
}
