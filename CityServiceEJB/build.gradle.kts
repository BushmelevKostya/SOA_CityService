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
    compileOnly("jakarta.xml.bind:jakarta.xml.bind-api:4.0.0")
    compileOnly("org.jboss.ejb3:jboss-ejb3-ext-api:2.3.0.Final")
    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.3")
    // JPA Implementation (Hibernate)
    implementation("org.hibernate.orm:hibernate-core:6.4.0.Final")
    implementation("com.orbitz.consul:consul-client:1.5.3")
    compileOnly("org.jboss.logging:jboss-logging:3.5.3.Final")
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

