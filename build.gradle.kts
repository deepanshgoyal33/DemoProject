plugins {
    id("java")
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1" // Plugin for creating a fat JAR
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Kafka clients
    implementation("org.apache.kafka:kafka-clients:3.6.1")

    // Jackson for JSON serialization
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")

    // SLF4J for logging
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("org.slf4j:slf4j-simple:2.0.9")
}

application {
    mainClass.set("org.example.KafkaProducerExample")
}

// Use Shadow JAR to include dependencies
tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
}

// Use Shadow Plugin to Create a Fat JAR
tasks.shadowJar {
    archiveBaseName.set("kafka-producer")
    archiveClassifier.set("") // No "-all" suffix, keeps it simple
    archiveVersion.set("") // Removes version from final jar name

    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
}

tasks.build {
    dependsOn(tasks.shadowJar) // Ensure shadowJar runs during build
}