package io.hamal.lib.sdk

import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpResponse
import io.hamal.lib.http.NoContentHttpResponse
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiException
import io.hamal.lib.sdk.api.ApiSubmittedReqImpl
import kotlin.reflect.KClass

internal fun <TYPE : Any> HttpResponse.fold(clazz: KClass<TYPE>): TYPE {
    return when (val result = this) {
        is SuccessHttpResponse -> result.result(clazz)
        is NoContentHttpResponse -> throw IllegalStateException("Http response without content")
        is ErrorHttpResponse -> throw ApiException(result.error(ApiError::class))
    }
}

fun<TYPE : DomainId> HttpResponse.foldReq(): ApiSubmittedReqImpl<TYPE> {
    @Suppress("UNCHECKED_CAST")
    return when (val result = this) {
        is SuccessHttpResponse -> result.result(ApiSubmittedReqImpl::class) as ApiSubmittedReqImpl<TYPE>
        is NoContentHttpResponse -> throw IllegalStateException("Http response without content")
        is ErrorHttpResponse -> throw ApiException(result.error(ApiError::class))
    }
}