package com.rihongo.search.api.model.response

import com.rihongo.persistence.blog.search.entity.BlogSearchCounter

data class BlogSearchRankResponse(
    val keyword: String,
    val count: Long
)

fun BlogSearchCounter.toResponse() =
    BlogSearchRankResponse(
        keyword, count
    )