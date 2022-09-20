package com.rihongo.search.controller

import com.rihongo.external.model.response.BlogSearchResponse
import com.rihongo.search.api.model.enums.ErrorCode
import com.rihongo.search.api.model.enums.ResponseCode
import com.rihongo.search.api.model.enums.SearchSort
import com.rihongo.search.api.model.response.BlogSearchRankResponse
import com.rihongo.search.api.model.response.ErrorResponse
import com.rihongo.search.api.model.response.MultiResponse
import com.rihongo.search.api.model.response.SingleResponse
import com.rihongo.search.util.GsonUtil.gson
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be greater than`
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.properties.Delegates

@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class MembershipIntegrationTests(@Autowired val client: WebTestClient) {

    companion object {
        private var query by Delegates.notNull<String>()
        private var sort by Delegates.notNull<String>()
        private var page by Delegates.notNull<Int>()
        private var size by Delegates.notNull<Int>()

        @BeforeAll
        @JvmStatic
        fun setUp() {
            query = "test"
            sort = SearchSort.ACCURACY.name
            page = 1
            size = 10
        }
    }

    @Rollback(false)
    @Order(1)
    @Test
    fun `blog search request test`() {
        val queryString = "?query=$query&sort=$sort&page=$page&size=$size"

        val responseBody: SingleResponse<BlogSearchResponse> = client.get()
            .uri("/v1/search/blog$queryString")
            .exchange()
            .expectStatus().isOk
            .expectBody(SingleResponse::class.java)
            .returnResult().responseBody as SingleResponse<BlogSearchResponse>

        responseBody.result `should be` ResponseCode.SUCCESS
        val json = gson.toJson(responseBody.data)
        val data = gson.fromJson(json, BlogSearchResponse::class.java)
        println(data)
        data.pagination.totalElements `should be greater than` 0
    }

    @Order(2)
    @Test
    fun `get blog search keyword rank test`() {
        val responseBody: MultiResponse<BlogSearchRankResponse> = client.get()
            .uri("/v1/search/blog/rank")
            .exchange()
            .expectStatus().isOk
            .expectBody(MultiResponse::class.java)
            .returnResult().responseBody as MultiResponse<BlogSearchRankResponse>

        responseBody.result `should be` ResponseCode.SUCCESS
    }

    @Order(3)
    @Test
    fun `blog search request wrong sort value test`() {
        val queryString = "?query=$query&sort=wrong&page=$page&size=$size"

        val responseBody: ErrorResponse = client.get()
            .uri("/v1/search/blog$queryString")
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(ErrorResponse::class.java)
            .returnResult().responseBody as ErrorResponse

        responseBody.result `should be` ResponseCode.ERROR
        responseBody.data.code `should be equal to` ErrorCode.ILLEGAL_STATE
    }

    @Order(4)
    @Test
    fun `blog search request default condition test`() {
        val queryString = "?query=$query"

        val responseBody: SingleResponse<BlogSearchResponse> = client.get()
            .uri("/v1/search/blog$queryString")
            .exchange()
            .expectStatus().isOk
            .expectBody(SingleResponse::class.java)
            .returnResult().responseBody as SingleResponse<BlogSearchResponse>

        responseBody.result `should be` ResponseCode.SUCCESS
        val json = gson.toJson(responseBody.data)
        val data = gson.fromJson(json, BlogSearchResponse::class.java)
        println(data)
        data.pagination.totalElements `should be greater than` 0
    }

}
