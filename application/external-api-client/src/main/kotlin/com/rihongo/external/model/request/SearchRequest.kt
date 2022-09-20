package com.rihongo.external.model.request

class SearchRequest(
    val query: String,
    val sort: String,
    val page: Int = 1,
    val size: Int = 10
)