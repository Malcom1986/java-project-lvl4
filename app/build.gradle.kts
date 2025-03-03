import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
  application
  id("checkstyle")
  id("io.freefair.lombok") version "8.2.2"
  id("com.github.ben-manes.versions") version "0.47.0"
  id("com.github.johnrengelman.shadow") version "8.1.1"
  jacoco
}

application {
  mainClass.set("hexlet.code.App")
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  implementation("com.h2database:h2:2.2.220")
  implementation("org.postgresql:postgresql:42.7.5")
  implementation("com.zaxxer:HikariCP:5.0.1")
  implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
  implementation("org.apache.commons:commons-text:1.10.0")
  implementation("gg.jte:jte:3.0.3")
  implementation("org.slf4j:slf4j-simple:2.0.7")
  implementation("io.javalin:javalin:5.6.2")
  implementation("io.javalin:javalin-bundle:5.6.2")
  implementation("io.javalin:javalin-rendering:5.6.2")

  testImplementation("org.assertj:assertj-core:3.23.1")
  testImplementation(platform("org.junit:junit-bom:5.10.0"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation ("com.squareup.okhttp3:mockwebserver:4.10.0")
  implementation ("org.jsoup:jsoup:1.14.3")
  implementation ("com.konghq:unirest-java:3.13.0")
}

tasks.jacocoTestReport {
  reports {
    xml.required.set(true)
  }
}

checkstyle {
  toolVersion = "10.3.3"
}

tasks.test {
  useJUnitPlatform()
  // https://technology.lastminute.com/junit5-kotlin-and-gradle-dsl/
  testLogging {
    exceptionFormat = TestExceptionFormat.FULL
    events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
    // showStackTraces = true
    // showCauses = true
    showStandardStreams = true
  }
}
