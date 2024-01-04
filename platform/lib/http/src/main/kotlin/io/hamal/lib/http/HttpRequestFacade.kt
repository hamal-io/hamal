package io.hamal.lib.http

import io.hamal.lib.http.HttpStatusCode.*
import org.apache.http.HttpEntity
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.*
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.entity.ContentType
import org.apache.http.message.BasicHeader
import java.io.InputStream

internal interface HttpRequestFacade {
    fun execute()

    fun response(): HttpResponse
}


internal sealed class HttpBaseRequestFacade(
    protected val url: String,
    protected val headers: HttpHeaders,
    protected val parameters: List<HttpParameter>,
    protected val serdeFactory: HttpSerdeFactory,
    protected val client: HttpClient
) : HttpRequestFacade {

    private lateinit var response: HttpResponse

    protected abstract fun buildRequest(): HttpRequestBase

    override fun execute() {
        val request: HttpRequestBase = buildRequest()
        headers.toBasicHeaders().forEach(request::addHeader)

        val httpResponse: org.apache.http.HttpResponse = client.execute(request)
        val httpHeaders = HttpHeaders(httpResponse.allHeaders.associateBy({ it.name }, { it.value }))

        val statusCode = HttpStatusCode.of(httpResponse.statusLine.statusCode)
        response = when (statusCode) {
            Ok, Created, Accepted -> HttpSuccessResponse(
                statusCode = statusCode,
                headers = httpHeaders,
                inputStream = httpResponse.entity.content?.let(HttpUtils::copyStream) ?: InputStream.nullInputStream(),
                contentDeserializer = serdeFactory.contentDeserializer
            )

            NoContent -> HttpNoContentResponse(
                statusCode = statusCode, headers = httpHeaders
            )

            else -> HttpErrorResponse(
                statusCode = statusCode,
                headers = httpHeaders,
                inputStream = httpResponse.entity.content?.let(HttpUtils::copyStream) ?: InputStream.nullInputStream(),
                errorDeserializer = serdeFactory.errorDeserializer
            )
        }

        request.releaseConnection()
    }

    override fun response(): HttpResponse = response
}

internal class HttpDeleteRequestFacade(
    url: String,
    headers: HttpHeaders,
    parameters: List<HttpParameter>,
    serdeFactory: HttpSerdeFactory,
    client: HttpClient
) : HttpBaseRequestFacade(
    url = url, headers = headers, parameters = parameters, serdeFactory = serdeFactory, client = client
) {
    override fun buildRequest(): HttpRequestBase {
        return HttpDelete("${url}${parameters.toQueryString()}")
    }
}

internal class HttpGetRequestFacade(
    url: String,
    headers: HttpHeaders,
    parameters: List<HttpParameter>,
    serdeFactory: HttpSerdeFactory,
    client: HttpClient
) : HttpBaseRequestFacade(
    url = url, headers = headers, parameters = parameters, serdeFactory = serdeFactory, client = client
) {
    override fun buildRequest(): HttpRequestBase {
        return HttpGet("${url}${parameters.toQueryString()}")
    }
}


internal sealed class HttpRequestWithBodyFacade(
    url: String,
    headers: HttpHeaders,
    parameters: List<HttpParameter>,
    val bodies: List<HttpBody>,
    serdeFactory: HttpSerdeFactory,
    client: HttpClient
) : HttpBaseRequestFacade(
    url = url, headers = headers, parameters = parameters, serdeFactory = serdeFactory, client = client
) {
    protected fun createEntity(): HttpEntity? {
        return when (bodies.size) {
            0 -> null
            1 -> when (val body = bodies.first()) {
                is HttpStringBody -> ByteArrayEntity(body.content.toByteArray(), ContentType.create(body.contentType))
                is HttpByteArrayBody -> ByteArrayEntity(body.content, ContentType.create(body.contentType))
                else -> TODO()
            }

            else -> TODO()
        }
    }
}


internal class HttpPatchRequestFacade(
    url: String,
    headers: HttpHeaders,
    parameters: List<HttpParameter>,
    bodies: List<HttpBody>,
    serdeFactory: HttpSerdeFactory,
    client: HttpClient
) : HttpRequestWithBodyFacade(
    url = url, headers = headers, parameters = parameters, bodies = bodies, serdeFactory = serdeFactory, client = client
) {
    override fun buildRequest(): HttpRequestBase {
        return HttpPatch("${url}${parameters.toQueryString()}").apply { entity = createEntity() }
    }
}


internal class HttpPostRequestFacade(
    url: String,
    headers: HttpHeaders,
    parameters: List<HttpParameter>,
    bodies: List<HttpBody>,
    serdeFactory: HttpSerdeFactory,
    client: HttpClient
) : HttpRequestWithBodyFacade(
    url = url, headers = headers, parameters = parameters, bodies = bodies, serdeFactory = serdeFactory, client = client
) {
    override fun buildRequest(): HttpRequestBase {
        return HttpPost("${url}${parameters.toQueryString()}").apply { entity = createEntity() }
    }
}

internal class HttpPutRequestFacade(
    url: String,
    headers: HttpHeaders,
    parameters: List<HttpParameter>,
    bodies: List<HttpBody>,
    serdeFactory: HttpSerdeFactory,
    client: HttpClient
) : HttpRequestWithBodyFacade(
    url = url, headers = headers, parameters = parameters, bodies = bodies, serdeFactory = serdeFactory, client = client
) {
    override fun buildRequest(): HttpRequestBase {
        return HttpPut("${url}${parameters.toQueryString()}").apply { entity = createEntity() }
    }
}

internal fun HttpHeaders.toBasicHeaders(): List<BasicHeader> {
    return map { BasicHeader(it.key, it.value) }
}