@file:OptIn(ExperimentalCoroutinesApi::class)

package com.rihongo.search.service

import com.rihongo.search.api.model.response.BlogSearchRankResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.redisson.api.RScoredSortedSetReactive
import org.redisson.api.RedissonReactiveClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class RedisOperatorTests(
    @Autowired
    val redissonReactiveClient: RedissonReactiveClient
){
    companion object {
        lateinit var scoredSet: RScoredSortedSetReactive<String>
        private const val RANK_START_INDEX = 0
        private const val RANK_END_INDEX = 9
        private const val SORTED_SET_NAME = "blogSearchCounter"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Rollback(false)
    @Order(1)
    fun `keyword rank save and get`() = runTest {
        val queryList: Map<Int, String> = mapOf(
            13 to "top", // 1
            12 to "second", // 2
            11 to "a", // 3
            10 to "3", // 4
            9 to "d", // 5
            8 to "c", // 6
            7 to "e", // 7
            5 to "66", // 8
            4 to "778", // 9
            3 to "top10", // 10
            2 to "2",
            1 to "1",
        )

        // save
        saveSearchCount(queryList)
    }

    private suspend fun saveSearchCount(queryList: Map<Int, String>) {
        scoredSet = redissonReactiveClient.getScoredSortedSet(SORTED_SET_NAME)

        queryList.map { (k, v) ->
            (1..k step 1).forEach { _ ->
                scoredSet.addScore(v, 1).subscribe()
            }
        }

        scoredSet.count(0.0, true, 100.0, true).map {
            it `should be equal to` queryList.count()
        }.subscribe()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Order(1)
    @Test
    fun getRank() = runTest {
        scoredSet = redissonReactiveClient.getScoredSortedSet(SORTED_SET_NAME)

        val result = scoredSet.entryRangeReversed(RANK_START_INDEX, RANK_END_INDEX)
            .awaitSingle()
            .map { scoredEntry ->
                BlogSearchRankResponse(
                    keyword = scoredEntry.value,
                    count = scoredEntry.score.toLong()
                )
            }
        println("result $result")

        result.size `should be equal to` 10
        result[0].keyword `should be equal to` "top"
        result[1].keyword `should be equal to` "second"
        result[9].keyword `should be equal to` "top10"
    }
}