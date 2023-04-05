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

    override fun <RESULT : Any, PAYLOAD : CommandUseCasePayload> maybe(
        resultClass: KClass<RESULT>,
        payload: PAYLOAD
    ): Maybe<RESULT> {
        TODO("Not yet implemented")
    }

    override fun <RESULT : Any, PAYLOAD : CommandUseCasePayload?> list(
        resultClass: KClass<RESULT>,
        payload: PAYLOAD
    ): List<RESULT> {
        TODO("Not yet implemented")
    }

    override fun <RESULT : Any, PAYLOAD : CommandUseCasePayload?> commands(
        resultClass: KClass<RESULT>,
        payloads: Collection<PAYLOAD>
    ): List<RESULT> {
        TODO("Not yet implemented")
    }

    override fun <RESULT : Any, PAYLOAD : QueryUseCasePayload> query(
        resultClass: KClass<RESULT>,
        payload: PAYLOAD
    ): List<RESULT> {
        val useCase = getQueryUseCasePort[resultClass, payload::class]
        return useCase(payload)
    }

    override fun <RESULT : Any, PAYLOAD : FetchOneUseCasePayload> fetchOne(
        resultClass: KClass<RESULT>,
        payload: PAYLOAD
    ): Maybe<RESULT> {
        TODO("Not yet implemented")
    }
}