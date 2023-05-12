package io.hamal.lib.http

import io.hamal.lib.http.HttpStatusCode.*
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.*
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
            Ok, Created, Accepted -> SuccessHttpResponse(
                statusCode = statusCode,
                headers = httpHeaders,
                inputStream = httpResponse.entity.content?.let(HttpUtils::copyStream) ?: InputStream.nullInputStream(),
                contentDeserializer = serdeFactory.contentDeserializer
            )

            NoContent -> NoContentHttpResponse(
                statusCode = statusCode,
                headers = httpHeaders
            )

            else -> ErrorHttpResponse(
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
    url = url,
    headers = headers,
    parameters = parameters,
    serdeFactory = serdeFactory,
    client = client
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
    url = url,
    headers = headers,
    parameters = parameters,
    serdeFactory = serdeFactory,
    client = client
) {
    override fun buildRequest(): HttpRequestBase {
        return HttpGet("${url}${parameters.toQueryString()}")
    }
}


internal sealed class HttpRequestWithBodyFacade(
    url: String,
    headers: HttpHeaders,
    parameters: List<HttpParameter>,
    serdeFactory: HttpSerdeFactory,
    client: HttpClient
) : HttpBaseRequestFacade(
    url = url,
    headers = headers,
    parameters = parameters,
    serdeFactory = serdeFactory,
    client = client
)


internal class HttpPatchRequestFacade(
    url: String,
    headers: HttpHeaders,
    parameters: List<HttpParameter>,
    serdeFactory: HttpSerdeFactory,
    client: HttpClient
) : HttpRequestWithBodyFacade(
    url = url,
    headers = headers,
    parameters = parameters,
    serdeFactory = serdeFactory,
    client = client
) {
    override fun buildRequest(): HttpRequestBase {
        return HttpPatch("${url}${parameters.toQueryString()}")
    }
}


internal class HttpPostRequestFacade(
    url: String,
    headers: HttpHeaders,
    parameters: List<HttpParameter>,
    serdeFactory: HttpSerdeFactory,
    client: HttpClient
) : HttpRequestWithBodyFacade(
    url = url,
    headers = headers,
    parameters = parameters,
    serdeFactory = serdeFactory,
    client = client
) {
    override fun buildRequest(): HttpRequestBase {
        return HttpPost("${url}${parameters.toQueryString()}")
    }
}

internal class HttpPutRequestFacade(
    url: String,
    headers: HttpHeaders,
    parameters: List<HttpParameter>,
    serdeFactory: HttpSerdeFactory,
    client: HttpClient
) : HttpRequestWithBodyFacade(
    url = url,
    headers = headers,
    parameters = parameters,
    serdeFactory = serdeFactory,
    client = client
) {
    override fun buildRequest(): HttpRequestBase {
        return HttpPut("${url}${parameters.toQueryString()}")
    }
}

internal fun HttpHeaders.toBasicHeaders(): List<BasicHeader> {
    return mapping.map { BasicHeader(it.key, it.value) }
}