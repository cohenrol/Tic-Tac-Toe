plugins {
    java
    id("org.springframework.boot") version "4.0.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.cohenrol.ap1jvt03"
version = "0.0.1"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
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

val mapstructVersion = "1.6.3"
val lombokVersion = "1.18.46"

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-json")

    // Lombok (Оставляем compileOnly)
    compileOnly("org.projectlombok:lombok:$lombokVersion")

    // MapStruct (Оставляем implementation)
    implementation("org.mapstruct:mapstruct:$mapstructVersion")

    // СТРОГИЙ ПОРЯДОК ПРОЦЕССОРОВ АННОТАЦИЙ:
    // 1. Сначала Lombok генерирует геттеры/сеттеры
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    // 2. Связующий мост (binding) ставится СТРОГО между ними
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    // 3. Последним идет MapStruct, который уже видит сгенерированный Lombok-код
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")

    runtimeOnly("org.postgresql:postgresql")

    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
}

tasks.withType<Test> {
    useJUnitPlatform()
}