dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-configuration-processor:2.7.3")
    kapt("org.springframework.boot:spring-boot-configuration-processor:2.7.3")

    // open api
    implementation("org.springdoc:springdoc-openapi-webflux-ui:1.6.11")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.6.11")

    // jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.h2database:h2:2.1.214")

    // redis
    implementation("it.ozimov:embedded-redis:0.7.1")
    implementation("org.redisson:redisson-spring-boot-starter:3.17.6")

    // module
    implementation(project(":application:external-api-client"))
    implementation(project(":persistence:blog-search"))
}

springBoot {
    mainClass.value("com.rihongo.search.BlogSearchApiApplicationKt")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = true
    mainClass.value("com.rihongo.search.BlogSearchApiApplicationKt")
}

tasks.jar {
    enabled = false
}