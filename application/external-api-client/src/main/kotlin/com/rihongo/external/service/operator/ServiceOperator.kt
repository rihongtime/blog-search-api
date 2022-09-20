package com.rihongo.external.service.operator

import org.springframework.web.reactive.function.client.WebClientResponseException

object ServiceOperator {

    suspend fun <R: Any, T : Any> execute(
        request: R ,
        job: suspend (R) -> T,
        fallback: suspend (R) -> T,
    ): T {
        return try {
            job(request)
        } catch (e: WebClientResponseException) {
            fallback(request)
        }
    }
}
