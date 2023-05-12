package io.hamal.lib.http

import io.hamal.lib.http.HttpRequest.HttpMethod
import io.hamal.lib.http.HttpRequest.HttpMethod.*
import org.apache.http.impl.client.HttpClientBuilder

interface HttpOperations {
    fun delete(url: String): HttpRequest
    fun get(url: String): HttpRequest
    fun patch(url: String): HttpRequestWithBody
    fun post(url: String): HttpRequestWithBody
    fun put(url: String): HttpRequestWithBody
}

class HttpTemplate(
    private var baseUrl: String = "",
    private val headerFactory: HttpMutableHeaders.() -> Unit = {},
    private var serdeFactory: HttpSerdeFactory.() -> Unit = {},
    private var httpClientFactory: HttpClientBuilder.() -> Unit = {}
) : HttpOperations {
    override fun delete(url: String): HttpRequest = requestWith(Delete, url)
    override fun get(url: String): HttpRequest = requestWith(Get, url)
    override fun patch(url: String): HttpRequestWithBody = requestWith(Patch, url)
    override fun post(url: String): HttpRequestWithBody = requestWith(Post, url)
    override fun put(url: String): HttpRequestWithBody = requestWith(Put, url)
    private fun requestWith(method: HttpMethod, url: String): DefaultHttpRequest {
        return DefaultHttpRequest(
            method = method,
            url = baseUrl + url,
            headers = HttpMutableHeaders().apply(headerFactory),
            serdeFactory = DefaultHttpSerdeFactory.apply(serdeFactory),
            client = HttpClientBuilder.create().apply(httpClientFactory).build()
        )
    }
}