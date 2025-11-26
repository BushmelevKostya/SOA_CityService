// Корневой build.gradle.kts для multi-module проекта
plugins {
    id("java")
}

allprojects {
    group = "itmo"
    version = "0.0.1-SNAPSHOT"
    
    repositories {
        mavenCentral()

    }
}

subprojects {
    apply(plugin = "java")
    
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }
}

