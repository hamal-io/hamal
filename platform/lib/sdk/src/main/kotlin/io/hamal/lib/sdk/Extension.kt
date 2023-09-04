package io.hamal.lib.sdk

import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpResponse
import io.hamal.lib.http.NoContentHttpResponse
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.sdk.hub.ApiError
import io.hamal.lib.sdk.hub.ApiException
import kotlin.reflect.KClass

internal fun <TYPE : Any> HttpResponse.fold(clazz: KClass<TYPE>): TYPE {
    return when (val result = this) {
        is SuccessHttpResponse -> result.result(clazz)
        is NoContentHttpResponse -> throw IllegalStateException("Http response without content")
        is ErrorHttpResponse -> throw ApiException(result.error(ApiError::class))
    }
}