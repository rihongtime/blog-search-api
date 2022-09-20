package com.rihongo.search.api.controller

import com.rihongo.external.model.response.BlogSearchResponse
import com.rihongo.search.api.model.request.BlogSearchRequest
import com.rihongo.search.api.model.response.BlogSearchRankResponse
import com.rihongo.search.api.model.response.MultiResponse
import com.rihongo.search.api.model.response.SingleResponse
import com.rihongo.search.api.service.search.SearchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/search")
class BlogSearchController(
    private val searchService: SearchService
) {

    @GetMapping("/blog")
    suspend fun blog(
        @RequestParam(required = true) query: String,
        @RequestParam(required = false, defaultValue = "ACCURACY") sort: String = "ACCURACY",
        @RequestParam(required = false, defaultValue = "1") page: Int = 1,
        @RequestParam(required = false, defaultValue = "10") size: Int = 10
    ): SingleResponse<BlogSearchResponse> =
        SingleResponse(data = searchService.blog(BlogSearchRequest(
            query, sort, page, size
        )))

    @GetMapping("/blog/rank")
    suspend fun keywordRank(): MultiResponse<BlogSearchRankResponse> =
        MultiResponse(data = searchService.getRank())
}
