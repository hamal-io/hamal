package io.hamal.lib.http

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.http.HttpRequest.HttpMethod.*
import kotlin.reflect.KClass

interface HttpRequest {
    val url: String
    val method: HttpMethod
    val serdeFactory: HttpSerdeFactory
    val headers: HttpMutableHeaders
    val client: org.apache.http.client.HttpClient

    fun path(key: String, value: String): HttpRequest
    fun path(key: String, value: SnowflakeId) = path(key, value.value.toString(16))
    fun path(key: String, value: ValueObjectId) = path(key, value.value)

    fun header(key: String, value: String): HttpRequest
    fun parameter(key: String, value: String): HttpRequest
    fun parameter(key: String, value: Number): HttpRequest
    fun parameter(key: String, value: SnowflakeId): HttpRequest
    fun parameter(key: String, value: ValueObjectId): HttpRequest
    fun parameter(key: String, value: Limit): HttpRequest
    fun parameter(key: String, value: List<ValueObjectId>): HttpRequest
    fun parameter(key: String, value: Boolean): HttpRequest
    fun execute(): HttpResponse
    fun <RESULT : Any> execute(clazz: KClass<RESULT>): RESULT
    fun <RESULT : Any> execute(clazz: KClass<RESULT>, action: RESULT.() -> Unit)
    fun <RESULT : Any> executeList(clazz: KClass<RESULT>): List<RESULT>
    enum class HttpMethod {
        Delete,
        Get,
        Patch,
        Post,
        Put
    }
}

interface HttpRequestWithBody : HttpRequest {
    override fun path(key: String, value: String): HttpRequestWithBody
    override fun path(key: String, value: SnowflakeId) = path(key, value.value.toString(16))
    override fun path(key: String, value: ValueObjectId) = path(key, value.value)

    override fun header(key: String, value: String): HttpRequestWithBody
    fun <BODY_TYPE : Any> body(body: BODY_TYPE, clazz: KClass<BODY_TYPE>): HttpRequestWithBody
    fun body(str: String): HttpRequestWithBody
    fun body(contentType: String, bytes: ByteArray): HttpRequestWithBody
}

inline fun <reified BODY_TYPE : Any> HttpRequestWithBody.body(body: BODY_TYPE): HttpRequestWithBody =
    body(body, BODY_TYPE::class)

class HttpRequestImpl(
    override var url: String,
    override val method: HttpRequest.HttpMethod,
    override val serdeFactory: HttpSerdeFactory,
    override val headers: HttpMutableHeaders,
    override val client: org.apache.http.client.HttpClient
) : HttpRequest, HttpRequestWithBody {

    private val parameters = mutableListOf<HttpParameter>()
    private val bodies = mutableListOf<HttpBody>()
    override fun <BODY_TYPE : Any> body(body: BODY_TYPE, clazz: KClass<BODY_TYPE>): HttpRequestImpl {
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

    override fun path(key: String, value: String): HttpRequestImpl {
        url = url.replace("{${key}}", value)
        return this
    }

    override fun header(key: String, value: String): HttpRequestImpl {
        headers[key] = value
        return this
    }

    override fun parameter(key: String, value: String): HttpRequestImpl {
        parameters.add(HttpParameter(key, value))
        return this
    }

    override fun parameter(key: String, value: Number): HttpRequestImpl {
        parameters.add(HttpParameter(key, value))
        return this
    }

    override fun parameter(key: String, value: SnowflakeId): HttpRequest {
        parameters.add(HttpParameter(key, value.value.toString(16)))
        return this
    }

    override fun parameter(key: String, value: ValueObjectId): HttpRequest {
        parameters.add(HttpParameter(key, value.value.value.toString(16)))
        return this
    }

    override fun parameter(key: String, value: Limit): HttpRequest {
        parameters.add(HttpParameter(key, value.value))
        return this
    }

    override fun parameter(key: String, value: List<ValueObjectId>): HttpRequest {
        parameters.add(HttpParameter(key, value.joinToString(",") { it.value.value.toString(16) }))
        return this
    }

    override fun parameter(key: String, value: Boolean): HttpRequestImpl {
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

        try {
            requestFacade.execute()
            return requestFacade.response()
        } catch (e: Exception) {
            throw HttpException(e)
        }
    }

    override fun <RESULT : Any> execute(clazz: KClass<RESULT>): RESULT {
        return when (val response = execute()) {
            is HttpSuccessResponse -> response.result(clazz)
            is HttpNoContentResponse -> throw IllegalStateException("No content was returned from the server")
            is HttpErrorResponse -> throw IllegalStateException("Http request was not successful")
        }
    }

    override fun <RESULT : Any> execute(clazz: KClass<RESULT>, action: (RESULT) -> Unit) {
        action(execute(clazz))
    }

    override fun <VALUE : Any> executeList(clazz: KClass<VALUE>): List<VALUE> {
        return when (val response = execute()) {
            is HttpSuccessResponse -> response.resultList(clazz)
            is HttpNoContentResponse -> throw IllegalStateException("No content was returned from the server")
            is HttpErrorResponse -> throw IllegalStateException("Http request was not successful")
        }
    }
}