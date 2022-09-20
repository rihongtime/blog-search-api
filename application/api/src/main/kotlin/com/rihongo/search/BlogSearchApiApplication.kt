package com.rihongo.search

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(
    scanBasePackages = ["com.rihongo.search", "com.rihongo.external", "com.rihongo.persistence"]
)
@ConfigurationPropertiesScan(
    basePackages = ["com.rihongo.search", "com.rihongo.external"]
)
@EnableJpaRepositories(
    basePackages = ["com.rihongo.persistence"]
)
@EntityScan(
    basePackages = ["com.rihongo.persistence.blog.search.entity"]
)
class BlogSearchApiApplication

fun main(args: Array<String>) {
    runApplication<BlogSearchApiApplication>(*args)
}
