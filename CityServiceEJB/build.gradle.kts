plugins {
    java
    `java-library`
}

group = "itmo"
version = "0.0.1-SNAPSHOT"
description = "CityServiceEJB"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Jakarta EE API
    compileOnly("jakarta.platform:jakarta.jakartaee-api:10.0.0")
    compileOnly("jakarta.ejb:jakarta.ejb-api:4.0.1")
    compileOnly("jakarta.persistence:jakarta.persistence-api:3.1.0")
    compileOnly("jakarta.inject:jakarta.inject-api:2.0.1")
    
    // Spring Data JPA (для работы с репозиториями)
    implementation("org.springframework.data:spring-data-jpa:3.2.0")
    implementation("org.springframework:spring-context:6.1.0")
    implementation("org.springframework:spring-tx:6.1.0")
    
    // JPA Implementation (Hibernate)
    implementation("org.hibernate.orm:hibernate-core:6.4.0.Final")
    
    // XML Binding
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.0")
    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.3")
}

tasks.jar {
    archiveBaseName.set("city-service-ejb")
    archiveClassifier.set("")
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    }
}

// Экспортируем все классы для использования в других модулях
java {
    withSourcesJar()
    withJavadocJar()
}

