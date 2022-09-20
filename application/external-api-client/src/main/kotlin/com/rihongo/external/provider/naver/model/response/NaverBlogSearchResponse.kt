package com.rihongo.external.provider.naver.model.response

data class NaverBlogSearchResponse(
    val total: Long,
    val start: Int,
    val display: Int,
    val items: List<Item>
)

data class Item(
    val title: String,
    val link: String,
    val description: String,
    val bloggername: String,
    val postdate: String,
)