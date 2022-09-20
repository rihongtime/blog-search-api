package com.rihongo.external.service.webclient

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.netty.http.client.HttpClient
import java.net.URI
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import java.util.function.Function

@Service
class WebClientServiceImpl : WebClientService {

    private val httpClient: HttpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
        .responseTimeout(Duration.ofMillis(5000))
        .doOnConnected { conn ->
            conn.addHandlerLast(ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                .addHandlerLast(WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS))
        }

    private lateinit var client: WebClient

    private lateinit var uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec>

    private lateinit var bodySpec: WebClient.RequestBodySpec

    private lateinit var headersSpec: WebClient.RequestHeadersSpec<*>

    override fun url(url: String): WebClientService {
        client = WebClient.builder()
            .baseUrl(url)
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .build()
        return this
    }

    override fun method(method: HttpMethod): WebClientService {
        uriSpec = client.method(method)
        return this
    }

    override fun path(path: String): WebClientService {
        bodySpec = uriSpec.uri { uriBuilder: UriBuilder ->
            uriBuilder
                .path(path).build()
        }
        return this
    }

    override fun path(path: Function<UriBuilder, URI>): WebClientService {
        bodySpec = uriSpec.uri(path)
        return this
    }

    override fun body(requestBody: Any): WebClientService {
        headersSpec = bodySpec.body(
            BodyInserters.fromValue(requestBody)
        )
        return this
    }

    override fun header(headersConsumer: Consumer<HttpHeaders>): WebClientService {
        headersSpec.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .acceptCharset(StandardCharsets.UTF_8)

        headersSpec.headers(headersConsumer)

        return this
    }

    override fun defaultHeader(): WebClientService {
        headersSpec.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .acceptCharset(StandardCharsets.UTF_8)
        return this
    }

    override fun response(): WebClient.ResponseSpec {
        return headersSpec.retrieve()
    }
}
