package com.rihongo.persistence.blog.search.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "blog_search_counter")
class BlogSearchCounter(
    @Id
    val keyword: String,
    val count: Long = 0
)
