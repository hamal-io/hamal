package io.hamal.application.adapter

import io.hamal.lib.ddd.usecase.*
import io.hamal.lib.meta.Maybe
import org.springframework.beans.factory.annotation.Autowired
import kotlin.reflect.KClass

class DefaultUseCaseInvokerAdapter(
    @Autowired internal val getCommandUseCasePort: GetCommandUseCasePort,
    @Autowired internal val getQueryUseCasePort: GetQueryUseCasePort,
    @Autowired internal val getFetchOneUseCasePort: GetFetchOneUseCasePort
) : InvokeUseCasePort {

    override fun <RESULT : Any, PAYLOAD : CommandUseCasePayload> command(
        resultClass: KClass<RESULT>,
        vararg payloads: PAYLOAD
    ): List<RESULT> = payloads.flatMap { payload -> getCommandUseCasePort[resultClass, payload::class](payload) }

    override fun <PAYLOAD : CommandUseCasePayload> command(
        vararg payloads: PAYLOAD
    ) = payloads.forEach { payload -> getCommandUseCasePort[Unit::class, payload::class](payload) }


    override fun <RESULT : Any, PAYLOAD : QueryUseCasePayload> query(
        resultClass: KClass<RESULT>,
        payload: PAYLOAD
    ): List<RESULT> = getQueryUseCasePort[resultClass, payload::class](payload)

    override fun <RESULT : Any, PAYLOAD : FetchOneUseCasePayload> fetchOne(
        resultClass: KClass<RESULT>,
        payload: PAYLOAD
    ): Maybe<RESULT> = getFetchOneUseCasePort[resultClass, payload::class](payload)
}