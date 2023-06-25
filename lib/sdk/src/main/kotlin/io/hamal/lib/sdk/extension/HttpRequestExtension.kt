package io.hamal.lib.sdk.extension

import io.hamal.lib.domain.vo.base.DomainId
import io.hamal.lib.http.HttpRequest

fun <ID : DomainId> HttpRequest.parameter(key: String, value: ID): HttpRequest {
    parameter(key, value.value.value)
    return this
}