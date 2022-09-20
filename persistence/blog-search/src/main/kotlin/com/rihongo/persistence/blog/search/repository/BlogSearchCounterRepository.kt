package com.rihongo.persistence.blog.search.repository

import com.rihongo.persistence.blog.search.entity.BlogSearchCounter
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BlogSearchCounterRepository : JpaRepository<BlogSearchCounter, String> {

    fun findTop10ByOrderByCountDesc(): List<BlogSearchCounter>

}
