dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-configuration-processor:2.7.3")
    kapt("org.springframework.boot:spring-boot-configuration-processor:2.7.3")
}

tasks {
    withType<Jar> {
        enabled = true
    }
    withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
        enabled = false
    }
}
