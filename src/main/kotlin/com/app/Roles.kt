package com.app

import io.ktor.server.application.*
import io.ktor.server.auth.*
import kotlinx.serialization.Serializable

@Serializable
enum class Role {
    ADMIN, EDITOR, USER
}

fun ApplicationCall.requireRole(role: Role) {
    val principal = principal<UserPrincipal>()
    if (principal == null || principal.role != role) {
        throw AuthorizationException("Você não tem permissão para acessar esta rota.")
    }
}

class AuthorizationException(message: String) : RuntimeException(message)