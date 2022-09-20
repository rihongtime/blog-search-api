package com.rihongo.external.model.response

data class BlogSearchResponse(
    val contentsList: List<Contents> = listOf(),
    val pagination: Pagination
)

data class Contents(
    val title: String,
    val contents: String,
    val url: String,
    val blogname: String,
    val datetime: String
)

data class Pagination(
    val sort: String,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long
)
