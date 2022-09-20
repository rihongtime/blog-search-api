package com.rihongo.search.config.redis

import com.rihongo.persistence.blog.search.entity.BlogSearchCounter
import com.rihongo.persistence.blog.search.repository.BlogSearchCounterRepository
import com.rihongo.search.util.PortCheckUtil.findAvailablePort
import com.rihongo.search.util.PortCheckUtil.isRunning
import org.redisson.Redisson
import org.redisson.api.MapOptions
import org.redisson.api.RMapCacheReactive
import org.redisson.api.RedissonReactiveClient
import org.redisson.api.map.MapWriter
import org.redisson.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.Transactional
import redis.embedded.RedisServer
import java.io.IOException
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Configuration
class RedisConfig(
    private val redisProperties: RedisProperties,
    private val blogSearchCounterRepository: BlogSearchCounterRepository
) {

    private var redisServer: RedisServer? = null

    @PostConstruct
    @Throws(IOException::class)
    fun redisServer() {
        val redisPort = if (isRunning(redisProperties.port)) findAvailablePort() else redisProperties.port

        redisServer = RedisServer.builder()
            .port(redisPort)
            .setting("maxmemory 128M")
            .build()
        redisServer?.start()

        val config = Config()
        config.useSingleServer().address = "redis://${redisProperties.host}:$redisPort"
    }

    @PreDestroy
    fun stopRedis() {
        redisServer?.stop()
    }

    @Bean
    fun redissonReactiveClient(): RedissonReactiveClient =
        Redisson.create().reactive()

    @Bean
    fun blogSearchCounterRMapCache(redissonReactiveClient: RedissonReactiveClient): RMapCacheReactive<String, Long> {
        return redissonReactiveClient.getMapCache(
            "BlogSearchCounter",
            MapOptions.defaults<String, Long>()
                .writer(getBlogSearchCounterWriter())
                .writeMode(MapOptions.WriteMode.WRITE_BEHIND)
                .writeBehindBatchSize(5000)
                .writeBehindDelay(3000)
        )
    }

    @Transactional
    fun getBlogSearchCounterWriter(): MapWriter<String, Long> {
        return object : MapWriter<String, Long> {
            override fun write(map: Map<String, Long>?) {
                map?.forEach {
                    blogSearchCounterRepository.save(
                        BlogSearchCounter(
                            keyword = it.key,
                            count = it.value
                        )
                    )
                }
            }
            override fun delete(keys: MutableCollection<String>) {
                keys.forEach {
                    blogSearchCounterRepository.deleteById(it)
                }
            }
        }
    }
}
