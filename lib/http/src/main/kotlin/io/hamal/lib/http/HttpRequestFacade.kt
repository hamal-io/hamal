package io.hamal.lib.http

import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpRequestBase
import java.io.InputStream

internal interface HttpRequestFacade {
    fun execute()

    fun response(): HttpResponse
}


internal sealed class HttpBaseRequestFacade(
    protected val url: String,
    protected val parameters: List<HttpParam<*>>,
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
        response = if (statusCode >= HttpStatusCode.BadRequest) {
            ErrorHttpResponse(
                statusCode = statusCode,
                inputStream = result.entity.content ?: InputStream.nullInputStream(),
                errorDeserializer = serdeFactory.errorDeserializer
            )
        } else {
            SuccessHttpResponse(
                statusCode = statusCode,
                inputStream = result.entity.content ?: InputStream.nullInputStream(),
                contentDeserializer = serdeFactory.contentDeserializer
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

internal class HttpGetRequestFacade(
    url: String,
    parameters: List<HttpParam<*>>,
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


internal fun List<HttpParam<*>>.toQueryString(): String {
    if (isEmpty()) {
        return ""
    }
    val builder = StringBuilder()
    builder.append("?")
    forEach { param ->
        builder.append(param.name)
        builder.append("=")
        builder.append(param.toQueryString())
        builder.append(",")
        builder.deleteCharAt(builder.length - 1)
        builder.append("&")
    }
    builder.deleteCharAt(builder.length - 1)
    return builder.toString()
}
