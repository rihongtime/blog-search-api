package com.rihongo.external.provider.kakao.service

import com.rihongo.external.model.enums.SearchType
import com.rihongo.external.model.request.SearchRequest
import com.rihongo.external.model.response.BlogSearchResponse
import com.rihongo.external.model.response.Contents
import com.rihongo.external.model.response.Pagination
import com.rihongo.external.provider.kakao.model.enums.KakaoSearchSort
import com.rihongo.external.provider.kakao.model.response.KakaoBlogSearchResponse
import com.rihongo.external.provider.kakao.property.KakaoApiProperties
import com.rihongo.external.service.webclient.WebClientService
import com.rihongo.external.util.Logger
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.util.UriBuilder

@Service
class KakaoApiSearchService(
    private val kakaoApiProperties: KakaoApiProperties,
    private val webClientService: WebClientService
) {

    private val logger by Logger()

    suspend fun blog(searchRequest: SearchRequest): BlogSearchResponse {
        val response = webClientService.url(kakaoApiProperties.domain)
            .method(HttpMethod.GET)
            .path { uriBuilder: UriBuilder ->
                uriBuilder
                    .path(kakaoApiProperties.searchPath[SearchType.BLOG.value]!!)
                    .queryParam("query", searchRequest.query)
                    .queryParam("sort", KakaoSearchSort.valueOf(searchRequest.sort).value)
                    .queryParam("page", searchRequest.page)
                    .queryParam("size", searchRequest.size)
                    .build()
            }
            .body("")
            .header {
                it.set("Authorization", "KakaoAK ${kakaoApiProperties.key}")
            }
            .response()
            .awaitBody<KakaoBlogSearchResponse>()

        logger.info("Kakao response: $response")
        return BlogSearchResponse(
            contentsList = makeContentsList(response),
            pagination = Pagination(
                sort = searchRequest.sort,
                pageNumber = searchRequest.page,
                pageSize = searchRequest.size,
                totalElements = response.meta.totalCount
            )
        )
    }

    private fun makeContentsList(response: KakaoBlogSearchResponse) =
        response.documents.map {
            Contents(
                title = it.title,
                contents = it.contents,
                url = it.url,
                blogname = it.blogname,
                datetime = it.datetime
            )
        }

}
