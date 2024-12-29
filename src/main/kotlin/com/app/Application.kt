package com.app

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.ktor.server.application.*
import io.ktor.server.netty.*
import java.io.File

fun main(args: Array<String>) {
    EngineMain.main(args) // Inicializa o Ktor
}

fun Application.module() {
    // Inicializar o Firebase
    initFirebase()

    // Configurações adicionais do Ktor
    configureRouting()
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
