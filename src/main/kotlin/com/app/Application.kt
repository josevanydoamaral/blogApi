package com.app

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.File
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    // Inicializar o Firebase
    initFirebase()

    // Outras configurações
    configureRouting()
}

fun initFirebase() {
    val serviceAccount = File("src/main/resources/firebase-service-account.json")
    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount.inputStream()))
        .setDatabaseUrl("https://blogapi-2bbdc.firebaseio.com")
        .build()

    FirebaseApp.initializeApp(options)
}
