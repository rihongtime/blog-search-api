package com.rihongo.external.service

import com.rihongo.external.model.request.SearchRequest
import com.rihongo.external.model.response.BlogSearchResponse
import org.springframework.stereotype.Service

@Service
interface OpenApiSearchService {
    suspend fun blog(searchRequest: SearchRequest): BlogSearchResponse
}
