package com.rihongo.external.service

import com.rihongo.external.ExternalApiApplication
import com.rihongo.external.model.request.SearchRequest
import com.rihongo.external.provider.kakao.service.KakaoApiSearchService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.`should not be`
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [ExternalApiApplication::class])
class KakaoApiSearchServiceTests(
    @Autowired val kakaoApiSearchService: KakaoApiSearchService
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `blog search request test`() = runTest {
        // given
        val searchRequest = SearchRequest(
            query = "test",
            sort = "ACCURACY",
            page = 1,
            size = 10,
        )

        // when
        val response = kakaoApiSearchService.blog(searchRequest)

        // then
        response `should not be` null
    }

}