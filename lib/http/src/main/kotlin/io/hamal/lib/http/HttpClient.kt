package io.hamal.lib.http

import io.hamal.lib.http.HttpRequest.HttpMethod.Get
import org.apache.http.impl.client.HttpClientBuilder

interface HttpClient {
    fun get(url: String) : HttpRequest
}

class DefaultHttpClient : HttpClient{
    override fun get(url: String): HttpRequest {
        return DefaultHttpRequest(
            method = Get,
            url = url,
            client = HttpClientBuilder.create()

                .build() // FIXME replace with factory
        )
    }

}