package io.hamal.lib.http

import io.hamal.lib.http.HttpRequest.HttpMethod.*
import kotlin.reflect.KClass

interface HttpRequest {
    val url: String
    val method: HttpMethod
    val serdeFactory: HttpSerdeFactory
    val headers: HttpMutableHeaders
    val client: org.apache.http.client.HttpClient
    fun header(key: String, value: String): HttpRequest
    fun parameter(key: String, value: String): HttpRequest
    fun parameter(key: String, value: Number): HttpRequest
    fun parameter(key: String, value: Boolean): HttpRequest
    fun execute(): HttpResponse
    fun <VALUE : Any> execute(clazz: KClass<VALUE>): VALUE
    enum class HttpMethod {
        Delete,
        Get,
        Patch,
        Post,
        Put
    }
}

interface HttpRequestWithBody : HttpRequest {

}

class DefaultHttpRequest(
    override val url: String,
    override val method: HttpRequest.HttpMethod,
    override val serdeFactory: HttpSerdeFactory,
    override val headers: HttpMutableHeaders,
    override val client: org.apache.http.client.HttpClient
) : HttpRequest, HttpRequestWithBody {

    private val parameters = mutableListOf<HttpParameter>()
    override fun header(key: String, value: String): HttpRequest {
        headers[key] = value
        return this
    }

    override fun parameter(key: String, value: String): HttpRequest {
        parameters.add(HttpParameter(key, value))
        return this
    }

    override fun parameter(key: String, value: Number): HttpRequest {
        parameters.add(HttpParameter(key, value))
        return this
    }

    override fun parameter(key: String, value: Boolean): HttpRequest {
        parameters.add(HttpParameter(key, value))
        return this
    }

    override fun execute(): HttpResponse {
        val preparedUrl = url

        val requestFacade = when (method) {
            Delete -> HttpDeleteRequestFacade(
                url = preparedUrl,
                headers = headers.toHttpHeaders(),
                parameters = parameters,
                serdeFactory = serdeFactory,
                client = client
            )

            Get -> HttpGetRequestFacade(
                url = preparedUrl,
                headers = headers.toHttpHeaders(),
                parameters = parameters,
                serdeFactory = serdeFactory,
                client = client
            )

            Patch -> HttpPatchRequestFacade(
                url = preparedUrl,
                headers = headers.toHttpHeaders(),
                parameters = parameters,
                serdeFactory = serdeFactory,
                client = client
            )

            Post -> HttpPostRequestFacade(
                url = preparedUrl,
                headers = headers.toHttpHeaders(),
                parameters = parameters,
                serdeFactory = serdeFactory,
                client = client
            )

            Put -> HttpPutRequestFacade(
                url = preparedUrl,
                headers = headers.toHttpHeaders(),
                parameters = parameters,
                serdeFactory = serdeFactory,
                client = client
            )
        }

        requestFacade.execute()
        return requestFacade.response()
    }

    override fun <RESULT : Any> execute(clazz: KClass<RESULT>): RESULT {
        return when (val response = execute()) {
            is SuccessHttpResponse -> response.result(clazz)
            is NoContentHttpResponse -> throw IllegalStateException("No content was returned from the server")
            is ErrorHttpResponse -> throw response.error()
        }
    }
}