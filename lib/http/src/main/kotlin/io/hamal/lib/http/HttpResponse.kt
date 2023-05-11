package io.hamal.lib.http

import java.io.InputStream
import kotlin.reflect.KClass

enum class HttpStatusCode(val value: Int) {
    Ok(200)
}

sealed interface HttpResponse {
    val statusCode: HttpStatusCode
    val inputStream: InputStream
}


data class SuccessHttpResponse(
    override val statusCode: HttpStatusCode,
    override val inputStream: InputStream,
    val contentDeserializer: HttpContentDeserializer
) : HttpResponse {
    fun <RESULT : Any> result(clazz: KClass<RESULT>): RESULT {
        return contentDeserializer.deserialize(inputStream, clazz)
    }
}

data class ErrorHttpResponse(
    override val statusCode: HttpStatusCode,
    override val inputStream: InputStream,
    val errorDeserializer: HttpErrorDeserializer
) : HttpResponse {

    fun error(): Throwable {
        return errorDeserializer.deserialize(inputStream)
    }

}