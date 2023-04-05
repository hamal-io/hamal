package io.hamal.lib.ddd.usecase

import kotlin.reflect.KClass


interface GetCommandUseCaseHandlerPort {
    operator fun <RESULT : Any, USE_CASE : CommandUseCase> get(
        resultClass: KClass<RESULT>,
        useCaseClass: KClass<USE_CASE>
    ): CommandUseCaseHandler<RESULT, USE_CASE>
}

interface GetQueryUseCaseHandlerPort {
    operator fun <RESULT : Any, USE_CASE : QueryUseCase> get(
        resultClass: KClass<RESULT>,
        useCaseClass: KClass<USE_CASE>
    ): QueryUseCaseHandler<RESULT, USE_CASE>
}

interface GetFetchOneUseCaseHandlerPort {
    operator fun <RESULT : Any, USE_CASE : FetchOneUseCase> get(
        resultClass: KClass<RESULT>,
        useCaseClass: KClass<USE_CASE>
    ): FetchOneUseCaseHandler<RESULT, USE_CASE>
}