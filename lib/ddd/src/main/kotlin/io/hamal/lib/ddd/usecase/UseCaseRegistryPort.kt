package io.hamal.lib.ddd.usecase

import kotlin.reflect.KClass


interface GetCommandUseCasePort {
    operator fun <RESULT : Any, PAYLOAD : CommandUseCasePayload> get(
        resultClass: KClass<RESULT>,
        payloadClass: KClass<PAYLOAD>
    ): CommandUseCase<RESULT, PAYLOAD>
}

interface GetQueryUseCasePort {
    operator fun <RESULT : Any, PAYLOAD : QueryUseCasePayload> get(
        resultClass: KClass<RESULT>,
        payloadClass: KClass<PAYLOAD>
    ): QueryUseCase<RESULT, PAYLOAD>
}

interface GetFetchOneUseCasePort {
    operator fun <RESULT : Any, PAYLOAD : FetchOneUseCasePayload> get(
        resultClass: KClass<RESULT>,
        payloadClass: KClass<PAYLOAD>
    ): FetchOneUseCase<RESULT, PAYLOAD>
}