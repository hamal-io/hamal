package io.hamal.lib.ddd.usecase

import io.hamal.lib.meta.Maybe
import kotlin.reflect.KClass

interface InvokeCommandUseCasePort {
    fun <RESULT : Any, PAYLOAD : CommandUseCasePayload> maybe(
        resultClass: KClass<RESULT>,
        payload: PAYLOAD
    ): Maybe<RESULT>

    fun <RESULT : Any, PAYLOAD : CommandUseCasePayload?> list(
        resultClass: KClass<RESULT>,
        payload: PAYLOAD
    ): List<RESULT>

    fun <RESULT : Any, PAYLOAD : CommandUseCasePayload?> commands(
        resultClass: KClass<RESULT>,
        payloads: Collection<PAYLOAD>
    ): List<RESULT>

    fun <PAYLOAD : CommandUseCasePayload> noResultCommand(payload: PAYLOAD) {
        maybe(Unit::class, payload)
    }
}

interface InvokeQueryUseCasePort {
    fun <RESULT : Any, PAYLOAD : QueryUseCasePayload> query(
        resultClass: KClass<RESULT>,
        payload: PAYLOAD
    ): List<RESULT>
}

interface InvokeFetchOneUseCasePort {
    fun <RESULT : Any, PAYLOAD : FetchOneUseCasePayload> fetchOne(
        resultClass: KClass<RESULT>,
        payload: PAYLOAD
    ): Maybe<RESULT>
}

interface InvokeUseCasePort : InvokeCommandUseCasePort, InvokeQueryUseCasePort, InvokeFetchOneUseCasePort