package com.rihongo.external.provider.kakao.model.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

data class KakaoBlogSearchResponse(
    val meta: Meta,
    val documents: List<Document> = listOf()
)

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Meta(
    val totalCount: Long,
    val pageableCount: Int,
    val isEnd: Boolean
)

data class Document(
    val title: String,
    val contents: String,
    val url: String,
    val blogname: String,
    val thumbnail: String,
    val datetime: String
)
