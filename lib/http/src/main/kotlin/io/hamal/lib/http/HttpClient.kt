package io.hamal.lib.http

import io.hamal.lib.http.HttpRequest.HttpMethod.Get
import org.apache.http.impl.client.HttpClientBuilder

interface HttpClient {
    fun get(url: String): HttpRequest
}

class DefaultHttpClient(
    private var errorDeserializer: HttpErrorDeserializer = DefaultErrorDeserializer,
    private var contentDeserializer: HttpContentDeserializer = KotlinJsonHttpContentDeserializer
) : HttpClient {
    override fun get(url: String): HttpRequest {
        return DefaultHttpRequest(
            method = Get,
            url = url,
            errorDeserializer = errorDeserializer,
            contentDeserializer = contentDeserializer,
            client = HttpClientBuilder.create().build() // FIXME replace with factory
        )
    }

}