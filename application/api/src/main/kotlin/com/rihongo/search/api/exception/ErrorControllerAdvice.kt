package com.rihongo.search.api.exception

import com.rihongo.search.api.model.enums.ErrorCode
import com.rihongo.search.api.model.response.ErrorData
import com.rihongo.search.api.model.response.ErrorResponse
import com.rihongo.search.util.Logger
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.reactive.function.client.WebClientException
import org.springframework.web.server.ServerWebExchange

@RestControllerAdvice
class ErrorControllerAdvice {
    private val logger by Logger()

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception, exchange: ServerWebExchange): ErrorResponse {
        logger.error("request uri: ${exchange.request.uri} attributes: ${exchange.attributes}")
        logger.error(
            "[Exception] LocalMessage: ${ex.localizedMessage}, " +
                "Message: ${ex.message}, " +
                "StackTrace: ${ex.stackTraceToString()}"
        )
        return ErrorResponse(data = ErrorData(ErrorCode.UNKNOWN, ex.message))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(ex: IllegalArgumentException?): ErrorResponse =
        ErrorResponse(data = ErrorData(ErrorCode.ILLEGAL_ARGUMENT, ex?.localizedMessage))

    @ExceptionHandler(IllegalStateException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalStateException(ex: IllegalStateException?): ErrorResponse =
        ErrorResponse(data = ErrorData(ErrorCode.ILLEGAL_STATE, ex?.localizedMessage))

    @ExceptionHandler(WebClientException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalStateException(ex: WebClientException?): ErrorResponse =
        ErrorResponse(data = ErrorData(ErrorCode.EXTERNAL_API_CALL, ex?.localizedMessage))
}
