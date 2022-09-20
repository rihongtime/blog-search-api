package com.rihongo.search.api.service.operator

import com.rihongo.search.api.model.response.BlogSearchRankResponse
import com.rihongo.search.util.Logger
import kotlinx.coroutines.reactor.awaitSingle
import org.redisson.api.RMapCacheReactive
import org.redisson.api.RScoredSortedSetReactive
import org.redisson.api.RedissonReactiveClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RedisOperator(
    private val redissonReactiveClient: RedissonReactiveClient,
    private val blogSearchCounterRMapCache: RMapCacheReactive<String, Long>,
) {
    private val logger by Logger()

    companion object {
        private const val RANK_START_INDEX = 0
        private const val RANK_END_INDEX = 10
        private const val INCREASE_COUNT = 1
        private const val SORTED_SET_NAME = "blogSearchCounter"
    }

    fun addSearchScore(keyword: String) {
        val scoredSet: RScoredSortedSetReactive<String> = redissonReactiveClient.getScoredSortedSet(SORTED_SET_NAME)
        scoredSet.addScore(keyword, INCREASE_COUNT).flatMap {
            blogSearchCounterRMapCache.put(keyword, it.toLong())
        }.subscribe()
    }

    fun isExistSearchScore(): Mono<Boolean> =
        redissonReactiveClient.getScoredSortedSet<String?>(SORTED_SET_NAME).isExists

    fun addAllSearchScore(map: Map<String, Double>): Mono<Int>? {
        val set = redissonReactiveClient.getScoredSortedSet<String?>(SORTED_SET_NAME)
        return set.addAll(map)
    }

    suspend fun getRankInRedis(): List<BlogSearchRankResponse>? =
        redissonReactiveClient.getScoredSortedSet<String?>(SORTED_SET_NAME)
            ?.let {
                it.entryRangeReversed(
                    RANK_START_INDEX,
                    RANK_END_INDEX
                ).awaitSingle()
                    .map { scoredEntry ->
                        BlogSearchRankResponse(
                            keyword = scoredEntry.value,
                            count = scoredEntry.score.toLong()
                        )
                    }
            }

}