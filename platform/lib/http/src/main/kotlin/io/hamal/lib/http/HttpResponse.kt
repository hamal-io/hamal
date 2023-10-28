package io.hamal.lib.http

import java.io.InputStream
import kotlin.reflect.KClass

enum class HttpStatusCode(val value: Int) {
    Ok(200),
    Created(201),
    Accepted(202),
    NoContent(204),
    BadRequest(400),
    Unauthorized(401),
    PaymentRequired(402),
    Forbidden(403),
    NotFound(404),
    MethodNotAllowed(405),
    NotAcceptable(406),
    Conflict(409),
    PayloadToLarge(413),
    UnsupportedMediaType(415),
    TooManyRequests(429),
    InternalServerError(500),
    NotImplemented(501),
    BadGateway(502),
    Unavailable(503),
    Timeout(504);

    companion object {
        @JvmStatic
        fun of(value: Int): HttpStatusCode {
            val result = HttpStatusCode.values().find { it.value == value }
            require(result != null) { "$value not mapped as a status code" }
            return result
        }
    }
}

sealed interface HttpResponse {
    val statusCode: HttpStatusCode
    val headers: HttpHeaders
    val contentLength: Int

    val isEmpty get() = contentLength == 0
    val isNotEmpty get() = !isEmpty
}


data class HttpSuccessResponse(
    override val statusCode: HttpStatusCode,
    override val headers: HttpHeaders,
    val inputStream: InputStream,
    val contentDeserializer: HttpContentDeserializer
) : HttpResponse {

    fun <RESULT : Any> result(clazz: KClass<RESULT>): RESULT {
        return contentDeserializer.deserialize(inputStream, clazz)
    }

    fun <RESULT : Any> resultList(clazz: KClass<RESULT>): List<RESULT> {
        return contentDeserializer.deserializeList(inputStream, clazz)
    }

    override val contentLength = inputStream.available()
}

data class HttpNoContentResponse(
    override val statusCode: HttpStatusCode,
    override val headers: HttpHeaders
) : HttpResponse {
    override val contentLength = 0
}


data class HttpErrorResponse(
    override val statusCode: HttpStatusCode,
    override val headers: HttpHeaders,
    val inputStream: InputStream,
    val errorDeserializer: HttpErrorDeserializer
) : HttpResponse {

    fun <ERROR : Any> error(clazz: KClass<ERROR>): ERROR {
        return errorDeserializer.deserialize(inputStream, clazz)
    }

    override val contentLength = inputStream.available()
}