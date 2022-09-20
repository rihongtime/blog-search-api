package com.rihongo.search.service

import com.rihongo.external.provider.kakao.property.KakaoApiProperties
import com.rihongo.external.provider.kakao.service.KakaoApiSearchService
import com.rihongo.external.provider.naver.property.NaverApiProperties
import com.rihongo.external.provider.naver.service.NaverApiSearchService
import com.rihongo.external.service.OpenApiSearchService
import com.rihongo.external.service.OpenApiSearchServiceImpl
import com.rihongo.external.service.webclient.WebClientServiceImpl
import com.rihongo.persistence.blog.search.entity.BlogSearchCounter
import com.rihongo.persistence.blog.search.service.BlogSearchCounterService
import com.rihongo.search.api.model.request.BlogSearchRequest
import com.rihongo.search.api.model.response.BlogSearchRankResponse
import com.rihongo.search.api.service.operator.RedisOperator
import com.rihongo.search.api.service.search.SearchService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.`should be greater than`
import org.amshove.kluent.`should not be`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SearchServiceTests {

    private lateinit var openApiSearchService: OpenApiSearchService
    private lateinit var kakaoApiSearchService: KakaoApiSearchService
    private lateinit var naverApiSearchService: NaverApiSearchService

    private lateinit var redisOperator: RedisOperator
    private lateinit var blogSearchCounterService: BlogSearchCounterService
    private lateinit var searchService: SearchService

    @BeforeEach
    fun setUp() {
        redisOperator = mockk()
        blogSearchCounterService = mockk()

        kakaoApiSearchService = spyk(
            objToCopy = KakaoApiSearchService(
                KakaoApiProperties(
                    domain = "https://dapi.kakao.com",
                    key = "568a7c9ecb4f354b000ffa57c4725d9f",
                    searchPath = mapOf("blog" to "/v2/search/blog")
                ),
                WebClientServiceImpl()
            ),
            recordPrivateCalls = true
        )

        naverApiSearchService = spyk(
            objToCopy = NaverApiSearchService(
                NaverApiProperties(
                    domain = "https://openapi.naver.com",
                    clientId = "ropub08ieeRXo4xTMHmI",
                    clientSecret = "oLDrfHeQlJ",
                    searchPath = mapOf("blog" to "/v1/search/blog.json")
                ),
                WebClientServiceImpl()
            ),
            recordPrivateCalls = true
        )

        openApiSearchService = spyk(
            objToCopy = OpenApiSearchServiceImpl(
                kakaoApiSearchService,
                naverApiSearchService
            ),
            recordPrivateCalls = true
        )

        searchService = spyk(
            objToCopy = SearchService(
                openApiSearchService,
                redisOperator,
                blogSearchCounterService
            ),
            recordPrivateCalls = true
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `blog search request test`() = runTest {
        val blogSearchRequest = BlogSearchRequest(
            query = "test",
            sort = "RECENCY",
            page = 1,
            size = 10
        )

        every {
            redisOperator.addSearchScore(keyword = blogSearchRequest.query)
        } returns Unit

        val blogSearchResponse = searchService.blog(blogSearchRequest)

        blogSearchResponse `should not be` null
        blogSearchResponse?.contentsList?.size?.`should be greater than`(0)
        blogSearchResponse?.pagination?.totalElements?.`should be greater than`(0)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `blog search get rank not exist redis test`() = runTest {
        coEvery {
            redisOperator.getRankInRedis()
        } returns listOf(
            BlogSearchRankResponse(
                keyword = "test",
                count = 12
            )
        )

        coEvery {
            blogSearchCounterService.getRankInDatabase()
        } returns listOf(
            BlogSearchCounter(
                keyword = "test",
                count = 12
            )
        )

        every {
            redisOperator.isExistSearchScore()
        } returns mono {
            false
        }

        every {
            blogSearchCounterService.getRankMapInDatabase()
        } returns mapOf("test" to 12.0)

        every {
            redisOperator.addAllSearchScore(mapOf("test" to 12.0))
        } returns mono {
            10
        }

        val blogSearchResponse = searchService.getRank()

        blogSearchResponse `should not be` null
        blogSearchResponse.size `should be greater than` 0
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `blog search get rank exist redis test`() = runTest {
        coEvery {
            redisOperator.getRankInRedis()
        } returns listOf(
            BlogSearchRankResponse(
                keyword = "test",
                count = 12
            )
        )

        coEvery {
            blogSearchCounterService.getRankInDatabase()
        } returns listOf(
            BlogSearchCounter(
                keyword = "test",
                count = 12
            )
        )

        every {
            redisOperator.isExistSearchScore()
        } returns mono {
            true
        }

        every {
            blogSearchCounterService.getRankMapInDatabase()
        } returns mapOf("test" to 12.0)

        every {
            redisOperator.addAllSearchScore(mapOf("test" to 12.0))
        } returns mono {
            10
        }

        val blogSearchResponse = searchService.getRank()

        blogSearchResponse `should not be` null
        blogSearchResponse.size `should be greater than` 0
    }

}