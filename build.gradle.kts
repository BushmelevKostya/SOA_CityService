plugins {
	java
	war
	id("org.springframework.boot") version "3.5.6"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.openapi.generator") version "7.0.0"
}

group = "itmo"
version = "0.0.1-SNAPSHOT"
description = "CityService"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-rest")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.openapitools:jackson-databind-nullable:0.2.6")
	implementation("io.swagger:swagger-annotations:1.6.8")
	implementation("javax.annotation:javax.annotation-api:1.3.2")
	implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.0")
	implementation("org.glassfish.jaxb:jaxb-runtime:4.0.2")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
	implementation("org.yaml:snakeyaml:2.2")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

openApiGenerate {
	generatorName.set("spring")
	inputSpec.set("$projectDir/src/main/resources/CityService.yaml")
	outputDir.set("$buildDir/generated")
	apiPackage.set("itmo.cityservice.api")
	modelPackage.set("itmo.cityservice.model")
	invokerPackage.set("itmo.cityservice")
	configOptions.set(mapOf(
		"interfaceOnly" to "false",
		"useSpringBoot3" to "true",
		"useTags" to "true",
		"dateLibrary" to "java8",
		"serializableModel" to "true",
		"useBeanValidation" to "true",
		"performBeanValidation" to "true",
		"useOptional" to "true",
		"openApiNullable" to "false",
		"library" to "spring-boot",
		"generateSupportingFiles" to "true",
		"skipDefaultInterface" to "false",
		"documentationProvider" to "none",
		"unhandledException" to "true",
		"useResponseEntity" to "true",
		"useSpringController" to "true",
		"useSwaggerUI" to "true",
		"additionalModelTypeAnnotations" to "@jakarta.xml.bind.annotation.XmlRootElement"
	))
	globalProperties.set(mapOf(
		"apis" to "",
		"models" to "",
		"supportingFiles" to "ApiUtil.java,Application.java,configuration/OpenAPIDocumentationConfig.java"
	))
}

sourceSets {
	main {
		java {
			srcDirs("src/main/java", "${layout.buildDirectory.get()}/generated/src/main/java")
		}
	}
}

tasks {
//	named("compileJava") {
//		dependsOn(named("openApiGenerate"))
//	}

	withType<Test> {
		useJUnitPlatform()
	}
}