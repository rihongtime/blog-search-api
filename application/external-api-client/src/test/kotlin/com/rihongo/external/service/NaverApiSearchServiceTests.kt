package com.rihongo.external.service

import com.rihongo.external.ExternalApiApplication
import com.rihongo.external.model.request.SearchRequest
import com.rihongo.external.provider.naver.service.NaverApiSearchService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.`should not be`
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [ExternalApiApplication::class])
class NaverApiSearchServiceTests(
    @Autowired val naverApiSearchService: NaverApiSearchService
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `blog search request test`() = runTest {
        // given
        val searchRequest = SearchRequest(
            query = "test",
            sort = "RECENCY",
            page = 1,
            size = 10,
        )

        // when
        val response = naverApiSearchService.blog(searchRequest)

        // then
        response `should not be` null
    }

}