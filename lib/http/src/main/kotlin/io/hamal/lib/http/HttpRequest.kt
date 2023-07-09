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
    fun <VALUE : Any> executeList(clazz: KClass<VALUE>): List<VALUE>
    enum class HttpMethod {
        Delete,
        Get,
        Patch,
        Post,
        Put
    }
}

interface HttpRequestWithBody : HttpRequest {
    override fun header(key: String, value: String): HttpRequestWithBody
    fun <BODY_TYPE : Any> body(body: BODY_TYPE, clazz: KClass<BODY_TYPE>): HttpRequestWithBody
    fun body(str: String): HttpRequestWithBody
    fun body(contentType: String, bytes: ByteArray): HttpRequestWithBody
}

inline fun <reified BODY_TYPE : Any> HttpRequestWithBody.body(body: BODY_TYPE): HttpRequestWithBody =
    body(body, BODY_TYPE::class)

class DefaultHttpRequest(
    override val url: String,
    override val method: HttpRequest.HttpMethod,
    override val serdeFactory: HttpSerdeFactory,
    override val headers: HttpMutableHeaders,
    override val client: org.apache.http.client.HttpClient
) : HttpRequest, HttpRequestWithBody {

    private val parameters = mutableListOf<HttpParameter>()
    private val bodies = mutableListOf<HttpBody>()
    override fun <BODY_TYPE : Any> body(body: BODY_TYPE, clazz: KClass<BODY_TYPE>): DefaultHttpRequest {
        val serializer = serdeFactory.contentSerializer
        bodies.add(
            HttpStringBody(
                name = "cmd",
                content = serializer.serialize(body, clazz),
                contentType = "application/json"
            )
        )
        return this
    }

    override fun body(str: String): HttpRequestWithBody {
        bodies.add(
            HttpStringBody(
                name = "cmd",
                content = str,
                contentType = "application/json"
            )
        )
        return this
    }

    override fun body(contentType: String, bytes: ByteArray): HttpRequestWithBody {
        bodies.add(
            HttpByteArrayBody(
                name = "cmd",
                content = bytes,
                contentType = contentType
            )
        )
        return this
    }

    override fun header(key: String, value: String): DefaultHttpRequest {
        headers[key] = value
        return this
    }

    override fun parameter(key: String, value: String): DefaultHttpRequest {
        parameters.add(HttpParameter(key, value))
        return this
    }

    override fun parameter(key: String, value: Number): DefaultHttpRequest {
        parameters.add(HttpParameter(key, value))
        return this
    }

    override fun parameter(key: String, value: Boolean): DefaultHttpRequest {
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
                bodies = bodies,
                serdeFactory = serdeFactory,
                client = client
            )

            Post -> HttpPostRequestFacade(
                url = preparedUrl,
                headers = headers.toHttpHeaders(),
                parameters = parameters,
                bodies = bodies,
                serdeFactory = serdeFactory,
                client = client
            )

            Put -> HttpPutRequestFacade(
                url = preparedUrl,
                headers = headers.toHttpHeaders(),
                parameters = parameters,
                bodies = bodies,
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
            is ErrorHttpResponse -> throw IllegalStateException("Http request was not successful")
        }
    }

    override fun <VALUE : Any> executeList(clazz: KClass<VALUE>): List<VALUE> {
        return when (val response = execute()) {
            is SuccessHttpResponse -> response.resultList(clazz)
            is NoContentHttpResponse -> throw IllegalStateException("No content was returned from the server")
            is ErrorHttpResponse -> throw IllegalStateException("Http request was not successful")
        }
    }
}