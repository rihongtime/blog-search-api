package com.rihongo.external.service

import com.rihongo.external.model.request.SearchRequest
import com.rihongo.external.model.response.BlogSearchResponse
import com.rihongo.external.provider.kakao.service.KakaoApiSearchService
import com.rihongo.external.provider.naver.service.NaverApiSearchService
import com.rihongo.external.service.operator.ServiceOperator.execute
import com.rihongo.external.util.Logger
import org.springframework.stereotype.Service

@Service
class OpenApiSearchServiceImpl(
    private val kakaoApiSearchService: KakaoApiSearchService,
    private val naverApiSearchService: NaverApiSearchService,
) : OpenApiSearchService {

    private val logger by Logger()

    override suspend fun blog(searchRequest: SearchRequest): BlogSearchResponse = execute(
        searchRequest,
        job = {
            kakaoApiSearchService.blog(searchRequest)
        },
        fallback = {
            logger.warn("fallback naver")
            naverApiSearchService.blog(searchRequest)
        }
    )
}