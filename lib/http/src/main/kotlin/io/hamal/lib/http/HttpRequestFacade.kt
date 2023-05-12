package io.hamal.lib.http

import io.hamal.lib.http.HttpStatusCode.*
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.*
import java.io.InputStream

internal interface HttpRequestFacade {
    fun execute()

    fun response(): HttpResponse
}


internal sealed class HttpBaseRequestFacade(
    protected val url: String,
    protected val parameters: List<HttpParameter>,
    protected val serdeFactory: HttpSerdeFactory,
    protected val client: HttpClient
) : HttpRequestFacade {

    private lateinit var response: HttpResponse

    protected abstract fun buildRequest(): HttpRequestBase

    override fun execute() {
        val request: HttpRequestBase = buildRequest()

        val result: org.apache.http.HttpResponse = client.execute(request)

        val statusCode = HttpStatusCode.of(result.statusLine.statusCode)
        response = when (statusCode) {
            Ok, Created, Accepted -> SuccessHttpResponse(
                statusCode = statusCode,
                inputStream = result.entity.content?.let(HttpUtils::copyStream) ?: InputStream.nullInputStream(),
                contentDeserializer = serdeFactory.contentDeserializer
            )

            NoContent -> NoContentHttpResponse(statusCode)
            else -> ErrorHttpResponse(
                statusCode = statusCode,
                inputStream = result.entity.content?.let(HttpUtils::copyStream) ?: InputStream.nullInputStream(),
                errorDeserializer = serdeFactory.errorDeserializer
            )
        }

        request.releaseConnection()
    }

    override fun response(): HttpResponse = response
}

internal class HttpDeleteRequestFacade(
    url: String,
    parameters: List<HttpParameter>,
    serdeFactory: HttpSerdeFactory,
    client: HttpClient
) : HttpBaseRequestFacade(
    url = url,
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
    parameters: List<HttpParameter>,
    serdeFactory: HttpSerdeFactory,
    client: HttpClient
) : HttpBaseRequestFacade(
    url = url,
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
    parameters: List<HttpParameter>,
    serdeFactory: HttpSerdeFactory,
    client: HttpClient
) : HttpBaseRequestFacade(
    url = url,
    parameters = parameters,
    serdeFactory = serdeFactory,
    client = client
)


internal class HttpPatchRequestFacade(
    url: String,
    parameters: List<HttpParameter>,
    serdeFactory: HttpSerdeFactory,
    client: HttpClient
) : HttpRequestWithBodyFacade(
    url = url,
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
    parameters: List<HttpParameter>,
    serdeFactory: HttpSerdeFactory,
    client: HttpClient
) : HttpRequestWithBodyFacade(
    url = url,
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
    parameters: List<HttpParameter>,
    serdeFactory: HttpSerdeFactory,
    client: HttpClient
) : HttpRequestWithBodyFacade(
    url = url,
    parameters = parameters,
    serdeFactory = serdeFactory,
    client = client
) {
    override fun buildRequest(): HttpRequestBase {
        return HttpPut("${url}${parameters.toQueryString()}")
    }
}