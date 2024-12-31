package com.app

<<<<<<< Updated upstream
import io.ktor.http.*
=======
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
>>>>>>> Stashed changes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
<<<<<<< Updated upstream
import io.ktor.server.plugins.statuspages.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}
=======
import kotlinx.coroutines.runBlocking
import java.io.File

fun main(args: Array<String>) {
    EngineMain.main(args) // Inicializa o Ktor
}



>>>>>>> Stashed changes

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
<<<<<<< Updated upstream
//    install(StatusPages) {
//        exception<AuthorizationException> {call, cause ->
//            call.respond(HttpStatusCode.Forbidden, cause.message ?: "Acesso negado")
//        }
//    }


=======
    // Inicializar o Firebase
    initFirebase()
    // Configurações adicionais do Ktor
>>>>>>> Stashed changes
    configureRouting()

    runBlocking {
        ensureAdminUserExists() // Garante a criação do usuário ADMIN
    }

}


