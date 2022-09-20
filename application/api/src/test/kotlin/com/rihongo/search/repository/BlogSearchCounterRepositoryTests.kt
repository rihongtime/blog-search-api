package com.rihongo.search.repository

import com.rihongo.persistence.blog.search.entity.BlogSearchCounter
import com.rihongo.persistence.blog.search.repository.BlogSearchCounterRepository
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
class BlogSearchCounterRepositoryTests(
    @Autowired
    val blogSearchCounterRepository: BlogSearchCounterRepository
) {

    @Test
    fun `keyword rank save and get`() {
        val blogSearchCounter = BlogSearchCounter(
            keyword = "test",
            count = 10
        )
        blogSearchCounterRepository.save(blogSearchCounter)
        val rank: List<BlogSearchCounter> = blogSearchCounterRepository.findTop10ByOrderByCountDesc()

        blogSearchCounter.count `should be equal to` rank[0].count
        blogSearchCounter.keyword `should be equal to` rank[0].keyword
    }
}