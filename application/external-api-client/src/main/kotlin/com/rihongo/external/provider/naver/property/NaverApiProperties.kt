package com.rihongo.external.provider.naver.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "open-api.naver")
class NaverApiProperties(
    val domain: String,
    val clientId: String,
    val clientSecret: String,
    val searchPath: Map<String, String>
)