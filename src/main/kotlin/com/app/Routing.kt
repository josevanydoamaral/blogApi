package com.app

import com.google.firebase.firestore.FirebaseFirestore
import io.ktor.server.application.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import io.ktor.server.request.receive
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable

@Serializable
data class Post(val id: Int, var title: String, var content: String, val comments: MutableList<Comment> = mutableListOf(), var likes: Int = 0)

@Serializable
data class Comment(val id: Int, val content: String, val author: String)

fun Application.configureRouting() {
    val firestore = FirebaseAdmin.getFirestore()

    routing {
        // Endpoint para obter todos os posts
        get("/posts") {
            val postsSnapshot = firestore.collection("posts").get()
            val posts = postsSnapshot.documents.mapNotNull { it.toObject(Post::class.java) }
            call.respond(posts)
        }

        // Endpoint para adicionar posts
        post("/posts/add") {
            val postsReceived = call.receive<List<Post>>()

            // Adiciona cada post ao Firestore
            postsReceived.forEach { post ->
                firestore.collection("posts").add(post)
            }

            call.respond(HttpStatusCode.Created, "Foram adicionados ${postsReceived.size} post(s).")
        }

        // Endpoint para atualizar um post
        put("/posts/update/{id}") {
            val postId = call.parameters["id"]?.toIntOrNull()
            val updatedPost = call.receive<Post>()

            if (postId != null) {
                val postRef = firestore.collection("posts").document(postId.toString())
                postRef.set(updatedPost) // Atualiza o post no Firestore
                call.respond(HttpStatusCode.OK, "Post atualizado com sucesso.")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Id inválido.")
            }
        }

        // Endpoint para deletar um post
        delete("/posts/delete/{id}") {
            val postId = call.parameters["id"]?.toIntOrNull()

            if (postId != null) {
                val postRef = firestore.collection("posts").document(postId.toString())
                postRef.delete() // Remove o post do Firestore
                call.respond(HttpStatusCode.NoContent, "Post removido com sucesso.")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Id inválido.")
            }
        }
    }
}
