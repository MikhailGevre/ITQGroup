plugins {
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.6")
    }
}

dependencies {
    /*
    Resilience4j
    */
    implementation("io.github.resilience4j:resilience4j-reactor:2.1.0")
    implementation("io.github.resilience4j:resilience4j-spring-boot3:2.1.0")
    implementation("io.github.resilience4j:resilience4j-retry:2.1.0")
    implementation("io.github.resilience4j:resilience4j-circuitbreaker:2.1.0")
    /*
    Web
     */
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
    /*
    JPA + Hibernate
    */
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    /*
    MapStruct
     */
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    /*
    PostgresSQL
    */
    runtimeOnly("org.postgresql:postgresql")

    implementation("org.liquibase:liquibase-core")

    /*
    Lombok
    */
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    /*
    Test
     */
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

     /*
     Testcontainers
     */
    testImplementation ("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation ("org.testcontainers:testcontainers:2.0.4")
    testImplementation ("org.testcontainers:testcontainers-junit-jupiter:2.0.4")
    testImplementation("org.testcontainers:testcontainers-postgresql:2.0.4")
    testImplementation("org.postgresql:postgresql:42.7.3")



}

tasks.test {
    useJUnitPlatform()
}