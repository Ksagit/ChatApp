plugins {
    java
    id("io.ktor.plugin") version "2.3.0"
    application
}

group = "gui"

repositories {
    mavenCentral()

}

dependencies {

    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("org.mockito:mockito-core:3.+")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("gui.Main")
}

val log4jConfigDir = file("resources")

tasks {
    val copyLog4jConfig by creating(Copy::class) {
        from(log4jConfigDir) {
            include("client_log4j_config.xml")
        }
        into("$buildDir/resources/main")
    }

    withType<JavaExec> {
        systemProperty("log4j.configurationFile", "$buildDir/resources/main/client_log4j_config.xml")
    }

    compileJava {
        dependsOn(copyLog4jConfig)
    }
}
