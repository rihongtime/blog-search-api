package com.rihongo.external.service.webclient

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import java.net.URI
import java.util.function.Consumer

@Service
interface WebClientService {

    fun url(url: String): WebClientService

    fun method(method: HttpMethod): WebClientService

    fun path(path: String): WebClientService

    fun path(path: java.util.function.Function<UriBuilder, URI>): WebClientService

    fun body(requestBody: Any): WebClientService

    fun defaultHeader(): WebClientService

    fun header(headersConsumer: Consumer<HttpHeaders>): WebClientService

    fun response(): WebClient.ResponseSpec
}
