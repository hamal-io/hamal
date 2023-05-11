package io.hamal.lib.http

import java.io.InputStream

interface HttpResponseFacade {
    val statusCode: Int
    val content: InputStream
}

data class HttpResponseSuccessFacade(
    override val statusCode: Int,
    override val content: InputStream
) : HttpResponseFacade {
    fun <RESULT : Any> result(): RESULT {
        TODO()
    }
}