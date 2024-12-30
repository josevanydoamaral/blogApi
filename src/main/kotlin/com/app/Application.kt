package com.app

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.response.respond
import io.ktor.server.request.receive
import kotlinx.serialization.json.Json
import java.io.File


fun main(args: Array<String>) {
    EngineMain.main(args) // Inicializa o Ktor
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json { prettyPrint = true; isLenient = true }) // Serializa para JSON
    }

    // Inicializa o Firebase
    FirebaseAdmin.initialize()

    // Configuração das rotas
    configureRouting()
}
