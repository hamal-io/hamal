package io.hamal.lib.http

import io.hamal.lib.http.HttpRequest.HttpMethod
import io.hamal.lib.http.HttpRequest.HttpMethod.*
import org.apache.http.impl.client.HttpClientBuilder

interface HttpClient {
    fun delete(url: String): HttpRequest
    fun get(url: String): HttpRequest
    fun patch(url: String): HttpRequestWithBody
    fun post(url: String): HttpRequestWithBody
    fun put(url: String): HttpRequestWithBody
}

class DefaultHttpClient(
    private var baseUrl: String = "",
    private var serdeFactory: HttpSerdeFactory = DefaultHttpSerdeFactory
) : HttpClient {

    override fun delete(url: String): HttpRequest {
        return requestWith(Delete, url)
    }

    override fun get(url: String): HttpRequest = requestWith(Get, url)

    override fun patch(url: String): HttpRequestWithBody = requestWith(Patch, url)

    override fun post(url: String): HttpRequestWithBody = requestWith(Post, url)
    override fun put(url: String): HttpRequestWithBody = requestWith(Put, url)

    private fun requestWith(method: HttpMethod, url: String): DefaultHttpRequest {
        return DefaultHttpRequest(
            method = method,
            url = baseUrl + url,
            serdeFactory = serdeFactory,
            client = HttpClientBuilder.create().build() // FIXME replace with factory
        )
    }

}