package com.app

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.io.FileInputStream

object FirebaseAdmin {

    // Inicializa o Firebase e o Firestore
    fun initialize() {
        val serviceAccount = FileInputStream("src/main/resources/firebase-service-account.json")

        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()

        FirebaseApp.initializeApp(options)
        println("Firebase inicializado com sucesso!")
    }

    // Retorna uma instância do Firestore
    fun getFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}
