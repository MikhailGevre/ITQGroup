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

dependencies {
    /*
    Web
     */
    implementation("org.springframework.boot:spring-boot-starter-web")

    /*
    JPA + Hibernate
    */
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    /*
    PostgreSQL
    */
    runtimeOnly("org.postgresql:postgresql")

    implementation("org.liquibase:liquibase-core")

    /*
    Lombok
    */
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    /*
    Test
     */
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}