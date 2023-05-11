package io.hamal.lib.http

import io.hamal.lib.http.HttpRequest.HttpMethod.Get

interface HttpRequest {
    val url: String
    val method: HttpMethod
    val client: org.apache.http.client.HttpClient


    fun param(key: String, value: String): HttpRequest

    fun execute(): HttpResponseFacade

    fun executeWithoutResult()

    enum class HttpMethod {
        Delete,
        Get,
        Patch,
        Post,
        Put
    }
}

class DefaultHttpRequest(
    override val url : String,
    override val method: HttpRequest.HttpMethod,
    override val client: org.apache.http.client.HttpClient
) : HttpRequest {

    override fun param(key: String, value: String): HttpRequest {
        TODO("Not yet implemented")
    }

    override fun  execute(): HttpResponseFacade {
        val preparedUrl = url

        val requestFacade = when (method) {
            Get -> HttpGetRequestFacade(
                url = preparedUrl,
                parameters = listOf(),
                client = client
            )

            else -> TODO()
        }

        requestFacade.execute()
        return requestFacade.response()
    }

    override fun executeWithoutResult() {
        TODO("Not yet implemented")
    }

}