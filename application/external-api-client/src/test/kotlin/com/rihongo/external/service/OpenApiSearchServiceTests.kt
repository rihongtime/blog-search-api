package com.rihongo.external.service

import com.rihongo.external.model.request.SearchRequest
import com.rihongo.external.provider.kakao.property.KakaoApiProperties
import com.rihongo.external.provider.kakao.service.KakaoApiSearchService
import com.rihongo.external.provider.naver.property.NaverApiProperties
import com.rihongo.external.provider.naver.service.NaverApiSearchService
import com.rihongo.external.service.webclient.WebClientServiceImpl
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.`should be greater than`
import org.amshove.kluent.`should not be`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OpenApiSearchServiceTests {

    private lateinit var openApiSearchService: OpenApiSearchService
    private lateinit var kakaoApiSearchService: KakaoApiSearchService
    private lateinit var naverApiSearchService: NaverApiSearchService
    private lateinit var fallbackOpenApiSearchService: OpenApiSearchService
    private lateinit var fallbackKakaoApiSearchService: KakaoApiSearchService

    @BeforeEach
    fun setUp() {
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

        fallbackKakaoApiSearchService = spyk(
            objToCopy = KakaoApiSearchService(
                KakaoApiProperties(
                    domain = "https://dapi.kakao.com",
                    key = "wrongKey",
                    searchPath = mapOf("blog" to "/v2/search/blog")
                ),
                WebClientServiceImpl()
            ),
            recordPrivateCalls = true
        )

        fallbackOpenApiSearchService = spyk(
            objToCopy = OpenApiSearchServiceImpl(
                fallbackKakaoApiSearchService,
                naverApiSearchService
            ),
            recordPrivateCalls = true
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `openapi blog search request test`() = runTest {
        val searchRequest = SearchRequest(
            query = "test",
            sort = "ACCURACY",
            page = 1,
            size = 10,
        )

        val blogSearchResponse = openApiSearchService.blog(searchRequest)

        blogSearchResponse `should not be` null
        blogSearchResponse.contentsList.size `should be greater than` 0
        blogSearchResponse.pagination.totalElements `should be greater than` 0
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `openapi blog search request fallback test`() = runTest {
        val searchRequest = SearchRequest(
            query = "test",
            sort = "ACCURACY",
            page = 1,
            size = 10,
        )

        val blogSearchResponse = fallbackOpenApiSearchService.blog(searchRequest)

        blogSearchResponse `should not be` null
        blogSearchResponse.contentsList.size `should be greater than` 0
        blogSearchResponse.pagination.totalElements `should be greater than` 0
    }

}