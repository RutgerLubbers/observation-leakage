import org.springframework.boot.gradle.tasks.run.BootRun

group = "com.ilionx.poc"
version = "0.0.1-SNAPSHOT"

val javaVersion = JavaVersion.VERSION_17

val datasourceProxyVersion = "1.9"
val datasourceMicrometerVersion = "1.0.2"

//val springBootVersion = see the plugins section too
val springBootVersion = "3.1.3"

plugins {
    val springBootVersion = "3.1.3"
    val springDependencyManagementVersion = "1.1.3"

    java

    // Our tests are using groovy with the spock framework, this adds the compileTestGroovy task and the watch to the test/groovy folder
    groovy

    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version springDependencyManagementVersion

    id("io.beekeeper.gradle.plugins.dependency-updates") version "0.15.0"
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

configurations {
    all {
        exclude(group = "com.vaadin.external.google", module = "android-json")
        exclude(group = "org.slf4j", module = "slf4j-log4j12")
        exclude(group = "org.slf4j", module = "slf4j-jdk14")
        exclude(group = "log4j", module = "log4j")
        exclude(group = "org.apache.logging")
    }
}

dependencies {

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.graphql:spring-graphql:1.2.3")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
//    implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    implementation("net.ttddyy:datasource-proxy:${datasourceProxyVersion}")

    implementation("net.ttddyy.observation:datasource-micrometer:${datasourceMicrometerVersion}")
    implementation("net.ttddyy.observation:datasource-micrometer-spring-boot:${datasourceMicrometerVersion}")

    implementation("com.zaxxer:HikariCP")
    implementation("com.h2database:h2:2.2.220")
}

tasks.getByName<BootRun>("bootRun") {
    setEnableAssertions(true)
}


