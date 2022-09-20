package com.rihongo.search.api.service.search

import com.rihongo.external.model.response.BlogSearchResponse
import com.rihongo.external.service.OpenApiSearchService
import com.rihongo.persistence.blog.search.service.BlogSearchCounterService
import com.rihongo.search.api.model.request.BlogSearchRequest
import com.rihongo.search.api.model.request.toSearchRequest
import com.rihongo.search.api.model.request.validate
import com.rihongo.search.api.model.response.BlogSearchRankResponse
import com.rihongo.search.api.model.response.toResponse
import com.rihongo.search.api.service.operator.RedisOperator
import com.rihongo.search.api.service.operator.ServiceOperator.execute
import com.rihongo.search.util.Logger
import org.springframework.stereotype.Service
import reactor.core.scheduler.Schedulers

@Service
class SearchService(
    private val openApiSearchService: OpenApiSearchService,
    private val redisOperator: RedisOperator,
    private val blogSearchCounterService: BlogSearchCounterService
) {

    private val logger by Logger()

    suspend fun blog(blogSearchRequest: BlogSearchRequest): BlogSearchResponse? = execute(
        validator = { blogSearchRequest.validate() },
        job = {
            logger.info("job execute in Thread.currentThread().name : ${Thread.currentThread().name}")
            openApiSearchService.blog(searchRequest = blogSearchRequest.toSearchRequest())
        },
        afterJob = {
            redisOperator.addSearchScore(keyword = blogSearchRequest.query)
        }
    )

    suspend fun getRank(): List<BlogSearchRankResponse> = execute(
        job = {
            redisOperator.getRankInRedis() ?: blogSearchCounterService.getRankInDatabase()
                .map { it.toResponse() }
        },
        afterJob = {
            databaseToRedis()
        }
    )


    private suspend fun databaseToRedis() {
        redisOperator.isExistSearchScore().filter {
            !it
        }.publishOn(Schedulers.boundedElastic()).map {
            blogSearchCounterService.getRankMapInDatabase()
        }.mapNotNull {
            redisOperator.addAllSearchScore(it)
        }.subscribe()
    }
}