package io.hamal.lib.ddd.usecase

import kotlin.reflect.KClass


interface GetCommandUseCasePort {
    operator fun <RESULT : Any, USE_CASE : CommandUseCase> get(
        resultClass: KClass<RESULT>,
        useCaseClass: KClass<USE_CASE>
    ): CommandUseCaseOperation<RESULT, USE_CASE>
}

interface GetQueryUseCasePort {
    operator fun <RESULT : Any, USE_CASE : QueryUseCase> get(
        resultClass: KClass<RESULT>,
        useCaseClass: KClass<USE_CASE>
    ): QueryUseCaseOperation<RESULT, USE_CASE>
}

interface GetFetchOneUseCasePort {
    operator fun <RESULT : Any, USE_CASE : FetchOneUseCase> get(
        resultClass: KClass<RESULT>,
        useCaseClass: KClass<USE_CASE>
    ): FetchOneUseCaseOperation<RESULT, USE_CASE>
}

interface GetUseCasePort : GetCommandUseCasePort, GetQueryUseCasePort, GetFetchOneUseCasePort {}