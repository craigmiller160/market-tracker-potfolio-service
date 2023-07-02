import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.diffplug.gradle.spotless.SpotlessExtension

val projectGroup: String by project
val projectVersion: String by project

plugins {
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm")
    kotlin("plugin.spring")
    id("io.craigmiller160.gradle.defaults") version "1.3.0-SNAPSHOT"
    id("com.diffplug.spotless") version "6.17.0"
    `maven-publish`
}

dependencyManagement {
    imports {
        mavenBom("org.springdoc:springdoc-openapi:2.0.3")
    }
}

group = projectGroup
version = projectVersion
java.sourceCompatibility = JavaVersion.VERSION_19

dependencies {
    val kotlinResultVersion: String by project
    val kotestVersion: String by project

    implementation("io.craigmiller160:spring-keycloak-oauth2-resource-server:1.0.0")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.postgresql:r2dbc-postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.craigmiller160:testcontainers-common:1.2.0")
    implementation("org.flywaydb:flyway-core")
    implementation("io.github.craigmiller160:spring-fp-result-kt:2.0.0")
    implementation("io.arrow-kt:arrow-core:1.1.5")

    testImplementation("com.squareup.okhttp3:mockwebserver:4.10.0")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.3.5")
    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("io.kotest.extensions:kotest-assertions-arrow:1.3.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xcontext-receivers")
        jvmTarget = "19"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

configure<SpotlessExtension> {
    kotlin {
        ktfmt("0.43")
    }
}

//tasks.withType(PublishToMavenRepository::class.java) {
//    this.dependsOn(tasks.test)
//    val publishToMavenTask = this
//    tasks.withType(Test::class.java) {
//        publishToMavenTask.mustRunAfter(this)
//    }
//}

//afterEvaluate {
//    tasks.getByName("publish«market-tracker-portfolio-service»PublicationTo«repository-name»Repository").dependsOn(tasks.test)
//}
