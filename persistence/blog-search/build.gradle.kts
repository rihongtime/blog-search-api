dependencies {
    // jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.h2database:h2:2.1.214")
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

noArg {
    annotation("javax.persistence.Entity")
}

tasks {
    withType<Jar> {
        enabled = true
    }
    withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
        enabled = false
    }
}
