package io.hamal.lib.http

import io.hamal.lib.http.HttpRequest.HttpMethod.Get
import org.apache.http.impl.client.HttpClientBuilder

interface HttpClient {
    fun delete(url: String): HttpRequest
    fun get(url: String): HttpRequest

    fun patch(url: String): HttpRequest

    fun post(url: String): HttpRequest

    fun put(url: String): HttpRequest
}

class DefaultHttpClient(
    private var baseUrl: String = "",
    private var serdeFactory: HttpSerdeFactory = DefaultHttpSerdeFactory
) : HttpClient {

    override fun delete(url: String): HttpRequest {
        TODO("Not yet implemented")
    }

    override fun get(url: String): HttpRequest {
        return DefaultHttpRequest(
            method = Get,
            url = baseUrl + url,
            serdeFactory = serdeFactory,
            client = HttpClientBuilder.create().build() // FIXME replace with factory
        )
    }

    override fun patch(url: String): HttpRequest {
        TODO("Not yet implemented")
    }

    override fun post(url: String): HttpRequest {
        TODO("Not yet implemented")
    }

    override fun put(url: String): HttpRequest {
        TODO("Not yet implemented")
    }

}