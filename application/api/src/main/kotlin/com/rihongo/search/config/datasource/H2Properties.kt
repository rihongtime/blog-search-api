package com.rihongo.search.config.datasource

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "spring.h2")
class H2Properties(
    val port: String
)
