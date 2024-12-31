package com.app

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

fun main(args: Array<String>) {
    EngineMain.main(args) // Inicializa o Ktor
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    // Inicializar o Firebase
    initFirebase()
    // Configurações adicionais do Ktor
    configureRouting()

    runBlocking {
        ensureAdminUserExists() // Garante a criação do usuário ADMIN
    }
}

fun initFirebase() {
    // Caminho para o ficheiro JSON da conta de serviço
    val serviceAccount = File("src/main/resources/firebase-service-account.json")

    if (!serviceAccount.exists()) {
        throw IllegalStateException("O ficheiro 'firebase-service-account.json' não foi encontrado em src/main/resources.")
    }

    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount.inputStream()))
        .build() // O URL do database não é necessário para o Firestore

    FirebaseApp.initializeApp(options)
    println("Firebase inicializado com sucesso!")
}
