package io.hamal.lib.ddd.usecase

import io.hamal.lib.meta.Maybe
import kotlin.reflect.KClass

interface InvokeCommandUseCasePort {

    fun <RESULT : Any, PAYLOAD : CommandUseCasePayload> command(
        resultClass: KClass<RESULT>,
        vararg payloads: PAYLOAD
    ): List<RESULT>

    fun <PAYLOAD : CommandUseCasePayload> command(vararg payloads: PAYLOAD)
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