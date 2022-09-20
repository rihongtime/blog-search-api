package com.rihongo.persistence.blog.search.service

import com.rihongo.persistence.blog.search.entity.BlogSearchCounter
import com.rihongo.persistence.blog.search.repository.BlogSearchCounterRepository
import org.springframework.stereotype.Service

@Service
class BlogSearchCounterService(
    private val blogSearchCounterRepository: BlogSearchCounterRepository
) {

    fun getRankMapInDatabase(): Map<String, Double> =
        blogSearchCounterRepository.findAll()
            .associate {
                Pair(it.keyword, it.count.toDouble())
            }

    fun getRankInDatabase(): List<BlogSearchCounter> =
        blogSearchCounterRepository.findTop10ByOrderByCountDesc()
}