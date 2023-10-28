package io.hamal.lib.sdk

import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpNoContentResponse
import io.hamal.lib.http.HttpResponse
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiException
import kotlin.reflect.KClass

internal fun <TYPE : Any> HttpResponse.fold(clazz: KClass<TYPE>): TYPE {
    return when (val result = this) {
        is HttpSuccessResponse -> result.result(clazz)
        is HttpNoContentResponse -> throw IllegalStateException("Http response without content")
        is HttpErrorResponse -> throw ApiException(result.error(ApiError::class))
    }
}