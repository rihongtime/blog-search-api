package com.rihongo.external

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan(
    basePackages = ["com.rihongo.external.provider"]
)
class ExternalApiApplication

fun main(args: Array<String>) {
    runApplication<ExternalApiApplication>(*args)
}
