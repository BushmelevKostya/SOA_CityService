plugins {
    java
    war
    id("org.openapi.generator") version "7.0.0"
}

group = "itmo"
version = "0.0.1-SNAPSHOT"
description = "CityServiceWeb"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // EJB
    implementation(project(":CityServiceEJB"))

    // Java EE
    compileOnly("jakarta.ws.rs:jakarta.ws.rs-api:3.1.0")
    compileOnly("jakarta.ejb:jakarta.ejb-api:4.0.1")
    compileOnly("jakarta.servlet:jakarta.servlet-api:5.0.0")
    compileOnly("jakarta.xml.bind:jakarta.xml.bind-api:4.0.0")

    implementation("io.swagger:swagger-annotations:1.6.8")
}

openApiGenerate {
    generatorName.set("jaxrs-spec")
    inputSpec.set("$projectDir/src/main/resources/CityService.yaml")
    outputDir.set("$buildDir/generated")
    apiPackage.set("itmo.cityservice.api")
    modelPackage.set("itmo.cityservice.model")
    configOptions.set(mapOf(
        "interfaceOnly" to "true",
        "useSwaggerAnnotations" to "true",
        "generateBuilders" to "true",
        "useBeanValidation" to "true",
        "withXml" to "true"
    ))
}

sourceSets {
    main {
        java {
            srcDirs("src/main/java", "${layout.buildDirectory.get()}/generated/src/main/java")
        }
    }
}

tasks.named<War>("war") {
    enabled = true
    archiveClassifier.set("")
}
