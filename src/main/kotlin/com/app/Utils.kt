package com.app

import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import io.ktor.server.auth.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.time.Instant

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
) {
    constructor() : this(0, "", "")
}

val posts = mutableListOf<Post>()

data class User(
    var id: String = "",                // Identificador único do usuário
    var username: String = "",          // Nome de usuário para login
    var password: String = "",          // Senha (idealmente armazenada de forma hash em produção)
    var role: Role = Role.USER,         // Papel do usuário (USER, EDITOR, ADMIN)
    var isActive: Boolean = true        // Indica se o usuário está ativo ou suspenso
)

data class UserPrincipal(
    val id: String,
    val username: String,
    val role: Role = Role.USER
) : Principal

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

suspend fun deletePost(postId: String): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val firestore: Firestore = FirestoreClient.getFirestore()

            // Remove o documento pelo ID
            firestore.collection("posts").document(postId).delete().get()

            println("Post com ID $postId eliminado com sucesso.")
            true
        } catch (e: Exception) {
            println("Erro ao eliminar o post: ${e.message}")
            false
        }
    }
}

suspend fun updatePost(post: Post): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val firestore: Firestore = FirestoreClient.getFirestore()

            // Atualiza os dados do post
            firestore.collection("posts")
                .document(post.id.toString())
                .set(post)
                .get()

            println("Post atualizado com sucesso: ID = ${post.id}")
            true
        } catch (e: Exception) {
            println("Erro ao atualizar o post: ${e.message}")
            false
        }
    }
}


suspend fun getComments(postId: Int): List<Comment> {
    return withContext(Dispatchers.IO) {
        try {
            val firestore: Firestore = FirestoreClient.getFirestore()

            // Busca o post pelo ID
            val document = firestore.collection("posts")
                .document(postId.toString())
                .get()
                .get()

            // Extrai os comentários
            document.toObject(Post::class.java)?.comments ?: emptyList()
        } catch (e: Exception) {
            println("Erro ao buscar comentários: ${e.message}")
            emptyList()
        }
    }
}


suspend fun addComment(postId: Int, comment: Comment): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val firestore: Firestore = FirestoreClient.getFirestore()

            // Busca o post pelo ID
            val document = firestore.collection("posts")
                .document(postId.toString())
                .get()
                .get()

            val post = document.toObject(Post::class.java)

            if (post != null) {
                post.comments.add(comment)

                // Atualiza o post com o novo comentário
                firestore.collection("posts")
                    .document(postId.toString())
                    .set(post)
                    .get()

                println("Comentário adicionado ao post $postId.")
                true
            } else {
                println("Post não encontrado: ID = $postId")
                false
            }
        } catch (e: Exception) {
            println("Erro ao adicionar comentário: ${e.message}")
            false
        }
    }
}

suspend fun getLikes(postId: Int): Int {
    return withContext(Dispatchers.IO) {
        try {
            val firestore: Firestore = FirestoreClient.getFirestore()

            // Busca o post pelo ID
            val document = firestore.collection("posts")
                .document(postId.toString())
                .get()
                .get()

            val post = document.toObject(Post::class.java)

            // Retorna o número de likes ou 0 se o post não for encontrado
            post?.likes ?: 0
        } catch (e: Exception) {
            println("Erro ao buscar número de likes: ${e.message}")
            0
        }
    }
}


suspend fun addLike(postId: Int): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val firestore: Firestore = FirestoreClient.getFirestore()

            val document = firestore.collection("posts")
                .document(postId.toString())
                .get()
                .get()

            val post = document.toObject(Post::class.java)

            if (post != null) {
                post.likes++

                // Atualiza o número de likes
                firestore.collection("posts")
                    .document(postId.toString())
                    .set(post)
                    .get()

                println("Like adicionado ao post $postId.")
                true
            } else {
                println("Post não encontrado: ID = $postId")
                false
            }
        } catch (e: Exception) {
            println("Erro ao adicionar like: ${e.message}")
            false
        }
    }
}

suspend fun removeLike(postId: Int): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val firestore: Firestore = FirestoreClient.getFirestore()

            val document = firestore.collection("posts")
                .document(postId.toString())
                .get()
                .get()

            val post = document.toObject(Post::class.java)

            if (post != null && post.likes > 0) {
                post.likes--

                // Atualiza o número de likes
                firestore.collection("posts")
                    .document(postId.toString())
                    .set(post)
                    .get()

                println("Like removido do post $postId.")
                true
            } else {
                println("Post não encontrado ou sem likes: ID = $postId")
                false
            }
        } catch (e: Exception) {
            println("Erro ao remover like: ${e.message}")
            false
        }
    }
}

// USERS

suspend fun addUser(user: User): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val firestore = FirestoreClient.getFirestore()
            val usersCollection = firestore.collection("users")
            val usersCount = usersCollection.get().get().documents.size

            if (usersCount == 0) {
                val adminUser = User(
                    id = "1",
                    username = "ADMIN",
                    password = "ADMIN", // Hash a senha em produção
                    role = Role.ADMIN,
                    isActive = true
                )
                usersCollection.document(adminUser.id).set(adminUser).get()
                println("Usuário ADMIN criado automaticamente.")
            }

            usersCollection.document(user.id).set(user).get()
            println("Usuário ${user.username} adicionado com sucesso.")
            true
        } catch (e: Exception) {
            println("Erro ao adicionar usuário: ${e.localizedMessage}")
            e.printStackTrace()
            false
        }
    }
}


suspend fun updateUser(user: User): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val firestore: Firestore = FirestoreClient.getFirestore()

            // Cria um mapa com os dados atualizados
            val userMap = mapOf(
                "username" to user.username,
                "password" to user.password, // Certifique-se de usar hashing
                "role" to user.role.toString(),
                "isActive" to user.isActive
            )

            // Atualiza o documento do usuário no Firestore
            firestore.collection("users")
                .document(user.id)
                .set(userMap)
                .get()

            println("Usuário ${user.username} atualizado com sucesso.")
            true
        } catch (e: Exception) {
            println("Erro ao atualizar usuário: ${e.message}")
            false
        }
    }
}

suspend fun getAllUsersFromFirestore(): List<User> {
    return withContext(Dispatchers.IO) {
        val firestore = FirestoreClient.getFirestore()
        val usersCollection = firestore.collection("users").get().get() // Chamada síncrona
        usersCollection.documents.mapNotNull { it.toObject(User::class.java) }
    }
}


suspend fun getUserFromFirestore(username: String): User? {
    return withContext(Dispatchers.IO) { // Certifique-se de que a operação de I/O seja executada no contexto correto
        val firestore = FirestoreClient.getFirestore()
        val userDoc = firestore.collection("users").document(username).get().get() // Chamada síncrona
        if (userDoc.exists()) {
            userDoc.toObject(User::class.java)
        } else {
            null
        }
    }
}
