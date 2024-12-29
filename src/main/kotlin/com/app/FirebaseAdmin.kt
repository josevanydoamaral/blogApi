package com.app

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.FileInputStream

object FirebaseAdmin {
    fun initialize() {
        val serviceAccount = FileInputStream("google-services.json")

        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl("https://<your-database-name>.firebaseio.com") // Altere pelo URL do Firebase
            .build()

        FirebaseApp.initializeApp(options)
        println("Firebase inicializado com sucesso!")
    }
}
