package com.rihongo.search.api.model.request

import com.rihongo.external.model.request.SearchRequest
import com.rihongo.search.api.model.enums.SearchSort

class BlogSearchRequest(
    val query: String,
    val sort: String,
    val page: Int,
    val size: Int
)

fun BlogSearchRequest.toSearchRequest() = SearchRequest(
    query = query,
    sort = sort,
    page = page,
    size = size
)

fun BlogSearchRequest.validate(): Boolean =
    (1..50).contains(size) &&
            (1..50).contains(page) &&
            SearchSort.values().any { it.name == sort }