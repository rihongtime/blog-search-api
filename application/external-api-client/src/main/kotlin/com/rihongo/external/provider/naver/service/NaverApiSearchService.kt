package com.rihongo.external.provider.naver.service

import com.rihongo.external.model.enums.SearchType
import com.rihongo.external.model.request.SearchRequest
import com.rihongo.external.model.response.BlogSearchResponse
import com.rihongo.external.model.response.Contents
import com.rihongo.external.model.response.Pagination
import com.rihongo.external.provider.naver.model.enums.NaverSearchSort
import com.rihongo.external.provider.naver.model.response.NaverBlogSearchResponse
import com.rihongo.external.provider.naver.property.NaverApiProperties
import com.rihongo.external.service.webclient.WebClientService
import com.rihongo.external.util.Logger
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.util.UriBuilder

@Service
class NaverApiSearchService(
    private val naverApiProperties: NaverApiProperties,
    private val webClientService: WebClientService
) {

    private val logger by Logger()

    companion object {
        private const val CLIENT_ID_HEADER_NAME = "X-Naver-Client-Id"
        private const val CLIENT_SECRET_HEADER_NAME = "X-Naver-Client-Secret"
    }

    suspend fun blog(searchRequest: SearchRequest): BlogSearchResponse {
        val response = webClientService.url(naverApiProperties.domain)
            .method(HttpMethod.GET)
            .path { uriBuilder: UriBuilder ->
                uriBuilder
                    .path(naverApiProperties.searchPath[SearchType.BLOG.value]!!)
                    .queryParam("query", searchRequest.query)
                    .queryParam("sort", NaverSearchSort.valueOf(searchRequest.sort).value)
                    .queryParam("start", searchRequest.page)
                    .queryParam("display", searchRequest.size)
                    .build()
            }
            .body("")
            .header {
                it.set(CLIENT_ID_HEADER_NAME, naverApiProperties.clientId)
                it.set(CLIENT_SECRET_HEADER_NAME, naverApiProperties.clientSecret)
            }
            .response()
            .awaitBody<NaverBlogSearchResponse>()

        logger.info("naver response: $response")

        return BlogSearchResponse(
            contentsList = makeContentsList(response),
            pagination = Pagination(
                sort = searchRequest.sort,
                pageNumber = searchRequest.page,
                pageSize = searchRequest.size,
                totalElements = response.total
            )
        )
    }

    private fun makeContentsList(response: NaverBlogSearchResponse) =
        response.items.map {
            Contents(
                title = it.title,
                contents = it.description,
                url = it.link,
                blogname = it.bloggername,
                datetime = it.postdate
            )
        }

}
