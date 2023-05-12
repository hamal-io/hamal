package io.hamal.lib.http

import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClientBuilder

interface HttpClientFactory {
    fun get(): HttpClient
}

object DefaultHttpClientFactory : HttpClientFactory {
    override fun get(): HttpClient {
        return HttpClientBuilder.create().build()
    }

}