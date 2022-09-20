package com.rihongo.search.api.service.operator

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ServiceOperator {

    suspend fun <T : Any> execute(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        job: suspend () -> T,
        afterJob: suspend () -> Unit
    ): T {
        val result = job()
        withContext(dispatcher) {
            afterJob()
        }
        return result
    }

    suspend fun <T : Any> execute(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        validator: suspend () -> Boolean,
        job: suspend () -> T?,
        afterJob: suspend () -> Unit
    ): T? {
        check(validator())
        val result = job()
        withContext(dispatcher) {
            afterJob()
        }
        return result
    }
}
