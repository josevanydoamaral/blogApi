package com.app

import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun getAllPosts(): List<Post> {
    return withContext(Dispatchers.IO) {
        try {
            val firestore: Firestore = FirestoreClient.getFirestore()

            // Busca todos os documentos da coleção "posts"
            val documents = firestore.collection("posts").get().get().documents

            // Mapeia os documentos para objetos Post
            documents.mapNotNull { doc ->
                // Tenta converter doc.id para Int
                val postId = doc.id.toIntOrNull() ?: 0 // Define um valor padrão (0) se a conversão falhar
                doc.toObject(Post::class.java).copy(id = postId)
            }
        } catch (e: Exception) {
            println("Erro ao buscar posts: ${e.message}")
            emptyList()
        }
    }
}

suspend fun savePost(post: Post): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            // Obtenha uma instância do Firestore
            val firestore: Firestore = FirestoreClient.getFirestore()

            // Crie um mapa com os dados do post
            val postMap = mapOf(
                "id" to post.id,
                "title" to post.title,
                "content" to post.content,
                "author" to post.author,
                "likes" to post.likes
            )

            // Salve o post na coleção "posts"
            firestore.collection("posts")
                .document(post.id.toString()) // Use o ID como chave no documento
                .set(postMap)
                .get() // Aguarde a operação ser concluída

            println("Post salvo com sucesso: ID = ${post.id}")
            true
        } catch (e: Exception) {
            println("Erro ao salvar o post: ${e.message}")
            false
        }
    }
}