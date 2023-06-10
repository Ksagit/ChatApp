plugins {
    java
    id("io.ktor.plugin") version "2.3.0"
    application
}

group = "server"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("org.mockito:mockito-core:3.12.4")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("server.Main")
}

val log4jConfigDir = file("resources")

tasks {
    val copyLog4jConfig by creating(Copy::class) {
        from(log4jConfigDir) {
            include("server_log4j_config.xml")
        }
        into("$buildDir/resources/main")
    }

    val testJar by creating(Jar::class) {
        from(sourceSets["test"].output)
        archiveClassifier.set("tests")
    }

    withType<Test> {
        systemProperty("log4j.configurationFile", "$buildDir/resources/main/server_log4j_config.xml")
        dependsOn(copyLog4jConfig)
    }

    compileJava {
        dependsOn(copyLog4jConfig)
    }
}
