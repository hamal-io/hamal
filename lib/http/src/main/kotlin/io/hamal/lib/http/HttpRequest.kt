package io.hamal.lib.http

import io.hamal.lib.http.HttpRequest.HttpMethod.Get
import kotlin.reflect.KClass

interface HttpRequest {
    val url: String
    val method: HttpMethod
    val errorDeserializer: HttpErrorDeserializer
    val contentDeserializer: HttpContentDeserializer
    val client: org.apache.http.client.HttpClient

    fun param(key: String, value: String): HttpRequest

    fun execute(): HttpResponse

    fun <VALUE : Any> execute(clazz: KClass<VALUE>): VALUE

    fun executeWithoutResult()

    enum class HttpMethod {
        Delete,
        Get,
        Patch,
        Post,
        Put
    }
}

class DefaultHttpRequest(
    override val url: String,
    override val method: HttpRequest.HttpMethod,
    override val errorDeserializer: HttpErrorDeserializer,
    override val contentDeserializer: HttpContentDeserializer,
    override val client: org.apache.http.client.HttpClient
) : HttpRequest {

    override fun param(key: String, value: String): HttpRequest {
        TODO("Not yet implemented")
    }

    override fun execute(): HttpResponse {
        val preparedUrl = url

        val requestFacade = when (method) {
            Get -> HttpGetRequestFacade(
                url = preparedUrl,
                parameters = listOf(),
                errorDeserializer = errorDeserializer,
                contentDeserializer = contentDeserializer,
                client = client
            )

            else -> TODO()
        }

        requestFacade.execute()
        return requestFacade.response()
    }

    override fun <RESULT : Any> execute(clazz: KClass<RESULT>): RESULT {
        return when (val response = execute()) {
            is SuccessHttpResponse -> response.result(clazz)
            is ErrorHttpResponse -> throw response.error()
        }
    }

    override fun executeWithoutResult() {
        TODO("Not yet implemented")
    }

}