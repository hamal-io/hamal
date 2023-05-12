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
//        for (header in headers) {
//            request.addHeader(header)
//        }
//
//        if (requestConfig != null) {
//            request.config = requestConfig
//        }

        val result: org.apache.http.HttpResponse = client.execute(request)

//        val allHeaders = result.allHeaders
//        val headerMap: MutableMap<String, String> = HashMap()
//        if (allHeaders != null) {
//            for (header in allHeaders) {
//                headerMap[header.name] = header.value
//            }
//        }

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

//        val httpStatusCode = result.statusLine.statusCode
//        if (httpStatusCode >= 400) {
//        TODO()
//            try {
//                val error: Unit = errorConverter.convert(httpStatusCode, inputStream)
//                response = HttpResponse.withError(httpStatusCode, error, headerMap)
//            } catch (t: Throwable) {
//                throw HttpRuntimeError(StringError(t))
//            }
//        } else {
//            if (interceptor is HttpResponse.ResponseInterceptor.NoInterceptor<ERROR?>) {
//                try {
//                    response = HttpResponse.of(httpStatusCode, converter.convert(inputStream), headerMap)
//                } catch (t: Throwable) {
//                    throw InternalServerError(t)
//                }
//            } else {
//            val copiedStream = HttpUtil.copyStream(inputStream)
//                val maybeInterceptedError: Unit = interceptor.intercept(httpStatusCode, copiedStream, errorConverter)
//                if (maybeInterceptedError.isPresent()) {
//                    val error: Unit = maybeInterceptedError.get()
//                    response = HttpResponse.withError(httpStatusCode, error, headerMap)
//                } else {
//                    try {
//                        copiedStream.reset()
//                        response = HttpResponse.of(httpStatusCode, converter.convert(copiedStream), headerMap)
//
//                    } catch (t: Throwable) {
//                        throw InternalServerError(t)
//                    }
//                }
//            }
//            TODO()
//        }
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