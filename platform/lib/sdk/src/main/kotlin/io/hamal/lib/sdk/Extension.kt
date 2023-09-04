package io.hamal.lib.sdk

import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpResponse
import io.hamal.lib.http.NoContentHttpResponse
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.sdk.hub.HubError
import io.hamal.lib.sdk.hub.HubException
import kotlin.reflect.KClass

internal fun <TYPE : Any> HttpResponse.fold(clazz: KClass<TYPE>): TYPE {
    return when (val result = this) {
        is SuccessHttpResponse -> result.result(clazz)
        is NoContentHttpResponse -> throw IllegalStateException("Http response without content")
        is ErrorHttpResponse -> throw HubException(result.error(HubError::class))
    }
}