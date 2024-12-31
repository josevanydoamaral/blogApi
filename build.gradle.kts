val ktorVersion = "3.0.2"
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.app"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
//Firebase(Tiago)
    implementation("com.google.firebase:firebase-admin:9.2.0")
    implementation("com.google.cloud:google-cloud-firestore:3.30.0") // Firestore Client
    implementation("org.mindrot:jbcrypt:0.4")



}
