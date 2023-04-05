package io.hamal.lib.ddd.usecase

import io.hamal.lib.meta.Maybe

interface CommandUseCaseInvokerPort {
    fun <RESULT : Any, PAYLOAD : CommandUseCasePayload> maybe(
        resultClass: Class<RESULT>,
        payload: PAYLOAD
    ): Maybe<RESULT>

    fun <RESULT : Any, PAYLOAD : CommandUseCasePayload?> list(
        resultClass: Class<RESULT>,
        payload: PAYLOAD
    ): List<RESULT>

    fun <RESULT : Any, PAYLOAD : CommandUseCasePayload?> commands(
        resultClass: Class<RESULT>,
        payloads: Collection<PAYLOAD>
    ): List<RESULT>

    fun <PAYLOAD : CommandUseCasePayload> noResultCommand(payload: PAYLOAD) {
        maybe(Unit::class.java, payload)
    }
}

interface QueryUseCaseInvokerPort {
    fun <RESULT : Any, PAYLOAD : QueryUseCasePayload> query(
        resultClass: Class<RESULT>,
        payload: PAYLOAD
    ): List<RESULT>
}

interface FetchOneUseCaseInvokerPort {
    fun <RESULT : Any, PAYLOAD : FetchOneUseCasePayload> fetchOne(
        resultClass: Class<RESULT>,
        payload: PAYLOAD
    ): Maybe<RESULT>
}