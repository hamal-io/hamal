package io.hamal.lib.sdk

import io.hamal.lib.domain.vo.SerializableDomainId
import io.hamal.lib.http.*
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiException
import io.hamal.lib.sdk.api.ApiSubmittedReqImpl
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

internal fun <TYPE : Any> HttpResponse.fold(clazz: KClass<TYPE>): TYPE {
    return when (val result = this) {
        is HttpSuccessResponse -> result.result(clazz)
        is HttpNoContentResponse -> throw IllegalStateException("Http response without content")
        is HttpErrorResponse -> throw ApiException(result.error(ApiError::class))
    }
}

inline fun <reified DOMAIN_ID : SerializableDomainId> HttpResponse.foldReq(): ApiSubmittedReqImpl<DOMAIN_ID> {
    return when (val result = this) {
        is HttpSuccessResponse -> result.toReq(DOMAIN_ID::class)
        is HttpNoContentResponse -> throw IllegalStateException("Http response without content")
        is HttpErrorResponse -> throw ApiException(result.error(ApiError::class))
    }
}

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
fun <DOMAIN_ID : SerializableDomainId> HttpSuccessResponse.toReq(idClass: KClass<DOMAIN_ID>): ApiSubmittedReqImpl<DOMAIN_ID> {
    return jsonDelegate.decodeFromStream(ApiSubmittedReqImpl.serializer(idClass.serializer()), inputStream)
}

inline fun <reified DOMAIN_ID : SerializableDomainId> HttpSuccessResponse.toReq(): ApiSubmittedReqImpl<DOMAIN_ID> =
    toReq(DOMAIN_ID::class)
