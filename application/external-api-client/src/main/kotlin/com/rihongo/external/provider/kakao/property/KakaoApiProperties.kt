package com.rihongo.external.provider.kakao.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "open-api.kakao")
class KakaoApiProperties(
    val domain: String,
    val key: String,
    val searchPath: Map<String, String>
)