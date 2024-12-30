package com.app

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
//    install(StatusPages) {
//        exception<AuthorizationException> {call, cause ->
//            call.respond(HttpStatusCode.Forbidden, cause.message ?: "Acesso negado")
//        }
//    }


    configureRouting()
}


