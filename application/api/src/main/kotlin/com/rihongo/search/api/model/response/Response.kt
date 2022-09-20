package com.rihongo.search.api.model.response

import com.rihongo.search.api.model.enums.ErrorCode
import com.rihongo.search.api.model.enums.ResponseCode

class SingleResponse<out T>(
    val result: ResponseCode? = ResponseCode.SUCCESS,
    val data: T? = null
)

class MultiResponse<out T>(
    val result: ResponseCode? = ResponseCode.SUCCESS,
    val data: List<T>? = null
)

class ErrorResponse(
    val result: ResponseCode? = ResponseCode.ERROR,
    val data: ErrorData
)

class ErrorData(
    val code: ErrorCode,
    val message: String?
)
